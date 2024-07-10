package org.springframework.web.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.InterceptingHttpAccessor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.smile.MappingJackson2SmileHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.util.AbstractUriTemplateHandler;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/RestTemplate.class */
public class RestTemplate extends InterceptingHttpAccessor implements RestOperations {
    private static boolean romePresent;
    private static final boolean jaxb2Present;
    private static final boolean jackson2Present;
    private static final boolean jackson2XmlPresent;
    private static final boolean jackson2SmilePresent;
    private static final boolean jackson2CborPresent;
    private static final boolean gsonPresent;
    private static final boolean jsonbPresent;
    private final List<HttpMessageConverter<?>> messageConverters;
    private ResponseErrorHandler errorHandler;
    private UriTemplateHandler uriTemplateHandler;
    private final ResponseExtractor<HttpHeaders> headersExtractor;

    static {
        ClassLoader classLoader = RestTemplate.class.getClassLoader();
        romePresent = ClassUtils.isPresent("com.rometools.rome.feed.WireFeed", classLoader);
        jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder", classLoader);
        jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader);
        jackson2XmlPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", classLoader);
        jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", classLoader);
        jackson2CborPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.cbor.CBORFactory", classLoader);
        gsonPresent = ClassUtils.isPresent("com.google.gson.Gson", classLoader);
        jsonbPresent = ClassUtils.isPresent("javax.json.bind.Jsonb", classLoader);
    }

    public RestTemplate() {
        this.messageConverters = new ArrayList();
        this.errorHandler = new DefaultResponseErrorHandler();
        this.headersExtractor = new HeadersExtractor();
        this.messageConverters.add(new ByteArrayHttpMessageConverter());
        this.messageConverters.add(new StringHttpMessageConverter());
        this.messageConverters.add(new ResourceHttpMessageConverter(false));
        try {
            this.messageConverters.add(new SourceHttpMessageConverter());
        } catch (Error e) {
        }
        this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        if (romePresent) {
            this.messageConverters.add(new AtomFeedHttpMessageConverter());
            this.messageConverters.add(new RssChannelHttpMessageConverter());
        }
        if (jackson2XmlPresent) {
            this.messageConverters.add(new MappingJackson2XmlHttpMessageConverter());
        } else if (jaxb2Present) {
            this.messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
        }
        if (jackson2Present) {
            this.messageConverters.add(new MappingJackson2HttpMessageConverter());
        } else if (gsonPresent) {
            this.messageConverters.add(new GsonHttpMessageConverter());
        } else if (jsonbPresent) {
            this.messageConverters.add(new JsonbHttpMessageConverter());
        }
        if (jackson2SmilePresent) {
            this.messageConverters.add(new MappingJackson2SmileHttpMessageConverter());
        }
        if (jackson2CborPresent) {
            this.messageConverters.add(new MappingJackson2CborHttpMessageConverter());
        }
        this.uriTemplateHandler = initUriTemplateHandler();
    }

    public RestTemplate(ClientHttpRequestFactory requestFactory) {
        this();
        setRequestFactory(requestFactory);
    }

    public RestTemplate(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = new ArrayList();
        this.errorHandler = new DefaultResponseErrorHandler();
        this.headersExtractor = new HeadersExtractor();
        Assert.notEmpty(messageConverters, "At least one HttpMessageConverter required");
        this.messageConverters.addAll(messageConverters);
        this.uriTemplateHandler = initUriTemplateHandler();
    }

    private static DefaultUriBuilderFactory initUriTemplateHandler() {
        DefaultUriBuilderFactory uriFactory = new DefaultUriBuilderFactory();
        uriFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);
        return uriFactory;
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        Assert.notEmpty(messageConverters, "At least one HttpMessageConverter required");
        if (this.messageConverters != messageConverters) {
            this.messageConverters.clear();
            this.messageConverters.addAll(messageConverters);
        }
    }

    public List<HttpMessageConverter<?>> getMessageConverters() {
        return this.messageConverters;
    }

    public void setErrorHandler(ResponseErrorHandler errorHandler) {
        Assert.notNull(errorHandler, "ResponseErrorHandler must not be null");
        this.errorHandler = errorHandler;
    }

    public ResponseErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void setDefaultUriVariables(Map<String, ?> uriVars) {
        if (this.uriTemplateHandler instanceof DefaultUriBuilderFactory) {
            ((DefaultUriBuilderFactory) this.uriTemplateHandler).setDefaultUriVariables(uriVars);
        } else if (this.uriTemplateHandler instanceof AbstractUriTemplateHandler) {
            ((AbstractUriTemplateHandler) this.uriTemplateHandler).setDefaultUriVariables(uriVars);
        } else {
            throw new IllegalArgumentException("This property is not supported with the configured UriTemplateHandler.");
        }
    }

    public void setUriTemplateHandler(UriTemplateHandler handler) {
        Assert.notNull(handler, "UriTemplateHandler must not be null");
        this.uriTemplateHandler = handler;
    }

    public UriTemplateHandler getUriTemplateHandler() {
        return this.uriTemplateHandler;
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
        HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters(), this.logger);
        return (T) execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
        HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters(), this.logger);
        return (T) execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T getForObject(URI url, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
        HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters(), this.logger);
        return (T) execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull(execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull(execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull(execute(url, HttpMethod.GET, requestCallback, responseExtractor));
    }

    @Override // org.springframework.web.client.RestOperations
    public HttpHeaders headForHeaders(String url, Object... uriVariables) throws RestClientException {
        return (HttpHeaders) nonNull(execute(url, HttpMethod.HEAD, (RequestCallback) null, headersExtractor(), uriVariables));
    }

    @Override // org.springframework.web.client.RestOperations
    public HttpHeaders headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
        return (HttpHeaders) nonNull(execute(url, HttpMethod.HEAD, (RequestCallback) null, headersExtractor(), uriVariables));
    }

    @Override // org.springframework.web.client.RestOperations
    public HttpHeaders headForHeaders(URI url) throws RestClientException {
        return (HttpHeaders) nonNull(execute(url, HttpMethod.HEAD, null, headersExtractor()));
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public URI postForLocation(String url, @Nullable Object request, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request);
        HttpHeaders headers = (HttpHeaders) execute(url, HttpMethod.POST, requestCallback, headersExtractor(), uriVariables);
        if (headers != null) {
            return headers.getLocation();
        }
        return null;
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public URI postForLocation(String url, @Nullable Object request, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request);
        HttpHeaders headers = (HttpHeaders) execute(url, HttpMethod.POST, requestCallback, headersExtractor(), uriVariables);
        if (headers != null) {
            return headers.getLocation();
        }
        return null;
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public URI postForLocation(URI url, @Nullable Object request) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request);
        HttpHeaders headers = (HttpHeaders) execute(url, HttpMethod.POST, requestCallback, headersExtractor());
        if (headers != null) {
            return headers.getLocation();
        }
        return null;
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T postForObject(String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request, responseType);
        HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters(), this.logger);
        return (T) execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T postForObject(String url, @Nullable Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request, responseType);
        HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters(), this.logger);
        return (T) execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T postForObject(URI url, @Nullable Object request, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request, responseType);
        HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>((Class) responseType, getMessageConverters());
        return (T) execute(url, HttpMethod.POST, requestCallback, responseExtractor);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull(execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull(execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> postForEntity(URI url, @Nullable Object request, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull(execute(url, HttpMethod.POST, requestCallback, responseExtractor));
    }

    @Override // org.springframework.web.client.RestOperations
    public void put(String url, @Nullable Object request, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request);
        execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor) null, uriVariables);
    }

    @Override // org.springframework.web.client.RestOperations
    public void put(String url, @Nullable Object request, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request);
        execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor) null, uriVariables);
    }

    @Override // org.springframework.web.client.RestOperations
    public void put(URI url, @Nullable Object request) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request);
        execute(url, HttpMethod.PUT, requestCallback, null);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T patchForObject(String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request, responseType);
        HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters(), this.logger);
        return (T) execute(url, HttpMethod.PATCH, requestCallback, responseExtractor, uriVariables);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T patchForObject(String url, @Nullable Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request, responseType);
        HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters(), this.logger);
        return (T) execute(url, HttpMethod.PATCH, requestCallback, responseExtractor, uriVariables);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T patchForObject(URI url, @Nullable Object request, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(request, responseType);
        HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>((Class) responseType, getMessageConverters());
        return (T) execute(url, HttpMethod.PATCH, requestCallback, responseExtractor);
    }

    @Override // org.springframework.web.client.RestOperations
    public void delete(String url, Object... uriVariables) throws RestClientException {
        execute(url, HttpMethod.DELETE, (RequestCallback) null, (ResponseExtractor) null, uriVariables);
    }

    @Override // org.springframework.web.client.RestOperations
    public void delete(String url, Map<String, ?> uriVariables) throws RestClientException {
        execute(url, HttpMethod.DELETE, (RequestCallback) null, (ResponseExtractor) null, uriVariables);
    }

    @Override // org.springframework.web.client.RestOperations
    public void delete(URI url) throws RestClientException {
        execute(url, HttpMethod.DELETE, null, null);
    }

    @Override // org.springframework.web.client.RestOperations
    public Set<HttpMethod> optionsForAllow(String url, Object... uriVariables) throws RestClientException {
        ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
        HttpHeaders headers = (HttpHeaders) execute(url, HttpMethod.OPTIONS, (RequestCallback) null, headersExtractor, uriVariables);
        return headers != null ? headers.getAllow() : Collections.emptySet();
    }

    @Override // org.springframework.web.client.RestOperations
    public Set<HttpMethod> optionsForAllow(String url, Map<String, ?> uriVariables) throws RestClientException {
        ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
        HttpHeaders headers = (HttpHeaders) execute(url, HttpMethod.OPTIONS, (RequestCallback) null, headersExtractor, uriVariables);
        return headers != null ? headers.getAllow() : Collections.emptySet();
    }

    @Override // org.springframework.web.client.RestOperations
    public Set<HttpMethod> optionsForAllow(URI url) throws RestClientException {
        ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
        HttpHeaders headers = (HttpHeaders) execute(url, HttpMethod.OPTIONS, null, headersExtractor);
        return headers != null ? headers.getAllow() : Collections.emptySet();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull(execute(url, method, requestCallback, responseExtractor, uriVariables));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull(execute(url, method, requestCallback, responseExtractor, uriVariables));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull(execute(url, method, requestCallback, responseExtractor));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {
        Type type = responseType.getType();
        RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
        return (ResponseEntity) nonNull(execute(url, method, requestCallback, responseExtractor, uriVariables));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        Type type = responseType.getType();
        RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
        return (ResponseEntity) nonNull(execute(url, method, requestCallback, responseExtractor, uriVariables));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException {
        Type type = responseType.getType();
        RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
        return (ResponseEntity) nonNull(execute(url, method, requestCallback, responseExtractor));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
        return (ResponseEntity) nonNull(doExecute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, responseExtractor));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.client.RestOperations
    public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException {
        Type type = responseType.getType();
        RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
        return (ResponseEntity) nonNull(doExecute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, responseExtractor));
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T execute(String url, HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Object... uriVariables) throws RestClientException {
        URI expanded = getUriTemplateHandler().expand(url, uriVariables);
        return (T) doExecute(expanded, method, requestCallback, responseExtractor);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T execute(String url, HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException {
        URI expanded = getUriTemplateHandler().expand(url, uriVariables);
        return (T) doExecute(expanded, method, requestCallback, responseExtractor);
    }

    @Override // org.springframework.web.client.RestOperations
    @Nullable
    public <T> T execute(URI url, HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
        return (T) doExecute(url, method, requestCallback, responseExtractor);
    }

    @Nullable
    protected <T> T doExecute(URI url, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
        Assert.notNull(url, "URI is required");
        Assert.notNull(method, "HttpMethod is required");
        ClientHttpResponse response = null;
        try {
            try {
                ClientHttpRequest request = createRequest(url, method);
                if (requestCallback != null) {
                    requestCallback.doWithRequest(request);
                }
                response = request.execute();
                handleResponse(url, method, response);
                T extractData = responseExtractor != null ? responseExtractor.extractData(response) : null;
                if (response != null) {
                    response.close();
                }
                return extractData;
            } catch (IOException ex) {
                String resource = url.toString();
                String query = url.getRawQuery();
                throw new ResourceAccessException("I/O error on " + method.name() + " request for \"" + (query != null ? resource.substring(0, resource.indexOf(63)) : resource) + "\": " + ex.getMessage(), ex);
            }
        } catch (Throwable th) {
            if (response != null) {
                response.close();
            }
            throw th;
        }
    }

    protected void handleResponse(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        ResponseErrorHandler errorHandler = getErrorHandler();
        boolean hasError = errorHandler.hasError(response);
        if (this.logger.isDebugEnabled()) {
            try {
                int code = response.getRawStatusCode();
                HttpStatus status = HttpStatus.resolve(code);
                this.logger.debug("Response " + (status != null ? status : Integer.valueOf(code)));
            } catch (IOException e) {
            }
        }
        if (hasError) {
            errorHandler.handleError(url, method, response);
        }
    }

    public <T> RequestCallback acceptHeaderRequestCallback(Class<T> responseType) {
        return new AcceptHeaderRequestCallback(responseType);
    }

    public <T> RequestCallback httpEntityCallback(@Nullable Object requestBody) {
        return new HttpEntityRequestCallback(this, requestBody);
    }

    public <T> RequestCallback httpEntityCallback(@Nullable Object requestBody, Type responseType) {
        return new HttpEntityRequestCallback(requestBody, responseType);
    }

    public <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(Type responseType) {
        return new ResponseEntityResponseExtractor(responseType);
    }

    public ResponseExtractor<HttpHeaders> headersExtractor() {
        return this.headersExtractor;
    }

    private static <T> T nonNull(@Nullable T result) {
        Assert.state(result != null, "No result");
        return result;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/RestTemplate$AcceptHeaderRequestCallback.class */
    public class AcceptHeaderRequestCallback implements RequestCallback {
        @Nullable
        private final Type responseType;

        public AcceptHeaderRequestCallback(@Nullable Type responseType) {
            RestTemplate.this = r4;
            this.responseType = responseType;
        }

        @Override // org.springframework.web.client.RequestCallback
        public void doWithRequest(ClientHttpRequest request) throws IOException {
            if (this.responseType != null) {
                List<MediaType> allSupportedMediaTypes = (List) RestTemplate.this.getMessageConverters().stream().filter(converter -> {
                    return canReadResponse(this.responseType, converter);
                }).flatMap(this::getSupportedMediaTypes).distinct().sorted(MediaType.SPECIFICITY_COMPARATOR).collect(Collectors.toList());
                if (RestTemplate.this.logger.isDebugEnabled()) {
                    RestTemplate.this.logger.debug("Accept=" + allSupportedMediaTypes);
                }
                request.getHeaders().setAccept(allSupportedMediaTypes);
            }
        }

        private boolean canReadResponse(Type responseType, HttpMessageConverter<?> converter) {
            Class<?> responseClass = responseType instanceof Class ? (Class) responseType : null;
            if (responseClass != null) {
                return converter.canRead(responseClass, null);
            }
            if (converter instanceof GenericHttpMessageConverter) {
                GenericHttpMessageConverter<?> genericConverter = (GenericHttpMessageConverter) converter;
                return genericConverter.canRead(responseType, null, null);
            }
            return false;
        }

        private Stream<MediaType> getSupportedMediaTypes(HttpMessageConverter<?> messageConverter) {
            return messageConverter.getSupportedMediaTypes().stream().map(mediaType -> {
                if (mediaType.getCharset() != null) {
                    return new MediaType(mediaType.getType(), mediaType.getSubtype());
                }
                return mediaType;
            });
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/RestTemplate$HttpEntityRequestCallback.class */
    public class HttpEntityRequestCallback extends AcceptHeaderRequestCallback {
        private final HttpEntity<?> requestEntity;

        public HttpEntityRequestCallback(@Nullable RestTemplate restTemplate, Object requestBody) {
            this(requestBody, null);
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public HttpEntityRequestCallback(@Nullable Object requestBody, @Nullable Type responseType) {
            super(responseType);
            RestTemplate.this = r6;
            if (requestBody instanceof HttpEntity) {
                this.requestEntity = (HttpEntity) requestBody;
            } else if (requestBody != null) {
                this.requestEntity = new HttpEntity<>(requestBody);
            } else {
                this.requestEntity = HttpEntity.EMPTY;
            }
        }

        @Override // org.springframework.web.client.RestTemplate.AcceptHeaderRequestCallback, org.springframework.web.client.RequestCallback
        public void doWithRequest(ClientHttpRequest httpRequest) throws IOException {
            super.doWithRequest(httpRequest);
            Object requestBody = this.requestEntity.getBody();
            if (requestBody == null) {
                HttpHeaders httpHeaders = httpRequest.getHeaders();
                HttpHeaders requestHeaders = this.requestEntity.getHeaders();
                if (!requestHeaders.isEmpty()) {
                    requestHeaders.forEach(key, values -> {
                        httpHeaders.put(key, (List<String>) new LinkedList(values));
                    });
                }
                if (httpHeaders.getContentLength() < 0) {
                    httpHeaders.setContentLength(0L);
                    return;
                }
                return;
            }
            Class<?> requestBodyClass = requestBody.getClass();
            Type requestBodyType = this.requestEntity instanceof RequestEntity ? ((RequestEntity) this.requestEntity).getType() : requestBodyClass;
            HttpHeaders httpHeaders2 = httpRequest.getHeaders();
            HttpHeaders requestHeaders2 = this.requestEntity.getHeaders();
            MediaType requestContentType = requestHeaders2.getContentType();
            for (HttpMessageConverter<?> messageConverter : RestTemplate.this.getMessageConverters()) {
                if (messageConverter instanceof GenericHttpMessageConverter) {
                    GenericHttpMessageConverter<Object> genericConverter = (GenericHttpMessageConverter) messageConverter;
                    if (genericConverter.canWrite(requestBodyType, requestBodyClass, requestContentType)) {
                        if (!requestHeaders2.isEmpty()) {
                            requestHeaders2.forEach(key2, values2 -> {
                                httpHeaders2.put(key2, (List<String>) new LinkedList(values2));
                            });
                        }
                        logBody(requestBody, requestContentType, genericConverter);
                        genericConverter.write(requestBody, requestBodyType, requestContentType, httpRequest);
                        return;
                    }
                } else if (messageConverter.canWrite(requestBodyClass, requestContentType)) {
                    if (!requestHeaders2.isEmpty()) {
                        requestHeaders2.forEach(key3, values3 -> {
                            httpHeaders2.put(key3, (List<String>) new LinkedList(values3));
                        });
                    }
                    logBody(requestBody, requestContentType, messageConverter);
                    messageConverter.write(requestBody, requestContentType, httpRequest);
                    return;
                }
            }
            String message = "No HttpMessageConverter for [" + requestBodyClass.getName() + "]";
            if (requestContentType != null) {
                message = message + " and content type [" + requestContentType + "]";
            }
            throw new RestClientException(message);
        }

        private void logBody(Object body, @Nullable MediaType mediaType, HttpMessageConverter<?> converter) {
            if (RestTemplate.this.logger.isDebugEnabled()) {
                if (mediaType != null) {
                    RestTemplate.this.logger.debug("Writing [" + body + "] as \"" + mediaType + "\"");
                    return;
                }
                String classname = converter.getClass().getName();
                RestTemplate.this.logger.debug("Writing [" + body + "] with " + classname);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/RestTemplate$ResponseEntityResponseExtractor.class */
    public class ResponseEntityResponseExtractor<T> implements ResponseExtractor<ResponseEntity<T>> {
        @Nullable
        private final HttpMessageConverterExtractor<T> delegate;

        public ResponseEntityResponseExtractor(@Nullable Type responseType) {
            RestTemplate.this = r8;
            if (responseType != null && Void.class != responseType) {
                this.delegate = new HttpMessageConverterExtractor<>(responseType, r8.getMessageConverters(), r8.logger);
            } else {
                this.delegate = null;
            }
        }

        @Override // org.springframework.web.client.ResponseExtractor
        public ResponseEntity<T> extractData(ClientHttpResponse response) throws IOException {
            if (this.delegate != null) {
                T body = this.delegate.extractData(response);
                return ResponseEntity.status(response.getRawStatusCode()).headers(response.getHeaders()).body(body);
            }
            return ResponseEntity.status(response.getRawStatusCode()).headers(response.getHeaders()).build();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/RestTemplate$HeadersExtractor.class */
    public static class HeadersExtractor implements ResponseExtractor<HttpHeaders> {
        private HeadersExtractor() {
        }

        @Override // org.springframework.web.client.ResponseExtractor
        public HttpHeaders extractData(ClientHttpResponse response) {
            return response.getHeaders();
        }
    }
}