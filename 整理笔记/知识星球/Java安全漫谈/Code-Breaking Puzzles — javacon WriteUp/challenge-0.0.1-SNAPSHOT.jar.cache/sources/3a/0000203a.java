package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/AbstractClientHttpRequest.class */
public abstract class AbstractClientHttpRequest implements ClientHttpRequest {
    private final HttpHeaders headers = new HttpHeaders();
    private boolean executed = false;

    protected abstract OutputStream getBodyInternal(HttpHeaders httpHeaders) throws IOException;

    protected abstract ClientHttpResponse executeInternal(HttpHeaders httpHeaders) throws IOException;

    @Override // org.springframework.http.HttpMessage
    public final HttpHeaders getHeaders() {
        return this.executed ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
    }

    @Override // org.springframework.http.HttpOutputMessage
    public final OutputStream getBody() throws IOException {
        assertNotExecuted();
        return getBodyInternal(this.headers);
    }

    @Override // org.springframework.http.client.ClientHttpRequest
    public final ClientHttpResponse execute() throws IOException {
        assertNotExecuted();
        ClientHttpResponse result = executeInternal(this.headers);
        this.executed = true;
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void assertNotExecuted() {
        Assert.state(!this.executed, "ClientHttpRequest already executed");
    }
}