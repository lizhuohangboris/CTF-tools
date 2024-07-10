package org.springframework.http.server.reactive;

import java.net.InetSocketAddress;
import java.net.URI;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServerHttpRequestDecorator.class */
public class ServerHttpRequestDecorator implements ServerHttpRequest {
    private final ServerHttpRequest delegate;

    public ServerHttpRequestDecorator(ServerHttpRequest delegate) {
        Assert.notNull(delegate, "Delegate is required");
        this.delegate = delegate;
    }

    public ServerHttpRequest getDelegate() {
        return this.delegate;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public String getId() {
        return getDelegate().getId();
    }

    @Override // org.springframework.http.HttpRequest
    @Nullable
    public HttpMethod getMethod() {
        return getDelegate().getMethod();
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return getDelegate().getMethodValue();
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return getDelegate().getURI();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public RequestPath getPath() {
        return getDelegate().getPath();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public MultiValueMap<String, String> getQueryParams() {
        return getDelegate().getQueryParams();
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return getDelegate().getHeaders();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public MultiValueMap<String, HttpCookie> getCookies() {
        return getDelegate().getCookies();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public InetSocketAddress getRemoteAddress() {
        return getDelegate().getRemoteAddress();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @Nullable
    public SslInfo getSslInfo() {
        return getDelegate().getSslInfo();
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        return getDelegate().getBody();
    }

    public String toString() {
        return getClass().getSimpleName() + " [delegate=" + getDelegate() + "]";
    }
}