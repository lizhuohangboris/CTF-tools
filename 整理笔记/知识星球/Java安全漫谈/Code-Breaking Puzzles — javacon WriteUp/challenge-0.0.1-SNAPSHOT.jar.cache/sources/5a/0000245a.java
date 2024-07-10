package org.springframework.web.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/MessageBodyClientHttpResponseWrapper.class */
class MessageBodyClientHttpResponseWrapper implements ClientHttpResponse {
    private final ClientHttpResponse response;
    @Nullable
    private PushbackInputStream pushbackInputStream;

    public MessageBodyClientHttpResponseWrapper(ClientHttpResponse response) throws IOException {
        this.response = response;
    }

    public boolean hasMessageBody() throws IOException {
        HttpStatus status = HttpStatus.resolve(getRawStatusCode());
        if ((status != null && (status.is1xxInformational() || status == HttpStatus.NO_CONTENT || status == HttpStatus.NOT_MODIFIED)) || getHeaders().getContentLength() == 0) {
            return false;
        }
        return true;
    }

    public boolean hasEmptyMessageBody() throws IOException {
        InputStream body = this.response.getBody();
        if (body.markSupported()) {
            body.mark(1);
            if (body.read() == -1) {
                return true;
            }
            body.reset();
            return false;
        }
        this.pushbackInputStream = new PushbackInputStream(body);
        int b = this.pushbackInputStream.read();
        if (b == -1) {
            return true;
        }
        this.pushbackInputStream.unread(b);
        return false;
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.response.getHeaders();
    }

    @Override // org.springframework.http.HttpInputMessage
    public InputStream getBody() throws IOException {
        return this.pushbackInputStream != null ? this.pushbackInputStream : this.response.getBody();
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public HttpStatus getStatusCode() throws IOException {
        return this.response.getStatusCode();
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public int getRawStatusCode() throws IOException {
        return this.response.getRawStatusCode();
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public String getStatusText() throws IOException {
        return this.response.getStatusText();
    }

    @Override // org.springframework.http.client.ClientHttpResponse, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.response.close();
    }
}