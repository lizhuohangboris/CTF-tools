package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;
import org.springframework.util.concurrent.ListenableFuture;

/* JADX INFO: Access modifiers changed from: package-private */
@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/SimpleStreamingAsyncClientHttpRequest.class */
public final class SimpleStreamingAsyncClientHttpRequest extends AbstractAsyncClientHttpRequest {
    private final HttpURLConnection connection;
    private final int chunkSize;
    @Nullable
    private OutputStream body;
    private final boolean outputStreaming;
    private final AsyncListenableTaskExecutor taskExecutor;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleStreamingAsyncClientHttpRequest(HttpURLConnection connection, int chunkSize, boolean outputStreaming, AsyncListenableTaskExecutor taskExecutor) {
        this.connection = connection;
        this.chunkSize = chunkSize;
        this.outputStreaming = outputStreaming;
        this.taskExecutor = taskExecutor;
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

    @Override // org.springframework.http.client.AbstractAsyncClientHttpRequest
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

    @Override // org.springframework.http.client.AbstractAsyncClientHttpRequest
    protected ListenableFuture<ClientHttpResponse> executeInternal(final HttpHeaders headers) throws IOException {
        return this.taskExecutor.submitListenable(new Callable<ClientHttpResponse>() { // from class: org.springframework.http.client.SimpleStreamingAsyncClientHttpRequest.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public ClientHttpResponse call() throws Exception {
                try {
                    if (SimpleStreamingAsyncClientHttpRequest.this.body != null) {
                        SimpleStreamingAsyncClientHttpRequest.this.body.close();
                    } else {
                        SimpleBufferingClientHttpRequest.addHeaders(SimpleStreamingAsyncClientHttpRequest.this.connection, headers);
                        SimpleStreamingAsyncClientHttpRequest.this.connection.connect();
                        SimpleStreamingAsyncClientHttpRequest.this.connection.getResponseCode();
                    }
                } catch (IOException e) {
                }
                return new SimpleClientHttpResponse(SimpleStreamingAsyncClientHttpRequest.this.connection);
            }
        });
    }
}