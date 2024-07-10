package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/AbstractAsyncClientHttpRequest.class */
abstract class AbstractAsyncClientHttpRequest implements AsyncClientHttpRequest {
    private final HttpHeaders headers = new HttpHeaders();
    private boolean executed = false;

    protected abstract OutputStream getBodyInternal(HttpHeaders httpHeaders) throws IOException;

    protected abstract ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders httpHeaders) throws IOException;

    @Override // org.springframework.http.HttpMessage
    public final HttpHeaders getHeaders() {
        return this.executed ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
    }

    @Override // org.springframework.http.HttpOutputMessage
    public final OutputStream getBody() throws IOException {
        assertNotExecuted();
        return getBodyInternal(this.headers);
    }

    @Override // org.springframework.http.client.AsyncClientHttpRequest
    public ListenableFuture<ClientHttpResponse> executeAsync() throws IOException {
        assertNotExecuted();
        ListenableFuture<ClientHttpResponse> result = executeInternal(this.headers);
        this.executed = true;
        return result;
    }

    protected void assertNotExecuted() {
        Assert.state(!this.executed, "ClientHttpRequest already executed");
    }
}