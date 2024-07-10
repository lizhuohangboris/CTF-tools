package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.Principal;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.PushBuilder;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.support.RequestContextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ServletRequestMethodArgumentResolver.class */
public class ServletRequestMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Nullable
    private static Class<?> pushBuilder;

    static {
        try {
            pushBuilder = ClassUtils.forName("javax.servlet.http.PushBuilder", ServletRequestMethodArgumentResolver.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            pushBuilder = null;
        }
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        return WebRequest.class.isAssignableFrom(paramType) || ServletRequest.class.isAssignableFrom(paramType) || MultipartRequest.class.isAssignableFrom(paramType) || HttpSession.class.isAssignableFrom(paramType) || (pushBuilder != null && pushBuilder.isAssignableFrom(paramType)) || Principal.class.isAssignableFrom(paramType) || InputStream.class.isAssignableFrom(paramType) || Reader.class.isAssignableFrom(paramType) || HttpMethod.class == paramType || Locale.class == paramType || TimeZone.class == paramType || ZoneId.class == paramType;
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Class<?> paramType = parameter.getParameterType();
        if (WebRequest.class.isAssignableFrom(paramType)) {
            if (!paramType.isInstance(webRequest)) {
                throw new IllegalStateException("Current request is not of type [" + paramType.getName() + "]: " + webRequest);
            }
            return webRequest;
        } else if (ServletRequest.class.isAssignableFrom(paramType) || MultipartRequest.class.isAssignableFrom(paramType)) {
            return resolveNativeRequest(webRequest, paramType);
        } else {
            return resolveArgument(paramType, (HttpServletRequest) resolveNativeRequest(webRequest, HttpServletRequest.class));
        }
    }

    private <T> T resolveNativeRequest(NativeWebRequest webRequest, Class<T> requiredType) {
        T nativeRequest = (T) webRequest.getNativeRequest(requiredType);
        if (nativeRequest == null) {
            throw new IllegalStateException("Current request is not of type [" + requiredType.getName() + "]: " + webRequest);
        }
        return nativeRequest;
    }

    @Nullable
    private Object resolveArgument(Class<?> paramType, HttpServletRequest request) throws IOException {
        if (HttpSession.class.isAssignableFrom(paramType)) {
            HttpSession session = request.getSession();
            if (session != null && !paramType.isInstance(session)) {
                throw new IllegalStateException("Current session is not of type [" + paramType.getName() + "]: " + session);
            }
            return session;
        } else if (pushBuilder != null && pushBuilder.isAssignableFrom(paramType)) {
            return PushBuilderDelegate.resolvePushBuilder(request, paramType);
        } else {
            if (InputStream.class.isAssignableFrom(paramType)) {
                InputStream inputStream = request.getInputStream();
                if (inputStream != null && !paramType.isInstance(inputStream)) {
                    throw new IllegalStateException("Request input stream is not of type [" + paramType.getName() + "]: " + inputStream);
                }
                return inputStream;
            } else if (Reader.class.isAssignableFrom(paramType)) {
                Reader reader = request.getReader();
                if (reader != null && !paramType.isInstance(reader)) {
                    throw new IllegalStateException("Request body reader is not of type [" + paramType.getName() + "]: " + reader);
                }
                return reader;
            } else if (Principal.class.isAssignableFrom(paramType)) {
                Principal userPrincipal = request.getUserPrincipal();
                if (userPrincipal != null && !paramType.isInstance(userPrincipal)) {
                    throw new IllegalStateException("Current user principal is not of type [" + paramType.getName() + "]: " + userPrincipal);
                }
                return userPrincipal;
            } else if (HttpMethod.class == paramType) {
                return HttpMethod.resolve(request.getMethod());
            } else {
                if (Locale.class == paramType) {
                    return RequestContextUtils.getLocale(request);
                }
                if (TimeZone.class == paramType) {
                    TimeZone timeZone = RequestContextUtils.getTimeZone(request);
                    return timeZone != null ? timeZone : TimeZone.getDefault();
                } else if (ZoneId.class == paramType) {
                    TimeZone timeZone2 = RequestContextUtils.getTimeZone(request);
                    return timeZone2 != null ? timeZone2.toZoneId() : ZoneId.systemDefault();
                } else {
                    throw new UnsupportedOperationException("Unknown parameter type: " + paramType.getName());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ServletRequestMethodArgumentResolver$PushBuilderDelegate.class */
    public static class PushBuilderDelegate {
        private PushBuilderDelegate() {
        }

        @Nullable
        public static Object resolvePushBuilder(HttpServletRequest request, Class<?> paramType) {
            PushBuilder pushBuilder = request.newPushBuilder();
            if (pushBuilder != null && !paramType.isInstance(pushBuilder)) {
                throw new IllegalStateException("Current push builder is not of type [" + paramType.getName() + "]: " + pushBuilder);
            }
            return pushBuilder;
        }
    }
}