package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.servlet.ServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ServletResponseMethodArgumentResolver.class */
public class ServletResponseMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        return ServletResponse.class.isAssignableFrom(paramType) || OutputStream.class.isAssignableFrom(paramType) || Writer.class.isAssignableFrom(paramType);
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        if (mavContainer != null) {
            mavContainer.setRequestHandled(true);
        }
        Class<?> paramType = parameter.getParameterType();
        if (ServletResponse.class.isAssignableFrom(paramType)) {
            return resolveNativeResponse(webRequest, paramType);
        }
        return resolveArgument(paramType, (ServletResponse) resolveNativeResponse(webRequest, ServletResponse.class));
    }

    private <T> T resolveNativeResponse(NativeWebRequest webRequest, Class<T> requiredType) {
        T nativeResponse = (T) webRequest.getNativeResponse(requiredType);
        if (nativeResponse == null) {
            throw new IllegalStateException("Current response is not of type [" + requiredType.getName() + "]: " + webRequest);
        }
        return nativeResponse;
    }

    private Object resolveArgument(Class<?> paramType, ServletResponse response) throws IOException {
        if (OutputStream.class.isAssignableFrom(paramType)) {
            return response.getOutputStream();
        }
        if (Writer.class.isAssignableFrom(paramType)) {
            return response.getWriter();
        }
        throw new UnsupportedOperationException("Unknown parameter type: " + paramType);
    }
}