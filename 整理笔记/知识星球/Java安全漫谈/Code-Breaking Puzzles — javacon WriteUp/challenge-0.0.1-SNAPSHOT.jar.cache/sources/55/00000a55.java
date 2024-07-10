package org.apache.coyote.http2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import org.apache.coyote.http2.Http2Parser;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/Http2AsyncParser.class */
public class Http2AsyncParser extends Http2Parser {
    private final SocketWrapperBase<?> socketWrapper;
    private final Http2AsyncUpgradeHandler upgradeHandler;
    private Throwable error;
    private final ByteBuffer header;
    private final ByteBuffer framePaylod;

    public Http2AsyncParser(String connectionId, Http2Parser.Input input, Http2Parser.Output output, SocketWrapperBase<?> socketWrapper, Http2AsyncUpgradeHandler upgradeHandler) {
        super(connectionId, input, output);
        this.error = null;
        this.socketWrapper = socketWrapper;
        socketWrapper.getSocketBufferHandler().expand(input.getMaxFrameSize());
        this.upgradeHandler = upgradeHandler;
        this.header = ByteBuffer.allocate(9);
        this.framePaylod = ByteBuffer.allocate(input.getMaxFrameSize());
    }

    @Override // org.apache.coyote.http2.Http2Parser
    public boolean readFrame(boolean block, FrameType expected) throws IOException, Http2Exception {
        if (block) {
            return super.readFrame(block, expected);
        }
        handleAsyncException();
        this.header.clear();
        this.framePaylod.clear();
        FrameCompletionHandler handler = new FrameCompletionHandler(expected, new ByteBuffer[]{this.header, this.framePaylod});
        SocketWrapperBase.CompletionState state = this.socketWrapper.read(SocketWrapperBase.BlockingMode.NON_BLOCK, this.socketWrapper.getWriteTimeout(), TimeUnit.MILLISECONDS, null, handler, handler, this.header, this.framePaylod);
        if (state == SocketWrapperBase.CompletionState.ERROR || state == SocketWrapperBase.CompletionState.INLINE) {
            handleAsyncException();
            return true;
        }
        return false;
    }

    private void handleAsyncException() throws IOException, Http2Exception {
        if (this.error != null) {
            Throwable error = this.error;
            this.error = null;
            if (error instanceof Http2Exception) {
                throw ((Http2Exception) error);
            }
            if (error instanceof IOException) {
                throw ((IOException) error);
            }
            throw new RuntimeException(error);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/Http2AsyncParser$FrameCompletionHandler.class */
    private class FrameCompletionHandler implements SocketWrapperBase.CompletionCheck, CompletionHandler<Long, Void> {
        private boolean parsedFrameHeader;
        private boolean validated;
        private final FrameType expected;
        private final ByteBuffer[] buffers;
        private int payloadSize;
        private FrameType frameType;
        private int flags;
        private int streamId;
        private boolean streamException;
        private SocketWrapperBase.CompletionState state;

        private FrameCompletionHandler(FrameType expected, ByteBuffer... buffers) {
            Http2AsyncParser.this = r4;
            this.parsedFrameHeader = false;
            this.validated = false;
            this.streamException = false;
            this.state = null;
            this.expected = expected;
            this.buffers = buffers;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase.CompletionCheck
        public SocketWrapperBase.CompletionHandlerCall callHandler(SocketWrapperBase.CompletionState state, ByteBuffer[] buffers, int offset, int length) {
            if (offset != 0 || length != 2) {
                try {
                    throw new IllegalArgumentException(Http2Parser.sm.getString("http2Parser.invalidBuffers"));
                } catch (IllegalArgumentException e) {
                    Http2AsyncParser.this.error = e;
                    return SocketWrapperBase.CompletionHandlerCall.DONE;
                }
            }
            if (!this.parsedFrameHeader) {
                ByteBuffer frameHeaderBuffer = buffers[0];
                if (frameHeaderBuffer.position() < 9) {
                    return SocketWrapperBase.CompletionHandlerCall.CONTINUE;
                }
                this.parsedFrameHeader = true;
                this.payloadSize = ByteUtil.getThreeBytes(frameHeaderBuffer, 0);
                this.frameType = FrameType.valueOf(ByteUtil.getOneByte(frameHeaderBuffer, 3));
                this.flags = ByteUtil.getOneByte(frameHeaderBuffer, 4);
                this.streamId = ByteUtil.get31Bits(frameHeaderBuffer, 5);
            }
            this.state = state;
            if (!this.validated) {
                this.validated = true;
                try {
                    Http2AsyncParser.this.validateFrame(this.expected, this.frameType, this.streamId, this.flags, this.payloadSize);
                } catch (StreamException e2) {
                    Http2AsyncParser.this.error = e2;
                    this.streamException = true;
                } catch (Http2Exception e3) {
                    Http2AsyncParser.this.error = e3;
                    return SocketWrapperBase.CompletionHandlerCall.DONE;
                }
            }
            if (buffers[1].position() < this.payloadSize) {
                return SocketWrapperBase.CompletionHandlerCall.CONTINUE;
            }
            return SocketWrapperBase.CompletionHandlerCall.DONE;
        }

        @Override // java.nio.channels.CompletionHandler
        public void completed(Long result, Void attachment) {
            boolean continueParsing;
            if (this.streamException || Http2AsyncParser.this.error == null) {
                ByteBuffer payload = this.buffers[1];
                payload.flip();
                do {
                    try {
                        continueParsing = false;
                        if (this.streamException) {
                            Http2AsyncParser.this.swallow(this.streamId, this.payloadSize, false, payload);
                        } else {
                            switch (this.frameType) {
                                case DATA:
                                    Http2AsyncParser.this.readDataFrame(this.streamId, this.flags, this.payloadSize, payload);
                                    break;
                                case HEADERS:
                                    Http2AsyncParser.this.readHeadersFrame(this.streamId, this.flags, this.payloadSize, payload);
                                    break;
                                case PRIORITY:
                                    Http2AsyncParser.this.readPriorityFrame(this.streamId, payload);
                                    break;
                                case RST:
                                    Http2AsyncParser.this.readRstFrame(this.streamId, payload);
                                    break;
                                case SETTINGS:
                                    Http2AsyncParser.this.readSettingsFrame(this.flags, this.payloadSize, payload);
                                    break;
                                case PUSH_PROMISE:
                                    Http2AsyncParser.this.readPushPromiseFrame(this.streamId, payload);
                                    break;
                                case PING:
                                    Http2AsyncParser.this.readPingFrame(this.flags, payload);
                                    break;
                                case GOAWAY:
                                    Http2AsyncParser.this.readGoawayFrame(this.payloadSize, payload);
                                    break;
                                case WINDOW_UPDATE:
                                    Http2AsyncParser.this.readWindowUpdateFrame(this.streamId, payload);
                                    break;
                                case CONTINUATION:
                                    Http2AsyncParser.this.readContinuationFrame(this.streamId, this.flags, this.payloadSize, payload);
                                    break;
                                case UNKNOWN:
                                    Http2AsyncParser.this.readUnknownFrame(this.streamId, this.frameType, this.flags, this.payloadSize, payload);
                                    break;
                            }
                        }
                        if (payload.remaining() >= 9) {
                            int position = payload.position();
                            this.payloadSize = ByteUtil.getThreeBytes(payload, position);
                            this.frameType = FrameType.valueOf(ByteUtil.getOneByte(payload, position + 3));
                            this.flags = ByteUtil.getOneByte(payload, position + 4);
                            this.streamId = ByteUtil.get31Bits(payload, position + 5);
                            this.streamException = false;
                            if (payload.remaining() - 9 >= this.payloadSize) {
                                continueParsing = true;
                                payload.position(payload.position() + 9);
                                try {
                                    Http2AsyncParser.this.validateFrame(null, this.frameType, this.streamId, this.flags, this.payloadSize);
                                } catch (StreamException e) {
                                    Http2AsyncParser.this.error = e;
                                    this.streamException = true;
                                } catch (Http2Exception e2) {
                                    Http2AsyncParser.this.error = e2;
                                    continueParsing = false;
                                }
                            }
                        }
                    } catch (IOException | RuntimeException | Http2Exception e3) {
                        Http2AsyncParser.this.error = e3;
                    }
                } while (continueParsing);
                if (payload.hasRemaining()) {
                    Http2AsyncParser.this.socketWrapper.unRead(payload);
                }
            }
            if (this.state == SocketWrapperBase.CompletionState.DONE) {
                Http2AsyncParser.this.upgradeHandler.upgradeDispatch(SocketEvent.OPEN_READ);
            }
        }

        @Override // java.nio.channels.CompletionHandler
        public void failed(Throwable e, Void attachment) {
            Http2AsyncParser.this.error = e;
            if (this.state == null || this.state == SocketWrapperBase.CompletionState.DONE) {
                Http2AsyncParser.this.upgradeHandler.upgradeDispatch(SocketEvent.ERROR);
            }
        }
    }
}