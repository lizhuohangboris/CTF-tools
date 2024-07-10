package org.springframework.http.client.support;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.AsyncClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingAsyncClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/support/InterceptingAsyncHttpAccessor.class */
public abstract class InterceptingAsyncHttpAccessor extends AsyncHttpAccessor {
    private List<AsyncClientHttpRequestInterceptor> interceptors = new ArrayList();

    public void setInterceptors(List<AsyncClientHttpRequestInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public List<AsyncClientHttpRequestInterceptor> getInterceptors() {
        return this.interceptors;
    }

    @Override // org.springframework.http.client.support.AsyncHttpAccessor
    public AsyncClientHttpRequestFactory getAsyncRequestFactory() {
        AsyncClientHttpRequestFactory delegate = super.getAsyncRequestFactory();
        if (!CollectionUtils.isEmpty(getInterceptors())) {
            return new InterceptingAsyncClientHttpRequestFactory(delegate, getInterceptors());
        }
        return delegate;
    }
}