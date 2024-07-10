package org.springframework.http.client.reactive;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import java.net.URI;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClientRequest;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/reactive/ReactorClientHttpRequest.class */
public class ReactorClientHttpRequest extends AbstractClientHttpRequest implements ZeroCopyHttpOutputMessage {
    private final HttpMethod httpMethod;
    private final URI uri;
    private final HttpClientRequest request;
    private final NettyOutbound outbound;
    private final NettyDataBufferFactory bufferFactory;

    public ReactorClientHttpRequest(HttpMethod method, URI uri, HttpClientRequest request, NettyOutbound outbound) {
        this.httpMethod = method;
        this.uri = uri;
        this.request = request;
        this.outbound = outbound;
        this.bufferFactory = new NettyDataBufferFactory(outbound.alloc());
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public DataBufferFactory bufferFactory() {
        return this.bufferFactory;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public HttpMethod getMethod() {
        return this.httpMethod;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public URI getURI() {
        return this.uri;
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return doCommit(() -> {
            Flux<ByteBuf> byteBufFlux = Flux.from(body).map(NettyDataBufferFactory::toByteBuf);
            return this.outbound.send(byteBufFlux).then();
        });
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        Flux map = Flux.from(body).map(ReactorClientHttpRequest::toByteBufs);
        return doCommit(() -> {
            return this.outbound.sendGroups(map).then();
        });
    }

    private static Publisher<ByteBuf> toByteBufs(Publisher<? extends DataBuffer> dataBuffers) {
        return Flux.from(dataBuffers).map(NettyDataBufferFactory::toByteBuf);
    }

    @Override // org.springframework.http.ZeroCopyHttpOutputMessage
    public Mono<Void> writeWith(Path file, long position, long count) {
        return doCommit(() -> {
            return this.outbound.sendFile(file, position, count).then();
        });
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> setComplete() {
        NettyOutbound nettyOutbound = this.outbound;
        nettyOutbound.getClass();
        return doCommit(this::then);
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected void applyHeaders() {
        getHeaders().forEach(key, value -> {
            this.request.requestHeaders().set(key, value);
        });
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected void applyCookies() {
        Stream map = getCookies().values().stream().flatMap((v0) -> {
            return v0.stream();
        }).map(cookie -> {
            return new DefaultCookie(cookie.getName(), cookie.getValue());
        });
        HttpClientRequest httpClientRequest = this.request;
        httpClientRequest.getClass();
        map.forEach((v1) -> {
            r1.addCookie(v1);
        });
    }
}