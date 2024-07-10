package org.springframework.http.client;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/InterceptingAsyncClientHttpRequestFactory.class */
public class InterceptingAsyncClientHttpRequestFactory implements AsyncClientHttpRequestFactory {
    private AsyncClientHttpRequestFactory delegate;
    private List<AsyncClientHttpRequestInterceptor> interceptors;

    public InterceptingAsyncClientHttpRequestFactory(AsyncClientHttpRequestFactory delegate, @Nullable List<AsyncClientHttpRequestInterceptor> interceptors) {
        this.delegate = delegate;
        this.interceptors = interceptors != null ? interceptors : Collections.emptyList();
    }

    @Override // org.springframework.http.client.AsyncClientHttpRequestFactory
    public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod method) {
        return new InterceptingAsyncClientHttpRequest(this.delegate, this.interceptors, uri, method);
    }
}