package org.springframework.http.server.reactive;

import ch.qos.logback.classic.spi.CallerData;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.function.IntPredicate;
import javax.net.ssl.SSLSession;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.xnio.channels.StreamSourceChannel;
import reactor.core.publisher.Flux;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/UndertowServerHttpRequest.class */
public class UndertowServerHttpRequest extends AbstractServerHttpRequest {
    private final HttpServerExchange exchange;
    private final RequestBodyPublisher body;

    public UndertowServerHttpRequest(HttpServerExchange exchange, DataBufferFactory bufferFactory) throws URISyntaxException {
        super(initUri(exchange), "", initHeaders(exchange));
        this.exchange = exchange;
        this.body = new RequestBodyPublisher(exchange, bufferFactory);
        this.body.registerListeners(exchange);
    }

    private static URI initUri(HttpServerExchange exchange) throws URISyntaxException {
        Assert.notNull(exchange, "HttpServerExchange is required.");
        String requestURL = exchange.getRequestURL();
        String query = exchange.getQueryString();
        String requestUriAndQuery = StringUtils.isEmpty(query) ? requestURL : requestURL + CallerData.NA + query;
        return new URI(requestUriAndQuery);
    }

    private static HttpHeaders initHeaders(HttpServerExchange exchange) {
        UndertowHeadersAdapter headersMap = new UndertowHeadersAdapter(exchange.getRequestHeaders());
        return new HttpHeaders(headersMap);
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return this.exchange.getRequestMethod().toString();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    protected MultiValueMap<String, HttpCookie> initCookies() {
        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();
        for (String name : this.exchange.getRequestCookies().keySet()) {
            Cookie cookie = (Cookie) this.exchange.getRequestCookies().get(name);
            HttpCookie httpCookie = new HttpCookie(name, cookie.getValue());
            cookies.add(name, httpCookie);
        }
        return cookies;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public InetSocketAddress getRemoteAddress() {
        return this.exchange.getSourceAddress();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    @Nullable
    protected SslInfo initSslInfo() {
        SSLSession session = this.exchange.getConnection().getSslSession();
        if (session != null) {
            return new DefaultSslInfo(session);
        }
        return null;
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        return Flux.from(this.body);
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    public <T> T getNativeRequest() {
        return (T) this.exchange;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    protected String initId() {
        return ObjectUtils.getIdentityHexString(this.exchange.getConnection());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/UndertowServerHttpRequest$RequestBodyPublisher.class */
    public class RequestBodyPublisher extends AbstractListenerReadPublisher<DataBuffer> {
        private final StreamSourceChannel channel;
        private final DataBufferFactory bufferFactory;
        private final ByteBufferPool byteBufferPool;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public RequestBodyPublisher(HttpServerExchange exchange, DataBufferFactory bufferFactory) {
            super(r4.getLogPrefix());
            UndertowServerHttpRequest.this = r4;
            this.channel = exchange.getRequestChannel();
            this.bufferFactory = bufferFactory;
            this.byteBufferPool = exchange.getConnection().getByteBufferPool();
        }

        public void registerListeners(HttpServerExchange exchange) {
            exchange.addExchangeCompleteListener(ex, next -> {
                onAllDataRead();
                next.proceed();
            });
            this.channel.getReadSetter().set(c -> {
                onDataAvailable();
            });
            this.channel.getCloseSetter().set(c2 -> {
                onAllDataRead();
            });
            this.channel.resumeReads();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        protected void checkOnDataAvailable() {
            this.channel.resumeReads();
            onDataAvailable();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        protected void readingPaused() {
            this.channel.suspendReads();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        @Nullable
        public DataBuffer read() throws IOException {
            PooledByteBuffer pooledByteBuffer = this.byteBufferPool.allocate();
            try {
                ByteBuffer byteBuffer = pooledByteBuffer.getBuffer();
                int read = this.channel.read(byteBuffer);
                if (rsReadLogger.isTraceEnabled()) {
                    rsReadLogger.trace(getLogPrefix() + "Read " + read + (read != -1 ? " bytes" : ""));
                }
                if (read > 0) {
                    byteBuffer.flip();
                    DataBuffer dataBuffer = this.bufferFactory.wrap(byteBuffer);
                    UndertowDataBuffer undertowDataBuffer = new UndertowDataBuffer(dataBuffer, pooledByteBuffer);
                    if (0 != 0 && pooledByteBuffer.isOpen()) {
                        pooledByteBuffer.close();
                    }
                    return undertowDataBuffer;
                }
                if (read == -1) {
                    onAllDataRead();
                }
                return null;
            } finally {
                if (1 != 0 && pooledByteBuffer.isOpen()) {
                    pooledByteBuffer.close();
                }
            }
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        protected void discardData() {
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/UndertowServerHttpRequest$UndertowDataBuffer.class */
    public static class UndertowDataBuffer implements PooledDataBuffer {
        private final DataBuffer dataBuffer;
        private final PooledByteBuffer pooledByteBuffer;

        public UndertowDataBuffer(DataBuffer dataBuffer, PooledByteBuffer pooledByteBuffer) {
            this.dataBuffer = dataBuffer;
            this.pooledByteBuffer = pooledByteBuffer;
        }

        @Override // org.springframework.core.io.buffer.PooledDataBuffer
        public boolean isAllocated() {
            return this.pooledByteBuffer.isOpen();
        }

        @Override // org.springframework.core.io.buffer.PooledDataBuffer
        public PooledDataBuffer retain() {
            return this;
        }

        @Override // org.springframework.core.io.buffer.PooledDataBuffer
        public boolean release() {
            try {
                boolean result = DataBufferUtils.release(this.dataBuffer);
                return result && this.pooledByteBuffer.isOpen();
            } finally {
                this.pooledByteBuffer.close();
            }
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBufferFactory factory() {
            return this.dataBuffer.factory();
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public int indexOf(IntPredicate predicate, int fromIndex) {
            return this.dataBuffer.indexOf(predicate, fromIndex);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public int lastIndexOf(IntPredicate predicate, int fromIndex) {
            return this.dataBuffer.lastIndexOf(predicate, fromIndex);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public int readableByteCount() {
            return this.dataBuffer.readableByteCount();
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public int writableByteCount() {
            return this.dataBuffer.writableByteCount();
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public int readPosition() {
            return this.dataBuffer.readPosition();
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer readPosition(int readPosition) {
            return this.dataBuffer.readPosition(readPosition);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public int writePosition() {
            return this.dataBuffer.writePosition();
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer writePosition(int writePosition) {
            return this.dataBuffer.writePosition(writePosition);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public int capacity() {
            return this.dataBuffer.capacity();
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer capacity(int newCapacity) {
            return this.dataBuffer.capacity(newCapacity);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public byte getByte(int index) {
            return this.dataBuffer.getByte(index);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public byte read() {
            return this.dataBuffer.read();
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer read(byte[] destination) {
            return this.dataBuffer.read(destination);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer read(byte[] destination, int offset, int length) {
            return this.dataBuffer.read(destination, offset, length);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer write(byte b) {
            return this.dataBuffer.write(b);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer write(byte[] source) {
            return this.dataBuffer.write(source);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer write(byte[] source, int offset, int length) {
            return this.dataBuffer.write(source, offset, length);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer write(DataBuffer... buffers) {
            return this.dataBuffer.write(buffers);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer write(ByteBuffer... byteBuffers) {
            return this.dataBuffer.write(byteBuffers);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer slice(int index, int length) {
            return this.dataBuffer.slice(index, length);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public ByteBuffer asByteBuffer() {
            return this.dataBuffer.asByteBuffer();
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public ByteBuffer asByteBuffer(int index, int length) {
            return this.dataBuffer.asByteBuffer(index, length);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public InputStream asInputStream() {
            return this.dataBuffer.asInputStream();
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public InputStream asInputStream(boolean releaseOnClose) {
            return this.dataBuffer.asInputStream(releaseOnClose);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public OutputStream asOutputStream() {
            return this.dataBuffer.asOutputStream();
        }
    }
}