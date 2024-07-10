package org.springframework.boot.web.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.beans.BeanUtils;
import org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/client/RestTemplateBuilder.class */
public class RestTemplateBuilder {
    private final boolean detectRequestFactory;
    private final String rootUri;
    private final Set<HttpMessageConverter<?>> messageConverters;
    private final Supplier<ClientHttpRequestFactory> requestFactorySupplier;
    private final UriTemplateHandler uriTemplateHandler;
    private final ResponseErrorHandler errorHandler;
    private final BasicAuthenticationInterceptor basicAuthentication;
    private final Set<RestTemplateCustomizer> restTemplateCustomizers;
    private final RequestFactoryCustomizer requestFactoryCustomizer;
    private final Set<ClientHttpRequestInterceptor> interceptors;

    public RestTemplateBuilder(RestTemplateCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        this.detectRequestFactory = true;
        this.rootUri = null;
        this.messageConverters = null;
        this.requestFactorySupplier = null;
        this.uriTemplateHandler = null;
        this.errorHandler = null;
        this.basicAuthentication = null;
        this.restTemplateCustomizers = Collections.unmodifiableSet(new LinkedHashSet(Arrays.asList(customizers)));
        this.requestFactoryCustomizer = new RequestFactoryCustomizer();
        this.interceptors = Collections.emptySet();
    }

    private RestTemplateBuilder(boolean detectRequestFactory, String rootUri, Set<HttpMessageConverter<?>> messageConverters, Supplier<ClientHttpRequestFactory> requestFactorySupplier, UriTemplateHandler uriTemplateHandler, ResponseErrorHandler errorHandler, BasicAuthenticationInterceptor basicAuthentication, Set<RestTemplateCustomizer> restTemplateCustomizers, RequestFactoryCustomizer requestFactoryCustomizer, Set<ClientHttpRequestInterceptor> interceptors) {
        this.detectRequestFactory = detectRequestFactory;
        this.rootUri = rootUri;
        this.messageConverters = messageConverters;
        this.requestFactorySupplier = requestFactorySupplier;
        this.uriTemplateHandler = uriTemplateHandler;
        this.errorHandler = errorHandler;
        this.basicAuthentication = basicAuthentication;
        this.restTemplateCustomizers = restTemplateCustomizers;
        this.requestFactoryCustomizer = requestFactoryCustomizer;
        this.interceptors = interceptors;
    }

    public RestTemplateBuilder detectRequestFactory(boolean detectRequestFactory) {
        return new RestTemplateBuilder(detectRequestFactory, this.rootUri, this.messageConverters, this.requestFactorySupplier, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.restTemplateCustomizers, this.requestFactoryCustomizer, this.interceptors);
    }

    public RestTemplateBuilder rootUri(String rootUri) {
        return new RestTemplateBuilder(this.detectRequestFactory, rootUri, this.messageConverters, this.requestFactorySupplier, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.restTemplateCustomizers, this.requestFactoryCustomizer, this.interceptors);
    }

    public RestTemplateBuilder messageConverters(HttpMessageConverter<?>... messageConverters) {
        Assert.notNull(messageConverters, "MessageConverters must not be null");
        return messageConverters(Arrays.asList(messageConverters));
    }

    public RestTemplateBuilder messageConverters(Collection<? extends HttpMessageConverter<?>> messageConverters) {
        Assert.notNull(messageConverters, "MessageConverters must not be null");
        return new RestTemplateBuilder(this.detectRequestFactory, this.rootUri, Collections.unmodifiableSet(new LinkedHashSet(messageConverters)), this.requestFactorySupplier, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.restTemplateCustomizers, this.requestFactoryCustomizer, this.interceptors);
    }

    public RestTemplateBuilder additionalMessageConverters(HttpMessageConverter<?>... messageConverters) {
        Assert.notNull(messageConverters, "MessageConverters must not be null");
        return additionalMessageConverters(Arrays.asList(messageConverters));
    }

    public RestTemplateBuilder additionalMessageConverters(Collection<? extends HttpMessageConverter<?>> messageConverters) {
        Assert.notNull(messageConverters, "MessageConverters must not be null");
        return new RestTemplateBuilder(this.detectRequestFactory, this.rootUri, append(this.messageConverters, messageConverters), this.requestFactorySupplier, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.restTemplateCustomizers, this.requestFactoryCustomizer, this.interceptors);
    }

    public RestTemplateBuilder defaultMessageConverters() {
        return new RestTemplateBuilder(this.detectRequestFactory, this.rootUri, Collections.unmodifiableSet(new LinkedHashSet(new RestTemplate().getMessageConverters())), this.requestFactorySupplier, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.restTemplateCustomizers, this.requestFactoryCustomizer, this.interceptors);
    }

    public RestTemplateBuilder interceptors(ClientHttpRequestInterceptor... interceptors) {
        Assert.notNull(interceptors, "interceptors must not be null");
        return interceptors(Arrays.asList(interceptors));
    }

    public RestTemplateBuilder interceptors(Collection<ClientHttpRequestInterceptor> interceptors) {
        Assert.notNull(interceptors, "interceptors must not be null");
        return new RestTemplateBuilder(this.detectRequestFactory, this.rootUri, this.messageConverters, this.requestFactorySupplier, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.restTemplateCustomizers, this.requestFactoryCustomizer, Collections.unmodifiableSet(new LinkedHashSet(interceptors)));
    }

    public RestTemplateBuilder additionalInterceptors(ClientHttpRequestInterceptor... interceptors) {
        Assert.notNull(interceptors, "interceptors must not be null");
        return additionalInterceptors(Arrays.asList(interceptors));
    }

    public RestTemplateBuilder additionalInterceptors(Collection<? extends ClientHttpRequestInterceptor> interceptors) {
        Assert.notNull(interceptors, "interceptors must not be null");
        return new RestTemplateBuilder(this.detectRequestFactory, this.rootUri, this.messageConverters, this.requestFactorySupplier, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.restTemplateCustomizers, this.requestFactoryCustomizer, append(this.interceptors, interceptors));
    }

    public RestTemplateBuilder requestFactory(Class<? extends ClientHttpRequestFactory> requestFactory) {
        Assert.notNull(requestFactory, "RequestFactory must not be null");
        return requestFactory(() -> {
            return createRequestFactory(requestFactory);
        });
    }

    private ClientHttpRequestFactory createRequestFactory(Class<? extends ClientHttpRequestFactory> requestFactory) {
        try {
            Constructor<?> constructor = requestFactory.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            return constructor.newInstance(new Object[0]);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public RestTemplateBuilder requestFactory(Supplier<ClientHttpRequestFactory> requestFactorySupplier) {
        Assert.notNull(requestFactorySupplier, "RequestFactory Supplier must not be null");
        return new RestTemplateBuilder(this.detectRequestFactory, this.rootUri, this.messageConverters, requestFactorySupplier, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.restTemplateCustomizers, this.requestFactoryCustomizer, this.interceptors);
    }

    public RestTemplateBuilder uriTemplateHandler(UriTemplateHandler uriTemplateHandler) {
        Assert.notNull(uriTemplateHandler, "UriTemplateHandler must not be null");
        return new RestTemplateBuilder(this.detectRequestFactory, this.rootUri, this.messageConverters, this.requestFactorySupplier, uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.restTemplateCustomizers, this.requestFactoryCustomizer, this.interceptors);
    }

    public RestTemplateBuilder errorHandler(ResponseErrorHandler errorHandler) {
        Assert.notNull(errorHandler, "ErrorHandler must not be null");
        return new RestTemplateBuilder(this.detectRequestFactory, this.rootUri, this.messageConverters, this.requestFactorySupplier, this.uriTemplateHandler, errorHandler, this.basicAuthentication, this.restTemplateCustomizers, this.requestFactoryCustomizer, this.interceptors);
    }

    public RestTemplateBuilder basicAuthorization(String username, String password) {
        return basicAuthentication(username, password);
    }

    public RestTemplateBuilder basicAuthentication(String username, String password) {
        return new RestTemplateBuilder(this.detectRequestFactory, this.rootUri, this.messageConverters, this.requestFactorySupplier, this.uriTemplateHandler, this.errorHandler, new BasicAuthenticationInterceptor(username, password), this.restTemplateCustomizers, this.requestFactoryCustomizer, this.interceptors);
    }

    public RestTemplateBuilder customizers(RestTemplateCustomizer... restTemplateCustomizers) {
        Assert.notNull(restTemplateCustomizers, "RestTemplateCustomizers must not be null");
        return customizers(Arrays.asList(restTemplateCustomizers));
    }

    public RestTemplateBuilder customizers(Collection<? extends RestTemplateCustomizer> restTemplateCustomizers) {
        Assert.notNull(restTemplateCustomizers, "RestTemplateCustomizers must not be null");
        return new RestTemplateBuilder(this.detectRequestFactory, this.rootUri, this.messageConverters, this.requestFactorySupplier, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, Collections.unmodifiableSet(new LinkedHashSet(restTemplateCustomizers)), this.requestFactoryCustomizer, this.interceptors);
    }

    public RestTemplateBuilder additionalCustomizers(RestTemplateCustomizer... restTemplateCustomizers) {
        Assert.notNull(restTemplateCustomizers, "RestTemplateCustomizers must not be null");
        return additionalCustomizers(Arrays.asList(restTemplateCustomizers));
    }

    public RestTemplateBuilder additionalCustomizers(Collection<? extends RestTemplateCustomizer> customizers) {
        Assert.notNull(customizers, "RestTemplateCustomizers must not be null");
        return new RestTemplateBuilder(this.detectRequestFactory, this.rootUri, this.messageConverters, this.requestFactorySupplier, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, append(this.restTemplateCustomizers, customizers), this.requestFactoryCustomizer, this.interceptors);
    }

    public RestTemplateBuilder setConnectTimeout(Duration connectTimeout) {
        return new RestTemplateBuilder(this.detectRequestFactory, this.rootUri, this.messageConverters, this.requestFactorySupplier, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.restTemplateCustomizers, this.requestFactoryCustomizer.connectTimeout(connectTimeout), this.interceptors);
    }

    @Deprecated
    public RestTemplateBuilder setConnectTimeout(int connectTimeout) {
        return setConnectTimeout(Duration.ofMillis(connectTimeout));
    }

    public RestTemplateBuilder setReadTimeout(Duration readTimeout) {
        return new RestTemplateBuilder(this.detectRequestFactory, this.rootUri, this.messageConverters, this.requestFactorySupplier, this.uriTemplateHandler, this.errorHandler, this.basicAuthentication, this.restTemplateCustomizers, this.requestFactoryCustomizer.readTimeout(readTimeout), this.interceptors);
    }

    @Deprecated
    public RestTemplateBuilder setReadTimeout(int readTimeout) {
        return setReadTimeout(Duration.ofMillis(readTimeout));
    }

    public RestTemplate build() {
        return build(RestTemplate.class);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends RestTemplate> T build(Class<T> restTemplateClass) {
        return (T) configure((RestTemplate) BeanUtils.instantiateClass(restTemplateClass));
    }

    public <T extends RestTemplate> T configure(T restTemplate) {
        configureRequestFactory(restTemplate);
        if (!CollectionUtils.isEmpty(this.messageConverters)) {
            restTemplate.setMessageConverters(new ArrayList(this.messageConverters));
        }
        if (this.uriTemplateHandler != null) {
            restTemplate.setUriTemplateHandler(this.uriTemplateHandler);
        }
        if (this.errorHandler != null) {
            restTemplate.setErrorHandler(this.errorHandler);
        }
        if (this.rootUri != null) {
            RootUriTemplateHandler.addTo(restTemplate, this.rootUri);
        }
        if (this.basicAuthentication != null) {
            restTemplate.getInterceptors().add(this.basicAuthentication);
        }
        restTemplate.getInterceptors().addAll(this.interceptors);
        if (!CollectionUtils.isEmpty(this.restTemplateCustomizers)) {
            for (RestTemplateCustomizer customizer : this.restTemplateCustomizers) {
                customizer.customize(restTemplate);
            }
        }
        return restTemplate;
    }

    private void configureRequestFactory(RestTemplate restTemplate) {
        ClientHttpRequestFactory requestFactory = null;
        if (this.requestFactorySupplier != null) {
            requestFactory = this.requestFactorySupplier.get();
        } else if (this.detectRequestFactory) {
            requestFactory = new ClientHttpRequestFactorySupplier().get();
        }
        if (requestFactory != null) {
            if (this.requestFactoryCustomizer != null) {
                this.requestFactoryCustomizer.accept(requestFactory);
            }
            restTemplate.setRequestFactory(requestFactory);
        }
    }

    private <T> Set<T> append(Set<T> set, Collection<? extends T> additions) {
        Set<T> result = new LinkedHashSet<>(set != null ? set : Collections.emptySet());
        result.addAll(additions);
        return Collections.unmodifiableSet(result);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/client/RestTemplateBuilder$RequestFactoryCustomizer.class */
    public static class RequestFactoryCustomizer implements Consumer<ClientHttpRequestFactory> {
        private final Duration connectTimeout;
        private final Duration readTimeout;

        RequestFactoryCustomizer() {
            this(null, null);
        }

        private RequestFactoryCustomizer(Duration connectTimeout, Duration readTimeout) {
            this.connectTimeout = connectTimeout;
            this.readTimeout = readTimeout;
        }

        public RequestFactoryCustomizer connectTimeout(Duration connectTimeout) {
            return new RequestFactoryCustomizer(connectTimeout, this.readTimeout);
        }

        public RequestFactoryCustomizer readTimeout(Duration readTimeout) {
            return new RequestFactoryCustomizer(this.connectTimeout, readTimeout);
        }

        @Override // java.util.function.Consumer
        public void accept(ClientHttpRequestFactory requestFactory) {
            ClientHttpRequestFactory unwrappedRequestFactory = unwrapRequestFactoryIfNecessary(requestFactory);
            if (this.connectTimeout != null) {
                new TimeoutRequestFactoryCustomizer(this.connectTimeout, "setConnectTimeout").customize(unwrappedRequestFactory);
            }
            if (this.readTimeout != null) {
                new TimeoutRequestFactoryCustomizer(this.readTimeout, "setReadTimeout").customize(unwrappedRequestFactory);
            }
        }

        private ClientHttpRequestFactory unwrapRequestFactoryIfNecessary(ClientHttpRequestFactory requestFactory) {
            if (!(requestFactory instanceof AbstractClientHttpRequestFactoryWrapper)) {
                return requestFactory;
            }
            ClientHttpRequestFactory unwrappedRequestFactory = requestFactory;
            Field field = ReflectionUtils.findField(AbstractClientHttpRequestFactoryWrapper.class, "requestFactory");
            ReflectionUtils.makeAccessible(field);
            do {
                unwrappedRequestFactory = (ClientHttpRequestFactory) ReflectionUtils.getField(field, unwrappedRequestFactory);
            } while (unwrappedRequestFactory instanceof AbstractClientHttpRequestFactoryWrapper);
            return unwrappedRequestFactory;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/client/RestTemplateBuilder$RequestFactoryCustomizer$TimeoutRequestFactoryCustomizer.class */
        public static final class TimeoutRequestFactoryCustomizer {
            private final Duration timeout;
            private final String methodName;

            TimeoutRequestFactoryCustomizer(Duration timeout, String methodName) {
                this.timeout = timeout;
                this.methodName = methodName;
            }

            void customize(ClientHttpRequestFactory factory) {
                ReflectionUtils.invokeMethod(findMethod(factory), factory, Integer.valueOf(Math.toIntExact(this.timeout.toMillis())));
            }

            private Method findMethod(ClientHttpRequestFactory factory) {
                Method method = ReflectionUtils.findMethod(factory.getClass(), this.methodName, Integer.TYPE);
                if (method != null) {
                    return method;
                }
                throw new IllegalStateException("Request factory " + factory.getClass() + " does not have a " + this.methodName + "(int) method");
            }
        }
    }
}