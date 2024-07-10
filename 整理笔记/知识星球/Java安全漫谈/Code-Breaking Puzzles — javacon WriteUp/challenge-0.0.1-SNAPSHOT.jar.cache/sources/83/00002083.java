package org.springframework.http.client.reactive;

import io.netty.buffer.ByteBufAllocator;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.netty.NettyInbound;
import reactor.netty.http.client.HttpClientResponse;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/reactive/ReactorClientHttpResponse.class */
public class ReactorClientHttpResponse implements ClientHttpResponse {
    private final NettyDataBufferFactory bufferFactory;
    private final HttpClientResponse response;
    private final NettyInbound inbound;

    public ReactorClientHttpResponse(HttpClientResponse response, NettyInbound inbound, ByteBufAllocator alloc) {
        this.response = response;
        this.inbound = inbound;
        this.bufferFactory = new NettyDataBufferFactory(alloc);
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        return this.inbound.receive().map(byteBuf -> {
            byteBuf.retain();
            return this.bufferFactory.wrap(byteBuf);
        });
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        this.response.responseHeaders().entries().forEach(e -> {
            headers.add((String) e.getKey(), (String) e.getValue());
        });
        return headers;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public HttpStatus getStatusCode() {
        return HttpStatus.valueOf(getRawStatusCode());
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public int getRawStatusCode() {
        return this.response.status().code();
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public MultiValueMap<String, ResponseCookie> getCookies() {
        MultiValueMap<String, ResponseCookie> result = new LinkedMultiValueMap<>();
        this.response.cookies().values().stream().flatMap((v0) -> {
            return v0.stream();
        }).forEach(cookie -> {
            result.add(cookie.name(), ResponseCookie.from(cookie.name(), cookie.value()).domain(cookie.domain()).path(cookie.path()).maxAge(cookie.maxAge()).secure(cookie.isSecure()).httpOnly(cookie.isHttpOnly()).build());
        });
        return CollectionUtils.unmodifiableMultiValueMap(result);
    }

    public String toString() {
        return "ReactorClientHttpResponse{request=[" + this.response.method().name() + " " + this.response.uri() + "],status=" + getRawStatusCode() + '}';
    }
}