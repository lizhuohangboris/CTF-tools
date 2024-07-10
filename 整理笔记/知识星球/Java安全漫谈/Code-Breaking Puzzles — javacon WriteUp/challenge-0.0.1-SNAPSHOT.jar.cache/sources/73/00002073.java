package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/SimpleStreamingClientHttpRequest.class */
final class SimpleStreamingClientHttpRequest extends AbstractClientHttpRequest {
    private final HttpURLConnection connection;
    private final int chunkSize;
    @Nullable
    private OutputStream body;
    private final boolean outputStreaming;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleStreamingClientHttpRequest(HttpURLConnection connection, int chunkSize, boolean outputStreaming) {
        this.connection = connection;
        this.chunkSize = chunkSize;
        this.outputStreaming = outputStreaming;
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return this.connection.getRequestMethod();
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        try {
            return this.connection.getURL().toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not get HttpURLConnection URI: " + ex.getMessage(), ex);
        }
    }

    @Override // org.springframework.http.client.AbstractClientHttpRequest
    protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
        if (this.body == null) {
            if (this.outputStreaming) {
                long contentLength = headers.getContentLength();
                if (contentLength >= 0) {
                    this.connection.setFixedLengthStreamingMode(contentLength);
                } else {
                    this.connection.setChunkedStreamingMode(this.chunkSize);
                }
            }
            SimpleBufferingClientHttpRequest.addHeaders(this.connection, headers);
            this.connection.connect();
            this.body = this.connection.getOutputStream();
        }
        return StreamUtils.nonClosing(this.body);
    }

    @Override // org.springframework.http.client.AbstractClientHttpRequest
    protected ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
        try {
            if (this.body != null) {
                this.body.close();
            } else {
                SimpleBufferingClientHttpRequest.addHeaders(this.connection, headers);
                this.connection.connect();
                this.connection.getResponseCode();
            }
        } catch (IOException e) {
        }
        return new SimpleClientHttpResponse(this.connection);
    }
}