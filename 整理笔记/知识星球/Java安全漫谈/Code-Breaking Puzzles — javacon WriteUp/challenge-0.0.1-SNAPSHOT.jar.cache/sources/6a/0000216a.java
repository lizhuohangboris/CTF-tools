package org.springframework.http.server.reactive;

import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServerHttpResponseDecorator.class */
public class ServerHttpResponseDecorator implements ServerHttpResponse {
    private final ServerHttpResponse delegate;

    public ServerHttpResponseDecorator(ServerHttpResponse delegate) {
        Assert.notNull(delegate, "Delegate is required");
        this.delegate = delegate;
    }

    public ServerHttpResponse getDelegate() {
        return this.delegate;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponse
    public boolean setStatusCode(@Nullable HttpStatus status) {
        return getDelegate().setStatusCode(status);
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponse
    public HttpStatus getStatusCode() {
        return getDelegate().getStatusCode();
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return getDelegate().getHeaders();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponse
    public MultiValueMap<String, ResponseCookie> getCookies() {
        return getDelegate().getCookies();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponse
    public void addCookie(ResponseCookie cookie) {
        getDelegate().addCookie(cookie);
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public DataBufferFactory bufferFactory() {
        return getDelegate().bufferFactory();
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public void beforeCommit(Supplier<? extends Mono<Void>> action) {
        getDelegate().beforeCommit(action);
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public boolean isCommitted() {
        return getDelegate().isCommitted();
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return getDelegate().writeWith(body);
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return getDelegate().writeAndFlushWith(body);
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> setComplete() {
        return getDelegate().setComplete();
    }

    public String toString() {
        return getClass().getSimpleName() + " [delegate=" + getDelegate() + "]";
    }
}