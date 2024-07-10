package org.springframework.http.client;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/InterceptingClientHttpRequestFactory.class */
public class InterceptingClientHttpRequestFactory extends AbstractClientHttpRequestFactoryWrapper {
    private final List<ClientHttpRequestInterceptor> interceptors;

    public InterceptingClientHttpRequestFactory(ClientHttpRequestFactory requestFactory, @Nullable List<ClientHttpRequestInterceptor> interceptors) {
        super(requestFactory);
        this.interceptors = interceptors != null ? interceptors : Collections.emptyList();
    }

    @Override // org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper
    protected ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod, ClientHttpRequestFactory requestFactory) {
        return new InterceptingClientHttpRequest(requestFactory, this.interceptors, uri, httpMethod);
    }
}