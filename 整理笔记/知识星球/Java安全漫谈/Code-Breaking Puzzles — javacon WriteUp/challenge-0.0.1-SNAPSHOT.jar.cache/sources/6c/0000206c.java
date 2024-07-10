package org.springframework.http.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.concurrent.ListenableFuture;

/* JADX INFO: Access modifiers changed from: package-private */
@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/SimpleBufferingAsyncClientHttpRequest.class */
public final class SimpleBufferingAsyncClientHttpRequest extends AbstractBufferingAsyncClientHttpRequest {
    private final HttpURLConnection connection;
    private final boolean outputStreaming;
    private final AsyncListenableTaskExecutor taskExecutor;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleBufferingAsyncClientHttpRequest(HttpURLConnection connection, boolean outputStreaming, AsyncListenableTaskExecutor taskExecutor) {
        this.connection = connection;
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

    @Override // org.springframework.http.client.AbstractBufferingAsyncClientHttpRequest
    protected ListenableFuture<ClientHttpResponse> executeInternal(final HttpHeaders headers, final byte[] bufferedOutput) throws IOException {
        return this.taskExecutor.submitListenable(new Callable<ClientHttpResponse>() { // from class: org.springframework.http.client.SimpleBufferingAsyncClientHttpRequest.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public ClientHttpResponse call() throws Exception {
                SimpleBufferingClientHttpRequest.addHeaders(SimpleBufferingAsyncClientHttpRequest.this.connection, headers);
                if (SimpleBufferingAsyncClientHttpRequest.this.getMethod() == HttpMethod.DELETE && bufferedOutput.length == 0) {
                    SimpleBufferingAsyncClientHttpRequest.this.connection.setDoOutput(false);
                }
                if (SimpleBufferingAsyncClientHttpRequest.this.connection.getDoOutput() && SimpleBufferingAsyncClientHttpRequest.this.outputStreaming) {
                    SimpleBufferingAsyncClientHttpRequest.this.connection.setFixedLengthStreamingMode(bufferedOutput.length);
                }
                SimpleBufferingAsyncClientHttpRequest.this.connection.connect();
                if (SimpleBufferingAsyncClientHttpRequest.this.connection.getDoOutput()) {
                    FileCopyUtils.copy(bufferedOutput, SimpleBufferingAsyncClientHttpRequest.this.connection.getOutputStream());
                } else {
                    SimpleBufferingAsyncClientHttpRequest.this.connection.getResponseCode();
                }
                return new SimpleClientHttpResponse(SimpleBufferingAsyncClientHttpRequest.this.connection);
            }
        });
    }
}