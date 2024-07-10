package org.springframework.http.server.reactive;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.CookieImpl;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.xnio.ChannelListener;
import org.xnio.channels.Channels;
import org.xnio.channels.StreamSinkChannel;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/UndertowServerHttpResponse.class */
public class UndertowServerHttpResponse extends AbstractListenerServerHttpResponse implements ZeroCopyHttpOutputMessage {
    private final HttpServerExchange exchange;
    private final UndertowServerHttpRequest request;
    @Nullable
    private StreamSinkChannel responseChannel;

    public UndertowServerHttpResponse(HttpServerExchange exchange, DataBufferFactory bufferFactory, UndertowServerHttpRequest request) {
        super(bufferFactory, createHeaders(exchange));
        Assert.notNull(exchange, "HttpServerExchange must not be null");
        this.exchange = exchange;
        this.request = request;
    }

    private static HttpHeaders createHeaders(HttpServerExchange exchange) {
        UndertowHeadersAdapter headersMap = new UndertowHeadersAdapter(exchange.getResponseHeaders());
        return new HttpHeaders(headersMap);
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    public <T> T getNativeResponse() {
        return (T) this.exchange;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyStatusCode() {
        Integer statusCode = getStatusCodeValue();
        if (statusCode != null) {
            this.exchange.setStatusCode(statusCode.intValue());
        }
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyHeaders() {
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyCookies() {
        for (String name : getCookies().keySet()) {
            for (ResponseCookie httpCookie : (List) getCookies().get(name)) {
                CookieImpl cookieImpl = new CookieImpl(name, httpCookie.getValue());
                if (!httpCookie.getMaxAge().isNegative()) {
                    cookieImpl.setMaxAge(Integer.valueOf((int) httpCookie.getMaxAge().getSeconds()));
                }
                if (httpCookie.getDomain() != null) {
                    cookieImpl.setDomain(httpCookie.getDomain());
                }
                if (httpCookie.getPath() != null) {
                    cookieImpl.setPath(httpCookie.getPath());
                }
                cookieImpl.setSecure(httpCookie.isSecure());
                cookieImpl.setHttpOnly(httpCookie.isHttpOnly());
                this.exchange.getResponseCookies().putIfAbsent(name, cookieImpl);
            }
        }
    }

    @Override // org.springframework.http.ZeroCopyHttpOutputMessage
    public Mono<Void> writeWith(Path file, long position, long count) {
        return doCommit(() -> {
            return Mono.defer(() -> {
                try {
                    FileChannel source = FileChannel.open(file, StandardOpenOption.READ);
                    StreamSinkChannel destination = this.exchange.getResponseChannel();
                    Channels.transferBlocking(destination, source, position, count);
                    Mono empty = Mono.empty();
                    if (source != null) {
                        if (0 != 0) {
                            source.close();
                        } else {
                            source.close();
                        }
                    }
                    return empty;
                } catch (IOException ex) {
                    return Mono.error(ex);
                }
            });
        });
    }

    @Override // org.springframework.http.server.reactive.AbstractListenerServerHttpResponse
    protected Processor<? super Publisher<? extends DataBuffer>, Void> createBodyFlushProcessor() {
        return new ResponseBodyFlushProcessor();
    }

    public ResponseBodyProcessor createBodyProcessor() {
        if (this.responseChannel == null) {
            this.responseChannel = this.exchange.getResponseChannel();
        }
        return new ResponseBodyProcessor(this.responseChannel);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/UndertowServerHttpResponse$ResponseBodyProcessor.class */
    public class ResponseBodyProcessor extends AbstractListenerWriteProcessor<DataBuffer> {
        private final StreamSinkChannel channel;
        @Nullable
        private volatile ByteBuffer byteBuffer;
        private volatile boolean writePossible;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ResponseBodyProcessor(StreamSinkChannel channel) {
            super(r4.request.getLogPrefix());
            UndertowServerHttpResponse.this = r4;
            Assert.notNull(channel, "StreamSinkChannel must not be null");
            this.channel = channel;
            this.channel.getWriteSetter().set(c -> {
                this.writePossible = true;
                onWritePossible();
            });
            this.channel.suspendWrites();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        protected boolean isWritePossible() {
            this.channel.resumeWrites();
            return this.writePossible;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public boolean write(DataBuffer dataBuffer) throws IOException {
            ByteBuffer buffer = this.byteBuffer;
            if (buffer == null) {
                return false;
            }
            this.writePossible = false;
            int total = buffer.remaining();
            int written = writeByteBuffer(buffer);
            if (UndertowServerHttpResponse.this.logger.isTraceEnabled()) {
                UndertowServerHttpResponse.this.logger.trace(getLogPrefix() + "Wrote " + written + " of " + total + " bytes");
            } else if (rsWriteLogger.isTraceEnabled()) {
                rsWriteLogger.trace(getLogPrefix() + "Wrote " + written + " of " + total + " bytes");
            }
            if (written != total) {
                return false;
            }
            this.writePossible = true;
            DataBufferUtils.release(dataBuffer);
            this.byteBuffer = null;
            return true;
        }

        private int writeByteBuffer(ByteBuffer byteBuffer) throws IOException {
            int written;
            int totalWritten = 0;
            do {
                written = this.channel.write(byteBuffer);
                totalWritten += written;
                if (!byteBuffer.hasRemaining()) {
                    break;
                }
            } while (written > 0);
            return totalWritten;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public void dataReceived(DataBuffer dataBuffer) {
            super.dataReceived((ResponseBodyProcessor) dataBuffer);
            this.byteBuffer = dataBuffer.asByteBuffer();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public boolean isDataEmpty(DataBuffer dataBuffer) {
            return dataBuffer.readableByteCount() == 0;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        protected void writingComplete() {
            this.channel.getWriteSetter().set((ChannelListener) null);
            this.channel.resumeWrites();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        protected void writingFailed(Throwable ex) {
            cancel();
            onError(ex);
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public void discardData(DataBuffer dataBuffer) {
            DataBufferUtils.release(dataBuffer);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/UndertowServerHttpResponse$ResponseBodyFlushProcessor.class */
    private class ResponseBodyFlushProcessor extends AbstractListenerWriteFlushProcessor<DataBuffer> {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ResponseBodyFlushProcessor() {
            super(r4.request.getLogPrefix());
            UndertowServerHttpResponse.this = r4;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected Processor<? super DataBuffer, Void> createWriteProcessor() {
            return UndertowServerHttpResponse.this.createBodyProcessor();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected void flush() throws IOException {
            StreamSinkChannel channel = UndertowServerHttpResponse.this.responseChannel;
            if (channel != null) {
                if (rsWriteFlushLogger.isTraceEnabled()) {
                    rsWriteFlushLogger.trace(getLogPrefix() + "flush");
                }
                channel.flush();
            }
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected void flushingFailed(Throwable t) {
            cancel();
            onError(t);
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected boolean isWritePossible() {
            StreamSinkChannel channel = UndertowServerHttpResponse.this.responseChannel;
            if (channel != null) {
                channel.resumeWrites();
                return true;
            }
            return false;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected boolean isFlushPending() {
            return false;
        }
    }
}