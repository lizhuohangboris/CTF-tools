package org.springframework.web.context.request;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/AsyncWebRequestInterceptor.class */
public interface AsyncWebRequestInterceptor extends WebRequestInterceptor {
    void afterConcurrentHandlingStarted(WebRequest webRequest);
}