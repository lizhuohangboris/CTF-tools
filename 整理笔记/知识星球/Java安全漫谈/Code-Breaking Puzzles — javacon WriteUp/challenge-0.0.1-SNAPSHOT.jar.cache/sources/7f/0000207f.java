package org.springframework.http.client.reactive;

import java.net.HttpCookie;
import java.util.List;
import org.eclipse.jetty.reactive.client.ReactiveResponse;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/reactive/JettyClientHttpResponse.class */
public class JettyClientHttpResponse implements ClientHttpResponse {
    private final ReactiveResponse reactiveResponse;
    private final Flux<DataBuffer> content;

    public JettyClientHttpResponse(ReactiveResponse reactiveResponse, Publisher<DataBuffer> content) {
        this.reactiveResponse = reactiveResponse;
        this.content = Flux.from(content);
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public HttpStatus getStatusCode() {
        return HttpStatus.valueOf(getRawStatusCode());
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public int getRawStatusCode() {
        return this.reactiveResponse.getStatus();
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public MultiValueMap<String, ResponseCookie> getCookies() {
        MultiValueMap<String, ResponseCookie> result = new LinkedMultiValueMap<>();
        List<String> cookieHeader = getHeaders().get(HttpHeaders.SET_COOKIE);
        if (cookieHeader != null) {
            cookieHeader.forEach(header -> {
                HttpCookie.parse(header).forEach(cookie -> {
                    result.add(cookie.getName(), ResponseCookie.from(cookie.getName(), cookie.getValue()).domain(cookie.getDomain()).path(cookie.getPath()).maxAge(cookie.getMaxAge()).secure(cookie.getSecure()).httpOnly(cookie.isHttpOnly()).build());
                });
            });
        }
        return CollectionUtils.unmodifiableMultiValueMap(result);
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        return this.content;
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        this.reactiveResponse.getHeaders().stream().forEach(field -> {
            headers.add(field.getName(), field.getValue());
        });
        return headers;
    }
}