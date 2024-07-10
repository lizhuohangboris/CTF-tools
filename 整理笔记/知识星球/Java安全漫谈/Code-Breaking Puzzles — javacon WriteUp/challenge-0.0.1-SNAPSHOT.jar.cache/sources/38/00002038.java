package org.springframework.http.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.util.concurrent.ListenableFuture;

/* JADX INFO: Access modifiers changed from: package-private */
@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/AbstractBufferingAsyncClientHttpRequest.class */
public abstract class AbstractBufferingAsyncClientHttpRequest extends AbstractAsyncClientHttpRequest {
    private ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream(1024);

    protected abstract ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders httpHeaders, byte[] bArr) throws IOException;

    @Override // org.springframework.http.client.AbstractAsyncClientHttpRequest
    protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
        return this.bufferedOutput;
    }

    @Override // org.springframework.http.client.AbstractAsyncClientHttpRequest
    protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers) throws IOException {
        byte[] bytes = this.bufferedOutput.toByteArray();
        if (headers.getContentLength() < 0) {
            headers.setContentLength(bytes.length);
        }
        ListenableFuture<ClientHttpResponse> result = executeInternal(headers, bytes);
        this.bufferedOutput = new ByteArrayOutputStream(0);
        return result;
    }
}