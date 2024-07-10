package org.springframework.http.client.support;

import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/support/HttpRequestWrapper.class */
public class HttpRequestWrapper implements HttpRequest {
    private final HttpRequest request;

    public HttpRequestWrapper(HttpRequest request) {
        Assert.notNull(request, "HttpRequest must not be null");
        this.request = request;
    }

    public HttpRequest getRequest() {
        return this.request;
    }

    @Override // org.springframework.http.HttpRequest
    @Nullable
    public HttpMethod getMethod() {
        return this.request.getMethod();
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return this.request.getMethodValue();
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return this.request.getURI();
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.request.getHeaders();
    }
}