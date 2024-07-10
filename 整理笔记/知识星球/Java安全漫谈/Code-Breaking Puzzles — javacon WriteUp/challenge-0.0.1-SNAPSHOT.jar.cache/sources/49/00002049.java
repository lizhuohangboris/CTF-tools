package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Future;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpHeaders;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.FutureAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.ListenableFutureCallbackRegistry;
import org.springframework.util.concurrent.SuccessCallback;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/HttpComponentsAsyncClientHttpRequest.class */
final class HttpComponentsAsyncClientHttpRequest extends AbstractBufferingAsyncClientHttpRequest {
    private final HttpAsyncClient httpClient;
    private final HttpUriRequest httpRequest;
    private final HttpContext httpContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpComponentsAsyncClientHttpRequest(HttpAsyncClient client, HttpUriRequest request, HttpContext context) {
        this.httpClient = client;
        this.httpRequest = request;
        this.httpContext = context;
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return this.httpRequest.getMethod();
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return this.httpRequest.getURI();
    }

    HttpContext getHttpContext() {
        return this.httpContext;
    }

    @Override // org.springframework.http.client.AbstractBufferingAsyncClientHttpRequest
    protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
        HttpComponentsClientHttpRequest.addHeaders(this.httpRequest, headers);
        if (this.httpRequest instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityEnclosingRequest = this.httpRequest;
            entityEnclosingRequest.setEntity(new NByteArrayEntity(bufferedOutput));
        }
        HttpResponseFutureCallback callback = new HttpResponseFutureCallback(this.httpRequest);
        Future<HttpResponse> futureResponse = this.httpClient.execute(this.httpRequest, this.httpContext, callback);
        return new ClientHttpResponseFuture(futureResponse, callback);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/HttpComponentsAsyncClientHttpRequest$HttpResponseFutureCallback.class */
    private static class HttpResponseFutureCallback implements FutureCallback<HttpResponse> {
        private final HttpUriRequest request;
        private final ListenableFutureCallbackRegistry<ClientHttpResponse> callbacks = new ListenableFutureCallbackRegistry<>();

        public HttpResponseFutureCallback(HttpUriRequest request) {
            this.request = request;
        }

        public void addCallback(ListenableFutureCallback<? super ClientHttpResponse> callback) {
            this.callbacks.addCallback(callback);
        }

        public void addSuccessCallback(SuccessCallback<? super ClientHttpResponse> callback) {
            this.callbacks.addSuccessCallback(callback);
        }

        public void addFailureCallback(FailureCallback callback) {
            this.callbacks.addFailureCallback(callback);
        }

        public void completed(HttpResponse result) {
            this.callbacks.success(new HttpComponentsAsyncClientHttpResponse(result));
        }

        public void failed(Exception ex) {
            this.callbacks.failure(ex);
        }

        public void cancelled() {
            this.request.abort();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/HttpComponentsAsyncClientHttpRequest$ClientHttpResponseFuture.class */
    private static class ClientHttpResponseFuture extends FutureAdapter<ClientHttpResponse, HttpResponse> implements ListenableFuture<ClientHttpResponse> {
        private final HttpResponseFutureCallback callback;

        public ClientHttpResponseFuture(Future<HttpResponse> response, HttpResponseFutureCallback callback) {
            super(response);
            this.callback = callback;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.util.concurrent.FutureAdapter
        public ClientHttpResponse adapt(HttpResponse response) {
            return new HttpComponentsAsyncClientHttpResponse(response);
        }

        @Override // org.springframework.util.concurrent.ListenableFuture
        public void addCallback(ListenableFutureCallback<? super ClientHttpResponse> callback) {
            this.callback.addCallback(callback);
        }

        @Override // org.springframework.util.concurrent.ListenableFuture
        public void addCallback(SuccessCallback<? super ClientHttpResponse> successCallback, FailureCallback failureCallback) {
            this.callback.addSuccessCallback(successCallback);
            this.callback.addFailureCallback(failureCallback);
        }
    }
}