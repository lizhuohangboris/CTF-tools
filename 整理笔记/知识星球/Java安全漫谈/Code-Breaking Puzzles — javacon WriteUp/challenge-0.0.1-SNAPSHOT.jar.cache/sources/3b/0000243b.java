package org.springframework.web.client;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.InterceptingAsyncHttpAccessor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;
import org.springframework.web.util.AbstractUriTemplateHandler;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/AsyncRestTemplate.class */
public class AsyncRestTemplate extends InterceptingAsyncHttpAccessor implements AsyncRestOperations {
    private final RestTemplate syncTemplate;

    public AsyncRestTemplate() {
        this(new SimpleAsyncTaskExecutor());
    }

    public AsyncRestTemplate(AsyncListenableTaskExecutor taskExecutor) {
        Assert.notNull(taskExecutor, "AsyncTaskExecutor must not be null");
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setTaskExecutor(taskExecutor);
        this.syncTemplate = new RestTemplate(requestFactory);
        setAsyncRequestFactory(requestFactory);
    }

    public AsyncRestTemplate(AsyncClientHttpRequestFactory asyncRequestFactory) {
        this(asyncRequestFactory, (ClientHttpRequestFactory) asyncRequestFactory);
    }

    public AsyncRestTemplate(AsyncClientHttpRequestFactory asyncRequestFactory, ClientHttpRequestFactory syncRequestFactory) {
        this(asyncRequestFactory, new RestTemplate(syncRequestFactory));
    }

    public AsyncRestTemplate(AsyncClientHttpRequestFactory requestFactory, RestTemplate restTemplate) {
        Assert.notNull(restTemplate, "RestTemplate must not be null");
        this.syncTemplate = restTemplate;
        setAsyncRequestFactory(requestFactory);
    }

    public void setErrorHandler(ResponseErrorHandler errorHandler) {
        this.syncTemplate.setErrorHandler(errorHandler);
    }

    public ResponseErrorHandler getErrorHandler() {
        return this.syncTemplate.getErrorHandler();
    }

    public void setDefaultUriVariables(Map<String, ?> defaultUriVariables) {
        UriTemplateHandler handler = this.syncTemplate.getUriTemplateHandler();
        if (handler instanceof DefaultUriBuilderFactory) {
            ((DefaultUriBuilderFactory) handler).setDefaultUriVariables(defaultUriVariables);
        } else if (handler instanceof AbstractUriTemplateHandler) {
            ((AbstractUriTemplateHandler) handler).setDefaultUriVariables(defaultUriVariables);
        } else {
            throw new IllegalArgumentException("This property is not supported with the configured UriTemplateHandler.");
        }
    }

    public void setUriTemplateHandler(UriTemplateHandler handler) {
        this.syncTemplate.setUriTemplateHandler(handler);
    }

    public UriTemplateHandler getUriTemplateHandler() {
        return this.syncTemplate.getUriTemplateHandler();
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public RestOperations getRestOperations() {
        return this.syncTemplate;
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.syncTemplate.setMessageConverters(messageConverters);
    }

    public List<HttpMessageConverter<?>> getMessageConverters() {
        return this.syncTemplate.getMessageConverters();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.AsyncRestOperations
    public <T> ListenableFuture<ResponseEntity<T>> getForEntity(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
        AsyncRequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.AsyncRestOperations
    public <T> ListenableFuture<ResponseEntity<T>> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        AsyncRequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.AsyncRestOperations
    public <T> ListenableFuture<ResponseEntity<T>> getForEntity(URI url, Class<T> responseType) throws RestClientException {
        AsyncRequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public ListenableFuture<HttpHeaders> headForHeaders(String url, Object... uriVariables) throws RestClientException {
        ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
        return execute(url, HttpMethod.HEAD, (AsyncRequestCallback) null, headersExtractor, uriVariables);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public ListenableFuture<HttpHeaders> headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
        ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
        return execute(url, HttpMethod.HEAD, (AsyncRequestCallback) null, headersExtractor, uriVariables);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public ListenableFuture<HttpHeaders> headForHeaders(URI url) throws RestClientException {
        ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
        return execute(url, HttpMethod.HEAD, null, headersExtractor);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public ListenableFuture<URI> postForLocation(String url, @Nullable HttpEntity<?> request, Object... uriVars) throws RestClientException {
        AsyncRequestCallback callback = httpEntityCallback(request);
        ResponseExtractor<HttpHeaders> extractor = headersExtractor();
        ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.POST, callback, extractor, uriVars);
        return adaptToLocationHeader(future);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public ListenableFuture<URI> postForLocation(String url, @Nullable HttpEntity<?> request, Map<String, ?> uriVars) throws RestClientException {
        AsyncRequestCallback callback = httpEntityCallback(request);
        ResponseExtractor<HttpHeaders> extractor = headersExtractor();
        ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.POST, callback, extractor, uriVars);
        return adaptToLocationHeader(future);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public ListenableFuture<URI> postForLocation(URI url, @Nullable HttpEntity<?> request) throws RestClientException {
        AsyncRequestCallback callback = httpEntityCallback(request);
        ResponseExtractor<HttpHeaders> extractor = headersExtractor();
        ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.POST, callback, extractor);
        return adaptToLocationHeader(future);
    }

    private static ListenableFuture<URI> adaptToLocationHeader(ListenableFuture<HttpHeaders> future) {
        return new ListenableFutureAdapter<URI, HttpHeaders>(future) { // from class: org.springframework.web.client.AsyncRestTemplate.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.springframework.util.concurrent.FutureAdapter
            @Nullable
            public URI adapt(HttpHeaders headers) throws ExecutionException {
                return headers.getLocation();
            }
        };
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.AsyncRestOperations
    public <T> ListenableFuture<ResponseEntity<T>> postForEntity(String url, @Nullable HttpEntity<?> request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        AsyncRequestCallback requestCallback = httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.AsyncRestOperations
    public <T> ListenableFuture<ResponseEntity<T>> postForEntity(String url, @Nullable HttpEntity<?> request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        AsyncRequestCallback requestCallback = httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.AsyncRestOperations
    public <T> ListenableFuture<ResponseEntity<T>> postForEntity(URI url, @Nullable HttpEntity<?> request, Class<T> responseType) throws RestClientException {
        AsyncRequestCallback requestCallback = httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return execute(url, HttpMethod.POST, requestCallback, responseExtractor);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public ListenableFuture<?> put(String url, @Nullable HttpEntity<?> request, Object... uriVars) throws RestClientException {
        AsyncRequestCallback requestCallback = httpEntityCallback(request);
        return execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor) null, uriVars);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public ListenableFuture<?> put(String url, @Nullable HttpEntity<?> request, Map<String, ?> uriVars) throws RestClientException {
        AsyncRequestCallback requestCallback = httpEntityCallback(request);
        return execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor) null, uriVars);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public ListenableFuture<?> put(URI url, @Nullable HttpEntity<?> request) throws RestClientException {
        AsyncRequestCallback requestCallback = httpEntityCallback(request);
        return execute(url, HttpMethod.PUT, requestCallback, null);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public ListenableFuture<?> delete(String url, Object... uriVariables) throws RestClientException {
        return execute(url, HttpMethod.DELETE, (AsyncRequestCallback) null, (ResponseExtractor) null, uriVariables);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public ListenableFuture<?> delete(String url, Map<String, ?> uriVariables) throws RestClientException {
        return execute(url, HttpMethod.DELETE, (AsyncRequestCallback) null, (ResponseExtractor) null, uriVariables);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public ListenableFuture<?> delete(URI url) throws RestClientException {
        return execute(url, HttpMethod.DELETE, null, null);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public ListenableFuture<Set<HttpMethod>> optionsForAllow(String url, Object... uriVars) throws RestClientException {
        ResponseExtractor<HttpHeaders> extractor = headersExtractor();
        ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.OPTIONS, (AsyncRequestCallback) null, extractor, uriVars);
        return adaptToAllowHeader(future);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public ListenableFuture<Set<HttpMethod>> optionsForAllow(String url, Map<String, ?> uriVars) throws RestClientException {
        ResponseExtractor<HttpHeaders> extractor = headersExtractor();
        ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.OPTIONS, (AsyncRequestCallback) null, extractor, uriVars);
        return adaptToAllowHeader(future);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public ListenableFuture<Set<HttpMethod>> optionsForAllow(URI url) throws RestClientException {
        ResponseExtractor<HttpHeaders> extractor = headersExtractor();
        ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.OPTIONS, null, extractor);
        return adaptToAllowHeader(future);
    }

    private static ListenableFuture<Set<HttpMethod>> adaptToAllowHeader(ListenableFuture<HttpHeaders> future) {
        return new ListenableFutureAdapter<Set<HttpMethod>, HttpHeaders>(future) { // from class: org.springframework.web.client.AsyncRestTemplate.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.springframework.util.concurrent.FutureAdapter
            public Set<HttpMethod> adapt(HttpHeaders headers) throws ExecutionException {
                return headers.getAllow();
            }
        };
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.AsyncRestOperations
    public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws RestClientException {
        AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.AsyncRestOperations
    public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.AsyncRestOperations
    public <T> ListenableFuture<ResponseEntity<T>> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
        AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return execute(url, method, requestCallback, responseExtractor);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.AsyncRestOperations
    public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {
        Type type = responseType.getType();
        AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
        return execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.AsyncRestOperations
    public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        Type type = responseType.getType();
        AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
        return execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.AsyncRestOperations
    public <T> ListenableFuture<ResponseEntity<T>> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException {
        Type type = responseType.getType();
        AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
        return execute(url, method, requestCallback, responseExtractor);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public <T> ListenableFuture<T> execute(String url, HttpMethod method, @Nullable AsyncRequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Object... uriVariables) throws RestClientException {
        URI expanded = getUriTemplateHandler().expand(url, uriVariables);
        return doExecute(expanded, method, requestCallback, responseExtractor);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public <T> ListenableFuture<T> execute(String url, HttpMethod method, @Nullable AsyncRequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException {
        URI expanded = getUriTemplateHandler().expand(url, uriVariables);
        return doExecute(expanded, method, requestCallback, responseExtractor);
    }

    @Override // org.springframework.web.client.AsyncRestOperations
    public <T> ListenableFuture<T> execute(URI url, HttpMethod method, @Nullable AsyncRequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
        return doExecute(url, method, requestCallback, responseExtractor);
    }

    protected <T> ListenableFuture<T> doExecute(URI url, HttpMethod method, @Nullable AsyncRequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
        Assert.notNull(url, "'url' must not be null");
        Assert.notNull(method, "'method' must not be null");
        try {
            AsyncClientHttpRequest request = createAsyncRequest(url, method);
            if (requestCallback != null) {
                requestCallback.doWithRequest(request);
            }
            ListenableFuture<ClientHttpResponse> responseFuture = request.executeAsync();
            return new ResponseExtractorFuture(method, url, responseFuture, responseExtractor);
        } catch (IOException ex) {
            throw new ResourceAccessException("I/O error on " + method.name() + " request for \"" + url + "\":" + ex.getMessage(), ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logResponseStatus(HttpMethod method, URI url, ClientHttpResponse response) {
        if (this.logger.isDebugEnabled()) {
            try {
                this.logger.debug("Async " + method.name() + " request for \"" + url + "\" resulted in " + response.getRawStatusCode() + " (" + response.getStatusText() + ")");
            } catch (IOException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleResponseError(HttpMethod method, URI url, ClientHttpResponse response) throws IOException {
        if (this.logger.isWarnEnabled()) {
            try {
                this.logger.warn("Async " + method.name() + " request for \"" + url + "\" resulted in " + response.getRawStatusCode() + " (" + response.getStatusText() + "); invoking error handler");
            } catch (IOException e) {
            }
        }
        getErrorHandler().handleError(url, method, response);
    }

    protected <T> AsyncRequestCallback acceptHeaderRequestCallback(Class<T> responseType) {
        return new AsyncRequestCallbackAdapter(this.syncTemplate.acceptHeaderRequestCallback(responseType));
    }

    protected <T> AsyncRequestCallback httpEntityCallback(@Nullable HttpEntity<T> requestBody) {
        return new AsyncRequestCallbackAdapter(this.syncTemplate.httpEntityCallback(requestBody));
    }

    protected <T> AsyncRequestCallback httpEntityCallback(@Nullable HttpEntity<T> request, Type responseType) {
        return new AsyncRequestCallbackAdapter(this.syncTemplate.httpEntityCallback(request, responseType));
    }

    protected <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(Type responseType) {
        return this.syncTemplate.responseEntityExtractor(responseType);
    }

    protected ResponseExtractor<HttpHeaders> headersExtractor() {
        return this.syncTemplate.headersExtractor();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/AsyncRestTemplate$ResponseExtractorFuture.class */
    public class ResponseExtractorFuture<T> extends ListenableFutureAdapter<T, ClientHttpResponse> {
        private final HttpMethod method;
        private final URI url;
        @Nullable
        private final ResponseExtractor<T> responseExtractor;

        public ResponseExtractorFuture(HttpMethod method, URI url, ListenableFuture<ClientHttpResponse> clientHttpResponseFuture, @Nullable ResponseExtractor<T> responseExtractor) {
            super(clientHttpResponseFuture);
            this.method = method;
            this.url = url;
            this.responseExtractor = responseExtractor;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.util.concurrent.FutureAdapter
        @Nullable
        public final T adapt(ClientHttpResponse response) throws ExecutionException {
            try {
                if (!AsyncRestTemplate.this.getErrorHandler().hasError(response)) {
                    AsyncRestTemplate.this.logResponseStatus(this.method, this.url, response);
                } else {
                    AsyncRestTemplate.this.handleResponseError(this.method, this.url, response);
                }
                T convertResponse = convertResponse(response);
                response.close();
                return convertResponse;
            } finally {
            }
        }

        @Nullable
        protected T convertResponse(ClientHttpResponse response) throws IOException {
            if (this.responseExtractor != null) {
                return this.responseExtractor.extractData(response);
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/AsyncRestTemplate$AsyncRequestCallbackAdapter.class */
    public static class AsyncRequestCallbackAdapter implements AsyncRequestCallback {
        private final RequestCallback adaptee;

        public AsyncRequestCallbackAdapter(RequestCallback requestCallback) {
            this.adaptee = requestCallback;
        }

        @Override // org.springframework.web.client.AsyncRequestCallback
        public void doWithRequest(final AsyncClientHttpRequest request) throws IOException {
            this.adaptee.doWithRequest(new ClientHttpRequest() { // from class: org.springframework.web.client.AsyncRestTemplate.AsyncRequestCallbackAdapter.1
                @Override // org.springframework.http.client.ClientHttpRequest
                public ClientHttpResponse execute() throws IOException {
                    throw new UnsupportedOperationException("execute not supported");
                }

                @Override // org.springframework.http.HttpOutputMessage
                public OutputStream getBody() throws IOException {
                    return request.getBody();
                }

                @Override // org.springframework.http.HttpRequest
                @Nullable
                public HttpMethod getMethod() {
                    return request.getMethod();
                }

                @Override // org.springframework.http.HttpRequest
                public String getMethodValue() {
                    return request.getMethodValue();
                }

                @Override // org.springframework.http.HttpRequest
                public URI getURI() {
                    return request.getURI();
                }

                @Override // org.springframework.http.HttpMessage
                public HttpHeaders getHeaders() {
                    return request.getHeaders();
                }
            });
        }
    }
}