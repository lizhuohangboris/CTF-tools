package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/OkHttp3ClientHttpRequest.class */
class OkHttp3ClientHttpRequest extends AbstractBufferingClientHttpRequest {
    private final OkHttpClient client;
    private final URI uri;
    private final HttpMethod method;

    public OkHttp3ClientHttpRequest(OkHttpClient client, URI uri, HttpMethod method) {
        this.client = client;
        this.uri = uri;
        this.method = method;
    }

    @Override // org.springframework.http.HttpRequest
    public HttpMethod getMethod() {
        return this.method;
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return this.method.name();
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return this.uri;
    }

    @Override // org.springframework.http.client.AbstractBufferingClientHttpRequest
    protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] content) throws IOException {
        Request request = OkHttp3ClientHttpRequestFactory.buildRequest(headers, content, this.uri, this.method);
        return new OkHttp3ClientHttpResponse(this.client.newCall(request).execute());
    }
}