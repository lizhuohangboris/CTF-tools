package org.apache.coyote.http2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.WebConnection;
import org.apache.coyote.Adapter;
import org.apache.coyote.ProtocolException;
import org.apache.coyote.Request;
import org.apache.coyote.http2.HpackDecoder;
import org.apache.coyote.http2.Http2UpgradeHandler;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/Http2AsyncUpgradeHandler.class */
public class Http2AsyncUpgradeHandler extends Http2UpgradeHandler {
    private static final ByteBuffer[] BYTEBUFFER_ARRAY = new ByteBuffer[0];
    private Throwable error;
    private IOException applicationIOE;
    private CompletionHandler<Long, Void> errorCompletion;
    private CompletionHandler<Long, Void> applicationErrorCompletion;

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void swallowed(int i, FrameType frameType, int i2, int i3) throws IOException {
        super.swallowed(i, frameType, i2, i3);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void incrementWindowSize(int i, int i2) throws Http2Exception {
        super.incrementWindowSize(i, i2);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void goaway(int i, long j, String str) {
        super.goaway(i, j, str);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void pingReceive(byte[] bArr, boolean z) throws IOException {
        super.pingReceive(bArr, z);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void setting(Setting setting, long j) throws ConnectionException {
        super.setting(setting, j);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void reset(int i, long j) throws Http2Exception {
        super.reset(i, j);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void headersEnd(int i) throws ConnectionException {
        super.headersEnd(i);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void reprioritise(int i, int i2, boolean z, int i3) throws Http2Exception {
        super.reprioritise(i, i2, z, i3);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ HpackDecoder.HeaderEmitter headersStart(int i, boolean z) throws Http2Exception, IOException {
        return super.headersStart(i, z);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void swallowedPadding(int i, int i2) throws ConnectionException, IOException {
        super.swallowedPadding(i, i2);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void receivedEndOfStream(int i) throws ConnectionException {
        super.receivedEndOfStream(i);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void endRequestBodyFrame(int i) throws Http2Exception {
        super.endRequestBodyFrame(i);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ ByteBuffer startRequestBodyFrame(int i, int i2) throws Http2Exception {
        return super.startRequestBodyFrame(i, i2);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ HpackDecoder getHpackDecoder() {
        return super.getHpackDecoder();
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Input
    public /* bridge */ /* synthetic */ int getMaxFrameSize() {
        return super.getMaxFrameSize();
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Input
    public /* bridge */ /* synthetic */ boolean fill(boolean z, byte[] bArr, int i, int i2) throws IOException {
        return super.fill(z, bArr, i, i2);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, javax.servlet.http.HttpUpgradeHandler
    public /* bridge */ /* synthetic */ void destroy() {
        super.destroy();
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    public /* bridge */ /* synthetic */ void pause() {
        super.pause();
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    public /* bridge */ /* synthetic */ AbstractEndpoint.Handler.SocketState upgradeDispatch(SocketEvent socketEvent) {
        return super.upgradeDispatch(socketEvent);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    public /* bridge */ /* synthetic */ void setSslSupport(SSLSupport sSLSupport) {
        super.setSslSupport(sSLSupport);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    public /* bridge */ /* synthetic */ void setSocketWrapper(SocketWrapperBase socketWrapperBase) {
        super.setSocketWrapper(socketWrapperBase);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, javax.servlet.http.HttpUpgradeHandler
    public /* bridge */ /* synthetic */ void init(WebConnection webConnection) {
        super.init(webConnection);
    }

    public Http2AsyncUpgradeHandler(Http2Protocol protocol, Adapter adapter, Request coyoteRequest) {
        super(protocol, adapter, coyoteRequest);
        this.error = null;
        this.applicationIOE = null;
        this.errorCompletion = new CompletionHandler<Long, Void>() { // from class: org.apache.coyote.http2.Http2AsyncUpgradeHandler.1
            @Override // java.nio.channels.CompletionHandler
            public void completed(Long result, Void attachment) {
            }

            @Override // java.nio.channels.CompletionHandler
            public void failed(Throwable t, Void attachment) {
                Http2AsyncUpgradeHandler.this.error = t;
            }
        };
        this.applicationErrorCompletion = new CompletionHandler<Long, Void>() { // from class: org.apache.coyote.http2.Http2AsyncUpgradeHandler.2
            @Override // java.nio.channels.CompletionHandler
            public void completed(Long result, Void attachment) {
            }

            @Override // java.nio.channels.CompletionHandler
            public void failed(Throwable t, Void attachment) {
                if (t instanceof IOException) {
                    Http2AsyncUpgradeHandler.this.applicationIOE = (IOException) t;
                }
                Http2AsyncUpgradeHandler.this.error = t;
            }
        };
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    protected Http2Parser getParser(String connectionId) {
        return new Http2AsyncParser(connectionId, this, this, this.socketWrapper, this);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    protected Http2UpgradeHandler.PingManager getPingManager() {
        return new AsyncPingManager();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    public boolean hasAsyncIO() {
        return true;
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    protected void writeSettings() {
        this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(this.localSettings.getSettingsFrameForPending()));
        if (this.error != null) {
            String msg = sm.getString("upgradeHandler.sendPrefaceFail", this.connectionId);
            if (log.isDebugEnabled()) {
                log.debug(msg);
            }
            throw new ProtocolException(msg, this.error);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    public void sendStreamReset(StreamException se) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.rst.debug", this.connectionId, Integer.toString(se.getStreamId()), se.getError()));
        }
        byte[] rstFrame = new byte[13];
        ByteUtil.setThreeBytes(rstFrame, 0, 4);
        rstFrame[3] = FrameType.RST.getIdByte();
        ByteUtil.set31Bits(rstFrame, 5, se.getStreamId());
        ByteUtil.setFourBytes(rstFrame, 9, se.getError().getCode());
        this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(rstFrame));
        handleAsyncException();
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    protected void writeGoAwayFrame(int maxStreamId, long errorCode, byte[] debugMsg) throws IOException {
        byte[] fixedPayload = new byte[8];
        ByteUtil.set31Bits(fixedPayload, 0, maxStreamId);
        ByteUtil.setFourBytes(fixedPayload, 4, errorCode);
        int len = 8;
        if (debugMsg != null) {
            len = 8 + debugMsg.length;
        }
        byte[] payloadLength = new byte[3];
        ByteUtil.setThreeBytes(payloadLength, 0, len);
        if (debugMsg != null) {
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(payloadLength), ByteBuffer.wrap(GOAWAY), ByteBuffer.wrap(fixedPayload), ByteBuffer.wrap(debugMsg));
        } else {
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(payloadLength), ByteBuffer.wrap(GOAWAY), ByteBuffer.wrap(fixedPayload));
        }
        handleAsyncException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    public void writeHeaders(Stream stream, int pushedStreamId, MimeHeaders mimeHeaders, boolean endOfStream, int payloadSize) throws IOException {
        synchronized (this.socketWrapper) {
            AsyncHeaderFrameBuffers headerFrameBuffers = (AsyncHeaderFrameBuffers) doWriteHeaders(stream, pushedStreamId, mimeHeaders, endOfStream, payloadSize);
            if (headerFrameBuffers != null) {
                this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.applicationErrorCompletion, (ByteBuffer[]) headerFrameBuffers.bufs.toArray(BYTEBUFFER_ARRAY));
                handleAsyncException();
            }
        }
        if (endOfStream) {
            stream.sentEndOfStream();
        }
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    protected Http2UpgradeHandler.HeaderFrameBuffers getHeaderFrameBuffers(int initialPayloadSize) {
        return new AsyncHeaderFrameBuffers(initialPayloadSize);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    public void writeBody(Stream stream, ByteBuffer data, int len, boolean finished) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.writeBody", this.connectionId, stream.getIdentifier(), Integer.toString(len)));
        }
        boolean writeable = stream.canWrite();
        byte[] header = new byte[9];
        ByteUtil.setThreeBytes(header, 0, len);
        header[3] = FrameType.DATA.getIdByte();
        if (finished) {
            header[4] = 1;
            stream.sentEndOfStream();
            if (!stream.isActive()) {
                this.activeRemoteStreamCount.decrementAndGet();
            }
        }
        if (writeable) {
            ByteUtil.set31Bits(header, 5, stream.getIdentifier().intValue());
            int orgLimit = data.limit();
            data.limit(data.position() + len);
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.applicationErrorCompletion, ByteBuffer.wrap(header), data);
            data.limit(orgLimit);
            handleAsyncException();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    public void writeWindowUpdate(Stream stream, int increment, boolean applicationInitiated) throws IOException {
        if (!stream.canWrite()) {
            return;
        }
        byte[] frame = new byte[13];
        ByteUtil.setThreeBytes(frame, 0, 4);
        frame[3] = FrameType.WINDOW_UPDATE.getIdByte();
        ByteUtil.set31Bits(frame, 9, increment);
        byte[] frame2 = new byte[13];
        ByteUtil.setThreeBytes(frame2, 0, 4);
        frame2[3] = FrameType.WINDOW_UPDATE.getIdByte();
        ByteUtil.set31Bits(frame2, 9, increment);
        ByteUtil.set31Bits(frame2, 5, stream.getIdentifier().intValue());
        this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(frame), ByteBuffer.wrap(frame2));
        handleAsyncException();
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public void settingsEnd(boolean ack) throws IOException {
        if (ack) {
            if (!this.localSettings.ack()) {
                log.warn(sm.getString("upgradeHandler.unexpectedAck", this.connectionId, getIdentifier()));
            }
        } else {
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(SETTINGS_ACK));
        }
        handleAsyncException();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAsyncException() throws IOException {
        if (this.applicationIOE != null) {
            IOException ioe = this.applicationIOE;
            this.applicationIOE = null;
            handleAppInitiatedIOException(ioe);
        } else if (this.error != null) {
            Throwable error = this.error;
            this.error = null;
            if (error instanceof IOException) {
                throw ((IOException) error);
            }
            throw new IOException(error);
        }
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    protected void processWrites() throws IOException {
        if (this.socketWrapper.isWritePending()) {
            this.socketWrapper.registerWriteInterest();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    public SendfileState processSendfile(SendfileData sendfile) {
        if (sendfile != null) {
            try {
                FileChannel channel = FileChannel.open(sendfile.path, StandardOpenOption.READ);
                sendfile.mappedBuffer = channel.map(FileChannel.MapMode.READ_ONLY, sendfile.pos, sendfile.end - sendfile.pos);
                if (channel != null) {
                    if (0 != 0) {
                        channel.close();
                    } else {
                        channel.close();
                    }
                }
                int reservation = sendfile.end - sendfile.pos > 2147483647L ? Integer.MAX_VALUE : (int) (sendfile.end - sendfile.pos);
                sendfile.streamReservation = sendfile.stream.reserveWindowSize(reservation, true);
                sendfile.connectionReservation = reserveWindowSize(sendfile.stream, sendfile.streamReservation, true);
                int frameSize = Integer.min(getMaxFrameSize(), sendfile.connectionReservation);
                boolean finished = ((long) frameSize) == sendfile.left && sendfile.stream.getCoyoteResponse().getTrailerFields() == null;
                boolean writeable = sendfile.stream.canWrite();
                byte[] header = new byte[9];
                ByteUtil.setThreeBytes(header, 0, frameSize);
                header[3] = FrameType.DATA.getIdByte();
                if (finished) {
                    header[4] = 1;
                    sendfile.stream.sentEndOfStream();
                    if (!sendfile.stream.isActive()) {
                        this.activeRemoteStreamCount.decrementAndGet();
                    }
                }
                if (writeable) {
                    ByteUtil.set31Bits(header, 5, sendfile.stream.getIdentifier().intValue());
                    sendfile.mappedBuffer.limit(sendfile.mappedBuffer.position() + frameSize);
                    this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, sendfile, SocketWrapperBase.COMPLETE_WRITE_WITH_COMPLETION, new SendfileCompletionHandler(), ByteBuffer.wrap(header), sendfile.mappedBuffer);
                    try {
                        handleAsyncException();
                    } catch (IOException e) {
                        return SendfileState.ERROR;
                    }
                }
                return SendfileState.PENDING;
            } catch (IOException e2) {
                return SendfileState.ERROR;
            }
        }
        return SendfileState.DONE;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/Http2AsyncUpgradeHandler$SendfileCompletionHandler.class */
    protected class SendfileCompletionHandler implements CompletionHandler<Long, SendfileData> {
        protected SendfileCompletionHandler() {
        }

        @Override // java.nio.channels.CompletionHandler
        public void completed(Long nBytes, SendfileData sendfile) {
            long bytesWritten = nBytes.longValue() - 9;
            sendfile.left -= bytesWritten;
            if (sendfile.left == 0) {
                try {
                    sendfile.stream.getOutputBuffer().end();
                    return;
                } catch (IOException e) {
                    failed((Throwable) e, sendfile);
                    return;
                }
            }
            sendfile.streamReservation = (int) (sendfile.streamReservation - bytesWritten);
            sendfile.connectionReservation = (int) (sendfile.connectionReservation - bytesWritten);
            sendfile.pos += bytesWritten;
            try {
                if (sendfile.connectionReservation == 0) {
                    if (sendfile.streamReservation == 0) {
                        int reservation = sendfile.end - sendfile.pos > 2147483647L ? Integer.MAX_VALUE : (int) (sendfile.end - sendfile.pos);
                        sendfile.streamReservation = sendfile.stream.reserveWindowSize(reservation, true);
                    }
                    sendfile.connectionReservation = Http2AsyncUpgradeHandler.this.reserveWindowSize(sendfile.stream, sendfile.streamReservation, true);
                }
                int frameSize = Integer.min(Http2AsyncUpgradeHandler.this.getMaxFrameSize(), sendfile.streamReservation);
                boolean finished = ((long) frameSize) == sendfile.left && sendfile.stream.getCoyoteResponse().getTrailerFields() == null;
                boolean writeable = sendfile.stream.canWrite();
                byte[] header = new byte[9];
                ByteUtil.setThreeBytes(header, 0, frameSize);
                header[3] = FrameType.DATA.getIdByte();
                if (finished) {
                    header[4] = 1;
                    sendfile.stream.sentEndOfStream();
                    if (!sendfile.stream.isActive()) {
                        Http2AsyncUpgradeHandler.this.activeRemoteStreamCount.decrementAndGet();
                    }
                }
                if (writeable) {
                    ByteUtil.set31Bits(header, 5, sendfile.stream.getIdentifier().intValue());
                    sendfile.mappedBuffer.limit(sendfile.mappedBuffer.position() + frameSize);
                    Http2AsyncUpgradeHandler.this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, Http2AsyncUpgradeHandler.this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, sendfile, SocketWrapperBase.COMPLETE_WRITE_WITH_COMPLETION, this, ByteBuffer.wrap(header), sendfile.mappedBuffer);
                    try {
                        Http2AsyncUpgradeHandler.this.handleAsyncException();
                    } catch (IOException e2) {
                        failed((Throwable) e2, sendfile);
                    }
                }
            } catch (IOException e3) {
                failed((Throwable) e3, sendfile);
            }
        }

        @Override // java.nio.channels.CompletionHandler
        public void failed(Throwable t, SendfileData sendfile) {
            Http2AsyncUpgradeHandler.this.applicationErrorCompletion.failed(t, null);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/Http2AsyncUpgradeHandler$AsyncPingManager.class */
    protected class AsyncPingManager extends Http2UpgradeHandler.PingManager {
        protected AsyncPingManager() {
            super();
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.PingManager
        public void sendPing(boolean force) throws IOException {
            if (this.initiateDisabled) {
                return;
            }
            long now = System.nanoTime();
            if (force || now - this.lastPingNanoTime > 10000000000L) {
                this.lastPingNanoTime = now;
                byte[] payload = new byte[8];
                int sentSequence = this.sequence + 1;
                this.sequence = sentSequence;
                Http2UpgradeHandler.PingRecord pingRecord = new Http2UpgradeHandler.PingRecord(sentSequence, now);
                this.inflightPings.add(pingRecord);
                ByteUtil.set31Bits(payload, 4, sentSequence);
                Http2AsyncUpgradeHandler.this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, Http2AsyncUpgradeHandler.this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, Http2AsyncUpgradeHandler.this.errorCompletion, ByteBuffer.wrap(Http2UpgradeHandler.PING), ByteBuffer.wrap(payload));
                Http2AsyncUpgradeHandler.this.handleAsyncException();
            }
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.PingManager
        public void receivePing(byte[] payload, boolean ack) throws IOException {
            if (ack) {
                super.receivePing(payload, ack);
                return;
            }
            Http2AsyncUpgradeHandler.this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, Http2AsyncUpgradeHandler.this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, Http2AsyncUpgradeHandler.this.errorCompletion, ByteBuffer.wrap(Http2UpgradeHandler.PING_ACK), ByteBuffer.wrap(payload));
            Http2AsyncUpgradeHandler.this.handleAsyncException();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/Http2AsyncUpgradeHandler$AsyncHeaderFrameBuffers.class */
    private static class AsyncHeaderFrameBuffers implements Http2UpgradeHandler.HeaderFrameBuffers {
        int payloadSize;
        private byte[] header;
        private ByteBuffer payload;
        private final List<ByteBuffer> bufs = new ArrayList();

        public AsyncHeaderFrameBuffers(int initialPayloadSize) {
            this.payloadSize = initialPayloadSize;
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public void startFrame() {
            this.header = new byte[9];
            this.payload = ByteBuffer.allocate(this.payloadSize);
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public void endFrame() throws IOException {
            this.bufs.add(ByteBuffer.wrap(this.header));
            this.bufs.add(this.payload);
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public void endHeaders() throws IOException {
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public byte[] getHeader() {
            return this.header;
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public ByteBuffer getPayload() {
            return this.payload;
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public void expandPayload() {
            this.payloadSize *= 2;
            this.payload = ByteBuffer.allocate(this.payloadSize);
        }
    }
}