package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ResponseBodyAdvice.class */
public interface ResponseBodyAdvice<T> {
    boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> cls);

    @Nullable
    T beforeBodyWrite(@Nullable T t, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> cls, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse);
}