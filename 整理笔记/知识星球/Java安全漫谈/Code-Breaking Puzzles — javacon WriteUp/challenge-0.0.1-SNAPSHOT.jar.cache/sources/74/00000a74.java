package org.apache.coyote.http2;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.apache.coyote.AbstractProcessor;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Adapter;
import org.apache.coyote.ContainerThreadMarker;
import org.apache.coyote.ErrorState;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.coyote.http11.filters.GzipOutputFilter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.DispatchType;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/StreamProcessor.class */
public class StreamProcessor extends AbstractProcessor {
    private static final Log log = LogFactory.getLog(StreamProcessor.class);
    private static final StringManager sm = StringManager.getManager(StreamProcessor.class);
    private final Http2UpgradeHandler handler;
    private final Stream stream;
    private SendfileData sendfileData;
    private SendfileState sendfileState;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StreamProcessor(Http2UpgradeHandler handler, Stream stream, Adapter adapter, SocketWrapperBase<?> socketWrapper) {
        super(adapter, stream.getCoyoteRequest(), stream.getCoyoteResponse());
        this.sendfileData = null;
        this.sendfileState = null;
        this.handler = handler;
        this.stream = stream;
        setSocketWrapper(socketWrapper);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void process(SocketEvent event) {
        try {
            synchronized (this) {
                ContainerThreadMarker.set();
                AbstractEndpoint.Handler.SocketState socketState = AbstractEndpoint.Handler.SocketState.CLOSED;
                try {
                    try {
                        AbstractEndpoint.Handler.SocketState state = process(this.socketWrapper, event);
                        if (state == AbstractEndpoint.Handler.SocketState.CLOSED) {
                            if (!getErrorState().isConnectionIoAllowed()) {
                                this.stream.close(new ConnectionException(sm.getString("streamProcessor.error.connection", this.stream.getConnectionId(), this.stream.getIdentifier()), Http2Error.INTERNAL_ERROR));
                            } else if (!getErrorState().isIoAllowed()) {
                                StreamException se = new StreamException(sm.getString("streamProcessor.error.stream", this.stream.getConnectionId(), this.stream.getIdentifier()), Http2Error.INTERNAL_ERROR, this.stream.getIdentifier().intValue());
                                this.stream.close(se);
                            }
                        }
                        ContainerThreadMarker.clear();
                    } catch (Throwable th) {
                        ContainerThreadMarker.clear();
                        throw th;
                    }
                } catch (Exception e) {
                    String msg = sm.getString("streamProcessor.error.connection", this.stream.getConnectionId(), this.stream.getIdentifier());
                    if (log.isDebugEnabled()) {
                        log.debug(msg, e);
                    }
                    ConnectionException ce = new ConnectionException(msg, Http2Error.INTERNAL_ERROR);
                    ce.initCause(e);
                    this.stream.close(ce);
                    ContainerThreadMarker.clear();
                }
            }
        } finally {
            this.handler.executeQueuedStream();
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void prepareResponse() throws IOException {
        this.response.setCommitted(true);
        if (this.handler.hasAsyncIO() && this.handler.getProtocol().getUseSendfile()) {
            prepareSendfile();
        }
        prepareHeaders(this.request, this.response, this.sendfileData == null, this.handler.getProtocol(), this.stream);
        this.stream.writeHeaders();
    }

    private void prepareSendfile() {
        String fileName = (String) this.stream.getCoyoteRequest().getAttribute("org.apache.tomcat.sendfile.filename");
        if (fileName != null) {
            this.sendfileData = new SendfileData();
            this.sendfileData.path = new File(fileName).toPath();
            this.sendfileData.pos = ((Long) this.stream.getCoyoteRequest().getAttribute("org.apache.tomcat.sendfile.start")).longValue();
            this.sendfileData.end = ((Long) this.stream.getCoyoteRequest().getAttribute("org.apache.tomcat.sendfile.end")).longValue();
            this.sendfileData.left = this.sendfileData.end - this.sendfileData.pos;
            this.sendfileData.stream = this.stream;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void prepareHeaders(Request coyoteRequest, Response coyoteResponse, boolean noSendfile, Http2Protocol protocol, Stream stream) {
        MimeHeaders headers = coyoteResponse.getMimeHeaders();
        int statusCode = coyoteResponse.getStatus();
        headers.addValue(":status").setString(Integer.toString(statusCode));
        if (statusCode >= 200 && statusCode != 205 && statusCode != 304) {
            String contentType = coyoteResponse.getContentType();
            if (contentType != null) {
                headers.setValue("content-type").setString(contentType);
            }
            String contentLanguage = coyoteResponse.getContentLanguage();
            if (contentLanguage != null) {
                headers.setValue("content-language").setString(contentLanguage);
            }
        }
        if (statusCode >= 200 && headers.getValue(SpringInputGeneralFieldTagProcessor.DATE_INPUT_TYPE_ATTR_VALUE) == null) {
            headers.addValue(SpringInputGeneralFieldTagProcessor.DATE_INPUT_TYPE_ATTR_VALUE).setString(FastHttpDateFormat.getCurrentDate());
        }
        if (noSendfile && protocol != null && protocol.useCompression(coyoteRequest, coyoteResponse)) {
            stream.addOutputFilter(new GzipOutputFilter());
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void finishResponse() throws IOException {
        this.sendfileState = this.handler.processSendfile(this.sendfileData);
        if (this.sendfileState != SendfileState.PENDING) {
            this.stream.getOutputBuffer().end();
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void ack() {
        if (!this.response.isCommitted() && this.request.hasExpectation()) {
            try {
                this.stream.writeAck();
            } catch (IOException ioe) {
                setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
            }
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void flush() throws IOException {
        this.stream.getOutputBuffer().flush();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final int available(boolean doRead) {
        return this.stream.getInputBuffer().available();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void setRequestBody(ByteChunk body) {
        this.stream.getInputBuffer().insertReplayedBody(body);
        try {
            this.stream.receivedEndOfStream();
        } catch (ConnectionException e) {
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void setSwallowResponse() {
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void disableSwallowRequest() {
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected void processSocketEvent(SocketEvent event, boolean dispatch) {
        if (dispatch) {
            this.handler.processStreamOnContainerThread(this, event);
        } else {
            process(event);
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean isRequestBodyFullyRead() {
        return this.stream.getInputBuffer().isRequestBodyFullyRead();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void registerReadInterest() {
        this.stream.getInputBuffer().registerReadInterest();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean isReady() {
        return this.stream.isReady();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void executeDispatches() {
        Iterator<DispatchType> dispatches = getIteratorAndClearDispatches();
        while (dispatches != null && dispatches.hasNext()) {
            DispatchType dispatchType = dispatches.next();
            processSocketEvent(dispatchType.getSocketStatus(), true);
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean isPushSupported() {
        return this.stream.isPushSupported();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void doPush(Request pushTarget) {
        try {
            this.stream.push(pushTarget);
        } catch (IOException ioe) {
            setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
            this.response.setErrorException(ioe);
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected boolean isTrailerFieldsReady() {
        return this.stream.isTrailerFieldsReady();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected boolean isTrailerFieldsSupported() {
        return this.stream.isTrailerFieldsSupported();
    }

    @Override // org.apache.coyote.AbstractProcessor, org.apache.coyote.Processor
    public final void recycle() {
        setSocketWrapper(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.AbstractProcessorLight
    public final Log getLog() {
        return log;
    }

    @Override // org.apache.coyote.Processor
    public final void pause() {
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public final AbstractEndpoint.Handler.SocketState service(SocketWrapperBase<?> socket) throws IOException {
        try {
            this.adapter.service(this.request, this.response);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("streamProcessor.service.error"), e);
            }
            this.response.setStatus(500);
            setErrorState(ErrorState.CLOSE_NOW, e);
        }
        if (this.sendfileState == SendfileState.PENDING) {
            return AbstractEndpoint.Handler.SocketState.SENDFILE;
        }
        if (getErrorState().isError()) {
            action(ActionCode.CLOSE, null);
            this.request.updateCounters();
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        } else if (isAsync()) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        } else {
            action(ActionCode.CLOSE, null);
            this.request.updateCounters();
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean flushBufferedWrite() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("streamProcessor.flushBufferedWrite.entry", this.stream.getConnectionId(), this.stream.getIdentifier()));
        }
        if (this.stream.flush(false)) {
            if (this.stream.isReady()) {
                throw new IllegalStateException();
            }
            return true;
        }
        return false;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final AbstractEndpoint.Handler.SocketState dispatchEndRequest() {
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }
}