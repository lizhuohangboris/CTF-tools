package org.springframework.web.context.request;

import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/WebRequestInterceptor.class */
public interface WebRequestInterceptor {
    void preHandle(WebRequest webRequest) throws Exception;

    void postHandle(WebRequest webRequest, @Nullable ModelMap modelMap) throws Exception;

    void afterCompletion(WebRequest webRequest, @Nullable Exception exc) throws Exception;
}