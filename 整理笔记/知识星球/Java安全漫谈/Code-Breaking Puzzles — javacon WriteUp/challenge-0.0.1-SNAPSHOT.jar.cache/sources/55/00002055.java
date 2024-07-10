package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.concurrent.ListenableFuture;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/InterceptingAsyncClientHttpRequest.class */
public class InterceptingAsyncClientHttpRequest extends AbstractBufferingAsyncClientHttpRequest {
    private AsyncClientHttpRequestFactory requestFactory;
    private List<AsyncClientHttpRequestInterceptor> interceptors;
    private URI uri;
    private HttpMethod httpMethod;

    public InterceptingAsyncClientHttpRequest(AsyncClientHttpRequestFactory requestFactory, List<AsyncClientHttpRequestInterceptor> interceptors, URI uri, HttpMethod httpMethod) {
        this.requestFactory = requestFactory;
        this.interceptors = interceptors;
        this.uri = uri;
        this.httpMethod = httpMethod;
    }

    @Override // org.springframework.http.client.AbstractBufferingAsyncClientHttpRequest
    protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers, byte[] body) throws IOException {
        return new AsyncRequestExecution().executeAsync(this, body);
    }

    @Override // org.springframework.http.HttpRequest
    public HttpMethod getMethod() {
        return this.httpMethod;
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return this.httpMethod.name();
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return this.uri;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/InterceptingAsyncClientHttpRequest$AsyncRequestExecution.class */
    private class AsyncRequestExecution implements AsyncClientHttpRequestExecution {
        private Iterator<AsyncClientHttpRequestInterceptor> iterator;

        public AsyncRequestExecution() {
            InterceptingAsyncClientHttpRequest.this = r4;
            this.iterator = r4.interceptors.iterator();
        }

        @Override // org.springframework.http.client.AsyncClientHttpRequestExecution
        public ListenableFuture<ClientHttpResponse> executeAsync(HttpRequest request, byte[] body) throws IOException {
            if (this.iterator.hasNext()) {
                AsyncClientHttpRequestInterceptor interceptor = this.iterator.next();
                return interceptor.intercept(request, body, this);
            }
            URI uri = request.getURI();
            HttpMethod method = request.getMethod();
            HttpHeaders headers = request.getHeaders();
            Assert.state(method != null, "No standard HTTP method");
            AsyncClientHttpRequest delegate = InterceptingAsyncClientHttpRequest.this.requestFactory.createAsyncRequest(uri, method);
            delegate.getHeaders().putAll(headers);
            if (body.length > 0) {
                StreamUtils.copy(body, delegate.getBody());
            }
            return delegate.executeAsync();
        }
    }
}