package org.apache.coyote.http2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.coyote.ActionCode;
import org.apache.coyote.CloseNowException;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;
import org.apache.coyote.http2.HpackDecoder;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.Host;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.net.WriteBuffer;
import org.apache.tomcat.util.res.StringManager;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/Stream.class */
public class Stream extends AbstractStream implements HpackDecoder.HeaderEmitter {
    private static final Log log = LogFactory.getLog(Stream.class);
    private static final StringManager sm = StringManager.getManager(Stream.class);
    private static final int HEADER_STATE_START = 0;
    private static final int HEADER_STATE_PSEUDO = 1;
    private static final int HEADER_STATE_REGULAR = 2;
    private static final int HEADER_STATE_TRAILER = 3;
    private static final MimeHeaders ACK_HEADERS;
    private volatile int weight;
    private volatile long contentLengthReceived;
    private final Http2UpgradeHandler handler;
    private final StreamStateMachine state;
    private int headerState;
    private StreamException headerException;
    private final Request coyoteRequest;
    private StringBuilder cookieHeader;
    private final Response coyoteResponse;
    private final StreamInputBuffer inputBuffer;
    private final StreamOutputBuffer streamOutputBuffer;
    private final Http2OutputBuffer http2OutputBuffer;

    static {
        Response response = new Response();
        response.setStatus(100);
        StreamProcessor.prepareHeaders(null, response, true, null, null);
        ACK_HEADERS = response.getMimeHeaders();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Stream(Integer identifier, Http2UpgradeHandler handler) {
        this(identifier, handler, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Stream(Integer identifier, Http2UpgradeHandler handler, Request coyoteRequest) {
        super(identifier);
        this.weight = 16;
        this.contentLengthReceived = 0L;
        this.headerState = 0;
        this.headerException = null;
        this.cookieHeader = null;
        this.coyoteResponse = new Response();
        this.streamOutputBuffer = new StreamOutputBuffer();
        this.http2OutputBuffer = new Http2OutputBuffer(this.coyoteResponse, this.streamOutputBuffer);
        this.handler = handler;
        handler.addChild(this);
        setWindowSize(handler.getRemoteSettings().getInitialWindowSize());
        this.state = new StreamStateMachine(this);
        if (coyoteRequest == null) {
            this.coyoteRequest = new Request();
            this.inputBuffer = new StreamInputBuffer();
            this.coyoteRequest.setInputBuffer(this.inputBuffer);
        } else {
            this.coyoteRequest = coyoteRequest;
            this.inputBuffer = null;
            this.state.receivedStartOfHeaders();
            this.state.receivedEndOfStream();
        }
        this.coyoteRequest.setSendfile(handler.hasAsyncIO() && handler.getProtocol().getUseSendfile());
        this.coyoteResponse.setOutputBuffer(this.http2OutputBuffer);
        this.coyoteRequest.setResponse(this.coyoteResponse);
        this.coyoteRequest.protocol().setString("HTTP/2.0");
        if (this.coyoteRequest.getStartTime() < 0) {
            this.coyoteRequest.setStartTime(System.currentTimeMillis());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void rePrioritise(AbstractStream parent, boolean exclusive, int weight) {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("stream.reprioritisation.debug", getConnectionId(), getIdentifier(), Boolean.toString(exclusive), parent.getIdentifier(), Integer.toString(weight)));
        }
        if (isDescendant(parent)) {
            parent.detachFromParent();
            getParentStream().addChild((Stream) parent);
        }
        if (exclusive) {
            Iterator<Stream> parentsChildren = parent.getChildStreams().iterator();
            while (parentsChildren.hasNext()) {
                Stream parentsChild = parentsChildren.next();
                parentsChildren.remove();
                addChild(parentsChild);
            }
        }
        detachFromParent();
        parent.addChild(this);
        this.weight = weight;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void rePrioritise(AbstractStream parent, int weight) {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("stream.reprioritisation.debug", getConnectionId(), getIdentifier(), Boolean.FALSE, parent.getIdentifier(), Integer.toString(weight)));
        }
        parent.addChild(this);
        this.weight = weight;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void receiveReset(long errorCode) {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("stream.reset.receive", getConnectionId(), getIdentifier(), Long.toString(errorCode)));
        }
        this.state.receivedReset();
        if (this.inputBuffer != null) {
            this.inputBuffer.receiveReset();
        }
        synchronized (this) {
            notifyAll();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void checkState(FrameType frameType) throws Http2Exception {
        this.state.checkFrameType(frameType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.AbstractStream
    public final synchronized void incrementWindowSize(int windowSizeIncrement) throws Http2Exception {
        boolean notify = getWindowSize() < 1;
        super.incrementWindowSize(windowSizeIncrement);
        if (notify && getWindowSize() > 0) {
            if (this.coyoteResponse.getWriteListener() == null) {
                notifyAll();
                return;
            }
            this.coyoteResponse.action(ActionCode.DISPATCH_WRITE, null);
            this.coyoteResponse.action(ActionCode.DISPATCH_EXECUTE, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized int reserveWindowSize(int reservation, boolean block) throws IOException {
        int allocation;
        long windowSize = getWindowSize();
        while (true) {
            long windowSize2 = windowSize;
            if (windowSize2 < 1) {
                if (!canWrite()) {
                    throw new CloseNowException(sm.getString("stream.notWritable", getConnectionId(), getIdentifier()));
                }
                if (block) {
                    try {
                        wait();
                        windowSize = getWindowSize();
                    } catch (InterruptedException e) {
                        throw new IOException(e);
                    }
                } else {
                    return 0;
                }
            } else {
                if (windowSize2 < reservation) {
                    allocation = (int) windowSize2;
                } else {
                    allocation = reservation;
                }
                decrementWindowSize(allocation);
                return allocation;
            }
        }
    }

    @Override // org.apache.coyote.http2.HpackDecoder.HeaderEmitter
    public final void emitHeader(String name, String value) throws HpackException {
        String uri;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("stream.header.debug", getConnectionId(), getIdentifier(), name, value));
        }
        if (!name.toLowerCase(Locale.US).equals(name)) {
            throw new HpackException(sm.getString("stream.header.case", getConnectionId(), getIdentifier(), name));
        }
        if ("connection".equals(name)) {
            throw new HpackException(sm.getString("stream.header.connection", getConnectionId(), getIdentifier()));
        }
        if ("te".equals(name) && !"trailers".equals(value)) {
            throw new HpackException(sm.getString("stream.header.te", getConnectionId(), getIdentifier(), value));
        }
        if (this.headerException != null) {
            return;
        }
        boolean pseudoHeader = name.charAt(0) == ':';
        if (pseudoHeader && this.headerState != 1) {
            this.headerException = new StreamException(sm.getString("stream.header.unexpectedPseudoHeader", getConnectionId(), getIdentifier(), name), Http2Error.PROTOCOL_ERROR, getIdentifier().intValue());
            return;
        }
        if (this.headerState == 1 && !pseudoHeader) {
            this.headerState = 2;
        }
        boolean z = true;
        switch (name.hashCode()) {
            case -1354757532:
                if (name.equals("cookie")) {
                    z = true;
                    break;
                }
                break;
            case -1332238263:
                if (name.equals(":authority")) {
                    z = true;
                    break;
                }
                break;
            case -1141949029:
                if (name.equals(":method")) {
                    z = false;
                    break;
                }
                break;
            case -972381601:
                if (name.equals(":scheme")) {
                    z = true;
                    break;
                }
                break;
            case 56997727:
                if (name.equals(":path")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (this.coyoteRequest.method().isNull()) {
                    this.coyoteRequest.method().setString(value);
                    return;
                }
                throw new HpackException(sm.getString("stream.header.duplicate", getConnectionId(), getIdentifier(), ":method"));
            case true:
                if (this.coyoteRequest.scheme().isNull()) {
                    this.coyoteRequest.scheme().setString(value);
                    return;
                }
                throw new HpackException(sm.getString("stream.header.duplicate", getConnectionId(), getIdentifier(), ":scheme"));
            case true:
                if (!this.coyoteRequest.requestURI().isNull()) {
                    throw new HpackException(sm.getString("stream.header.duplicate", getConnectionId(), getIdentifier(), ":path"));
                }
                if (value.length() == 0) {
                    throw new HpackException(sm.getString("stream.header.noPath", getConnectionId(), getIdentifier()));
                }
                int queryStart = value.indexOf(63);
                if (queryStart == -1) {
                    uri = value;
                } else {
                    uri = value.substring(0, queryStart);
                    String query = value.substring(queryStart + 1);
                    this.coyoteRequest.queryString().setString(query);
                }
                byte[] uriBytes = uri.getBytes(StandardCharsets.ISO_8859_1);
                this.coyoteRequest.requestURI().setBytes(uriBytes, 0, uriBytes.length);
                return;
            case true:
                if (this.coyoteRequest.serverName().isNull()) {
                    try {
                        int i = Host.parse(value);
                        if (i > -1) {
                            this.coyoteRequest.serverName().setString(value.substring(0, i));
                            this.coyoteRequest.setServerPort(Integer.parseInt(value.substring(i + 1)));
                            return;
                        }
                        this.coyoteRequest.serverName().setString(value);
                        return;
                    } catch (IllegalArgumentException e) {
                        throw new HpackException(sm.getString("stream.header.invalid", getConnectionId(), getIdentifier(), ":authority", value));
                    }
                }
                throw new HpackException(sm.getString("stream.header.duplicate", getConnectionId(), getIdentifier(), ":authority"));
            case true:
                if (this.cookieHeader == null) {
                    this.cookieHeader = new StringBuilder();
                } else {
                    this.cookieHeader.append("; ");
                }
                this.cookieHeader.append(value);
                return;
            default:
                if (this.headerState != 3 || this.handler.getProtocol().isTrailerHeaderAllowed(name)) {
                    if ("expect".equals(name) && "100-continue".equals(value)) {
                        this.coyoteRequest.setExpectation(true);
                    }
                    if (pseudoHeader) {
                        this.headerException = new StreamException(sm.getString("stream.header.unknownPseudoHeader", getConnectionId(), getIdentifier(), name), Http2Error.PROTOCOL_ERROR, getIdentifier().intValue());
                    }
                    if (this.headerState == 3) {
                        this.coyoteRequest.getTrailerFields().put(name, value);
                        return;
                    } else {
                        this.coyoteRequest.getMimeHeaders().addValue(name).setString(value);
                        return;
                    }
                }
                return;
        }
    }

    @Override // org.apache.coyote.http2.HpackDecoder.HeaderEmitter
    public void setHeaderException(StreamException streamException) {
        if (this.headerException == null) {
            this.headerException = streamException;
        }
    }

    @Override // org.apache.coyote.http2.HpackDecoder.HeaderEmitter
    public void validateHeaders() throws StreamException {
        if (this.headerException == null) {
            return;
        }
        throw this.headerException;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean receivedEndOfHeaders() throws ConnectionException {
        if (this.coyoteRequest.method().isNull() || this.coyoteRequest.scheme().isNull() || this.coyoteRequest.requestURI().isNull()) {
            throw new ConnectionException(sm.getString("stream.header.required", getConnectionId(), getIdentifier()), Http2Error.PROTOCOL_ERROR);
        }
        if (this.cookieHeader != null) {
            this.coyoteRequest.getMimeHeaders().addValue("cookie").setString(this.cookieHeader.toString());
        }
        return this.headerState == 2 || this.headerState == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void writeHeaders() throws IOException {
        boolean endOfStream = this.streamOutputBuffer.hasNoBody() && this.coyoteResponse.getTrailerFields() == null;
        this.handler.writeHeaders(this, 0, this.coyoteResponse.getMimeHeaders(), endOfStream, 1024);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addOutputFilter(OutputFilter filter) {
        this.http2OutputBuffer.addFilter(filter);
    }

    final void writeTrailers() throws IOException {
        Supplier<Map<String, String>> supplier = this.coyoteResponse.getTrailerFields();
        if (supplier == null) {
            return;
        }
        MimeHeaders mimeHeaders = this.coyoteResponse.getMimeHeaders();
        mimeHeaders.recycle();
        Map<String, String> headerMap = supplier.get();
        if (headerMap == null) {
            headerMap = Collections.emptyMap();
        }
        for (Map.Entry<String, String> headerEntry : headerMap.entrySet()) {
            MessageBytes mb = mimeHeaders.addValue(headerEntry.getKey());
            mb.setString(headerEntry.getValue());
        }
        this.handler.writeHeaders(this, 0, mimeHeaders, true, 1024);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void writeAck() throws IOException {
        this.handler.writeHeaders(this, 0, ACK_HEADERS, false, 64);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.AbstractStream
    public final String getConnectionId() {
        return this.handler.getConnectionId();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.AbstractStream
    public final int getWeight() {
        return this.weight;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Request getCoyoteRequest() {
        return this.coyoteRequest;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Response getCoyoteResponse() {
        return this.coyoteResponse;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ByteBuffer getInputByteBuffer() {
        return this.inputBuffer.getInBuffer();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void receivedStartOfHeaders(boolean headersEndStream) throws Http2Exception {
        if (this.headerState == 0) {
            this.headerState = 1;
            this.handler.getHpackDecoder().setMaxHeaderCount(this.handler.getProtocol().getMaxHeaderCount());
            this.handler.getHpackDecoder().setMaxHeaderSize(this.handler.getProtocol().getMaxHeaderSize());
        } else if (this.headerState == 1 || this.headerState == 2) {
            if (headersEndStream) {
                this.headerState = 3;
                this.handler.getHpackDecoder().setMaxHeaderCount(this.handler.getProtocol().getMaxTrailerCount());
                this.handler.getHpackDecoder().setMaxHeaderSize(this.handler.getProtocol().getMaxTrailerSize());
            } else {
                throw new ConnectionException(sm.getString("stream.trailerHeader.noEndOfStream", getConnectionId(), getIdentifier()), Http2Error.PROTOCOL_ERROR);
            }
        }
        this.state.receivedStartOfHeaders();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void receivedData(int payloadSize) throws ConnectionException {
        this.contentLengthReceived += payloadSize;
        long contentLengthHeader = this.coyoteRequest.getContentLengthLong();
        if (contentLengthHeader > -1 && this.contentLengthReceived > contentLengthHeader) {
            throw new ConnectionException(sm.getString("stream.header.contentLength", getConnectionId(), getIdentifier(), Long.valueOf(contentLengthHeader), Long.valueOf(this.contentLengthReceived)), Http2Error.PROTOCOL_ERROR);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void receivedEndOfStream() throws ConnectionException {
        long contentLengthHeader = this.coyoteRequest.getContentLengthLong();
        if (contentLengthHeader > -1 && this.contentLengthReceived != contentLengthHeader) {
            throw new ConnectionException(sm.getString("stream.header.contentLength", getConnectionId(), getIdentifier(), Long.valueOf(contentLengthHeader), Long.valueOf(this.contentLengthReceived)), Http2Error.PROTOCOL_ERROR);
        }
        this.state.receivedEndOfStream();
        if (this.inputBuffer == null) {
            return;
        }
        this.inputBuffer.notifyEof();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void sentEndOfStream() {
        this.streamOutputBuffer.endOfStreamSent = true;
        this.state.sentEndOfStream();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isReady() {
        return this.streamOutputBuffer.isReady();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean flush(boolean block) throws IOException {
        return this.streamOutputBuffer.flush(block);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final StreamInputBuffer getInputBuffer() {
        return this.inputBuffer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final HttpOutputBuffer getOutputBuffer() {
        return this.http2OutputBuffer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void sentPushPromise() {
        this.state.sentPushPromise();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isActive() {
        return this.state.isActive();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean canWrite() {
        return this.state.canWrite();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isClosedFinal() {
        return this.state.isClosedFinal();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void closeIfIdle() {
        this.state.closeIfIdle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean isInputFinished() {
        return !this.state.isFrameTypePermitted(FrameType.DATA);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void close(Http2Exception http2Exception) {
        if (http2Exception instanceof StreamException) {
            try {
                StreamException se = (StreamException) http2Exception;
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("stream.reset.send", getConnectionId(), getIdentifier(), se.getError()));
                }
                this.state.sendReset();
                this.handler.sendStreamReset(se);
                return;
            } catch (IOException ioe) {
                ConnectionException ce = new ConnectionException(sm.getString("stream.reset.fail"), Http2Error.PROTOCOL_ERROR);
                ce.initCause(ioe);
                this.handler.closeConnection(ce);
                return;
            }
        }
        this.handler.closeConnection(http2Exception);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isPushSupported() {
        return this.handler.getRemoteSettings().getEnablePush();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void push(Request request) throws IOException {
        if (!isPushSupported() || getIdentifier().intValue() % 2 == 0) {
            return;
        }
        request.getMimeHeaders().addValue(":method").duplicate(request.method());
        request.getMimeHeaders().addValue(":scheme").duplicate(request.scheme());
        StringBuilder path = new StringBuilder(request.requestURI().toString());
        if (!request.queryString().isNull()) {
            path.append('?');
            path.append(request.queryString().toString());
        }
        request.getMimeHeaders().addValue(":path").setString(path.toString());
        if ((!request.scheme().equals("http") || request.getServerPort() != 80) && (!request.scheme().equals("https") || request.getServerPort() != 443)) {
            request.getMimeHeaders().addValue(":authority").setString(request.serverName().getString() + ":" + request.getServerPort());
        } else {
            request.getMimeHeaders().addValue(":authority").duplicate(request.serverName());
        }
        push(this.handler, request, this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isTrailerFieldsReady() {
        return !this.state.canRead();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isTrailerFieldsSupported() {
        return !this.streamOutputBuffer.endOfStreamSent;
    }

    private static void push(Http2UpgradeHandler handler, Request request, Stream stream) throws IOException {
        if (org.apache.coyote.Constants.IS_SECURITY_ENABLED) {
            try {
                AccessController.doPrivileged(new PrivilegedPush(handler, request, stream));
                return;
            } catch (PrivilegedActionException ex) {
                Exception e = ex.getException();
                if (e instanceof IOException) {
                    throw ((IOException) e);
                }
                throw new IOException(ex);
            }
        }
        handler.push(request, stream);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/Stream$PrivilegedPush.class */
    public static class PrivilegedPush implements PrivilegedExceptionAction<Void> {
        private final Http2UpgradeHandler handler;
        private final Request request;
        private final Stream stream;

        public PrivilegedPush(Http2UpgradeHandler handler, Request request, Stream stream) {
            this.handler = handler;
            this.request = request;
            this.stream = stream;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws IOException {
            this.handler.push(this.request, this.stream);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/Stream$StreamOutputBuffer.class */
    public class StreamOutputBuffer implements HttpOutputBuffer, WriteBuffer.Sink {
        private final ByteBuffer buffer = ByteBuffer.allocate(8192);
        private final WriteBuffer writeBuffer = new WriteBuffer(32768);
        private volatile long written = 0;
        private volatile int streamReservation = 0;
        private volatile boolean closed = false;
        private volatile boolean endOfStreamSent = false;

        StreamOutputBuffer() {
        }

        @Override // org.apache.coyote.OutputBuffer
        public final synchronized int doWrite(ByteBuffer chunk) throws IOException {
            if (this.closed) {
                throw new IllegalStateException(Stream.sm.getString("stream.closed", Stream.this.getConnectionId(), Stream.this.getIdentifier()));
            }
            int totalThisTime = 0;
            if (this.writeBuffer.isEmpty()) {
                int chunkLimit = chunk.limit();
                while (true) {
                    if (chunk.remaining() <= 0) {
                        break;
                    }
                    int thisTime = Math.min(this.buffer.remaining(), chunk.remaining());
                    chunk.limit(chunk.position() + thisTime);
                    this.buffer.put(chunk);
                    chunk.limit(chunkLimit);
                    totalThisTime += thisTime;
                    if (chunk.remaining() > 0 && !this.buffer.hasRemaining()) {
                        if (flush(true, Stream.this.coyoteResponse.getWriteListener() == null)) {
                            totalThisTime += chunk.remaining();
                            this.writeBuffer.add(chunk);
                            break;
                        }
                    }
                }
            } else {
                totalThisTime = chunk.remaining();
                this.writeBuffer.add(chunk);
            }
            this.written += totalThisTime;
            return totalThisTime;
        }

        final synchronized boolean flush(boolean block) throws IOException {
            boolean dataLeft = this.buffer.position() > 0;
            boolean flushed = false;
            if (dataLeft) {
                dataLeft = flush(false, block);
                flushed = true;
            }
            if (!dataLeft) {
                if (this.writeBuffer.isEmpty()) {
                    if (!flushed) {
                        dataLeft = flush(false, block);
                    }
                } else {
                    dataLeft = this.writeBuffer.write(this, block);
                }
            }
            return dataLeft;
        }

        private final synchronized boolean flush(boolean writeInProgress, boolean block) throws IOException {
            if (Stream.log.isDebugEnabled()) {
                Stream.log.debug(Stream.sm.getString("stream.outputBuffer.flush.debug", Stream.this.getConnectionId(), Stream.this.getIdentifier(), Integer.toString(this.buffer.position()), Boolean.toString(writeInProgress), Boolean.toString(this.closed)));
            }
            if (this.buffer.position() == 0) {
                if (this.closed && !this.endOfStreamSent) {
                    Stream.this.handler.writeBody(Stream.this, this.buffer, 0, Stream.this.coyoteResponse.getTrailerFields() == null);
                    return false;
                }
                return false;
            }
            this.buffer.flip();
            int left = this.buffer.remaining();
            while (left > 0) {
                if (this.streamReservation == 0) {
                    this.streamReservation = Stream.this.reserveWindowSize(left, block);
                    if (this.streamReservation == 0) {
                        this.writeBuffer.add(this.buffer);
                        this.buffer.clear();
                        return true;
                    }
                }
                while (this.streamReservation > 0) {
                    int connectionReservation = Stream.this.handler.reserveWindowSize(Stream.this, this.streamReservation, block);
                    if (connectionReservation == 0) {
                        this.writeBuffer.add(this.buffer);
                        this.buffer.clear();
                        return true;
                    }
                    Stream.this.handler.writeBody(Stream.this, this.buffer, connectionReservation, !writeInProgress && this.closed && left == connectionReservation && Stream.this.coyoteResponse.getTrailerFields() == null);
                    this.streamReservation -= connectionReservation;
                    left -= connectionReservation;
                }
            }
            this.buffer.clear();
            return false;
        }

        final synchronized boolean isReady() {
            if (Stream.this.getWindowSize() > 0 && Stream.this.handler.getWindowSize() > 0 && this.writeBuffer.isEmpty()) {
                return true;
            }
            return false;
        }

        @Override // org.apache.coyote.OutputBuffer
        public final long getBytesWritten() {
            return this.written;
        }

        @Override // org.apache.coyote.http11.HttpOutputBuffer
        public final void end() throws IOException {
            this.closed = true;
            flush(true);
            Stream.this.writeTrailers();
        }

        final boolean hasNoBody() {
            return this.written == 0 && this.closed;
        }

        @Override // org.apache.coyote.http11.HttpOutputBuffer
        public void flush() throws IOException {
            flush(Stream.this.getCoyoteResponse().getWriteListener() == null);
        }

        @Override // org.apache.tomcat.util.net.WriteBuffer.Sink
        public synchronized boolean writeFromBuffer(ByteBuffer src, boolean blocking) throws IOException {
            int chunkLimit = src.limit();
            while (src.remaining() > 0) {
                int thisTime = Math.min(this.buffer.remaining(), src.remaining());
                src.limit(src.position() + thisTime);
                this.buffer.put(src);
                src.limit(chunkLimit);
                if (flush(false, blocking)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/Stream$StreamInputBuffer.class */
    public class StreamInputBuffer implements InputBuffer {
        private byte[] outBuffer;
        private volatile ByteBuffer inBuffer;
        private volatile boolean readInterest;
        private boolean reset = false;

        StreamInputBuffer() {
        }

        @Override // org.apache.coyote.InputBuffer
        public final int doRead(ApplicationBufferHandler applicationBufferHandler) throws IOException {
            ensureBuffersExist();
            synchronized (this.inBuffer) {
                boolean canRead = false;
                while (this.inBuffer.position() == 0) {
                    boolean z = Stream.this.isActive() && !Stream.this.isInputFinished();
                    canRead = z;
                    if (!z) {
                        break;
                    }
                    try {
                        if (Stream.log.isDebugEnabled()) {
                            Stream.log.debug(Stream.sm.getString("stream.inputBuffer.empty"));
                        }
                        this.inBuffer.wait();
                        if (this.reset) {
                            throw new IOException(Stream.sm.getString("stream.inputBuffer.reset"));
                        }
                    } catch (InterruptedException e) {
                        throw new IOException(e);
                    }
                }
                if (this.inBuffer.position() > 0) {
                    this.inBuffer.flip();
                    int written = this.inBuffer.remaining();
                    if (Stream.log.isDebugEnabled()) {
                        Stream.log.debug(Stream.sm.getString("stream.inputBuffer.copy", Integer.toString(written)));
                    }
                    this.inBuffer.get(this.outBuffer, 0, written);
                    this.inBuffer.clear();
                    applicationBufferHandler.setByteBuffer(ByteBuffer.wrap(this.outBuffer, 0, written));
                    Stream.this.handler.writeWindowUpdate(Stream.this, written, true);
                    return written;
                } else if (!canRead) {
                    return -1;
                } else {
                    throw new IllegalStateException();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final void registerReadInterest() {
            ensureBuffersExist();
            synchronized (this.inBuffer) {
                this.readInterest = true;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final synchronized boolean isRequestBodyFullyRead() {
            return (this.inBuffer == null || this.inBuffer.position() == 0) && Stream.this.isInputFinished();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final synchronized int available() {
            if (this.inBuffer == null) {
                return 0;
            }
            return this.inBuffer.position();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final synchronized boolean onDataAvailable() {
            if (this.readInterest) {
                if (Stream.log.isDebugEnabled()) {
                    Stream.log.debug(Stream.sm.getString("stream.inputBuffer.dispatch"));
                }
                this.readInterest = false;
                Stream.this.coyoteRequest.action(ActionCode.DISPATCH_READ, null);
                Stream.this.coyoteRequest.action(ActionCode.DISPATCH_EXECUTE, null);
                return true;
            }
            if (Stream.log.isDebugEnabled()) {
                Stream.log.debug(Stream.sm.getString("stream.inputBuffer.signal"));
            }
            synchronized (this.inBuffer) {
                this.inBuffer.notifyAll();
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final ByteBuffer getInBuffer() {
            ensureBuffersExist();
            return this.inBuffer;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final synchronized void insertReplayedBody(ByteChunk body) {
            this.inBuffer = ByteBuffer.wrap(body.getBytes(), body.getOffset(), body.getLength());
        }

        private final void ensureBuffersExist() {
            if (this.inBuffer == null) {
                int size = Stream.this.handler.getLocalSettings().getInitialWindowSize();
                synchronized (this) {
                    if (this.inBuffer == null) {
                        this.inBuffer = ByteBuffer.allocate(size);
                        this.outBuffer = new byte[size];
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void receiveReset() {
            if (this.inBuffer != null) {
                synchronized (this.inBuffer) {
                    this.reset = true;
                    this.inBuffer.notifyAll();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void notifyEof() {
            if (this.inBuffer != null) {
                synchronized (this.inBuffer) {
                    this.inBuffer.notifyAll();
                }
            }
        }
    }
}