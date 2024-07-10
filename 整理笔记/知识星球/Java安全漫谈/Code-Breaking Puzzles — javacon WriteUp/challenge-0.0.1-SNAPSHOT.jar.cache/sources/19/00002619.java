package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/AbstractMappingJacksonResponseBodyAdvice.class */
public abstract class AbstractMappingJacksonResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    protected abstract void beforeBodyWriteInternal(MappingJacksonValue mappingJacksonValue, MediaType mediaType, MethodParameter methodParameter, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse);

    @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
    @Nullable
    public final Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType contentType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return null;
        }
        MappingJacksonValue container = getOrCreateContainer(body);
        beforeBodyWriteInternal(container, contentType, returnType, request, response);
        return container;
    }

    protected MappingJacksonValue getOrCreateContainer(Object body) {
        return body instanceof MappingJacksonValue ? (MappingJacksonValue) body : new MappingJacksonValue(body);
    }
}