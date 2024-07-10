package org.springframework.web.servlet.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.Assert;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/resource/ResourceUrlProviderExposingInterceptor.class */
public class ResourceUrlProviderExposingInterceptor extends HandlerInterceptorAdapter {
    public static final String RESOURCE_URL_PROVIDER_ATTR = ResourceUrlProvider.class.getName();
    private final ResourceUrlProvider resourceUrlProvider;

    public ResourceUrlProviderExposingInterceptor(ResourceUrlProvider resourceUrlProvider) {
        Assert.notNull(resourceUrlProvider, "ResourceUrlProvider is required");
        this.resourceUrlProvider = resourceUrlProvider;
    }

    @Override // org.springframework.web.servlet.handler.HandlerInterceptorAdapter, org.springframework.web.servlet.HandlerInterceptor
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(RESOURCE_URL_PROVIDER_ATTR, this.resourceUrlProvider);
        return true;
    }
}