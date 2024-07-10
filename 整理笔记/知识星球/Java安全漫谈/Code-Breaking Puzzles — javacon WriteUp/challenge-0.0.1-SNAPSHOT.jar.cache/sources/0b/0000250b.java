package org.springframework.web.method.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.ExceptionDepthComparator;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/annotation/ExceptionHandlerMethodResolver.class */
public class ExceptionHandlerMethodResolver {
    public static final ReflectionUtils.MethodFilter EXCEPTION_HANDLER_METHODS = method -> {
        return AnnotatedElementUtils.hasAnnotation(method, ExceptionHandler.class);
    };
    private final Map<Class<? extends Throwable>, Method> mappedMethods = new HashMap(16);
    private final Map<Class<? extends Throwable>, Method> exceptionLookupCache = new ConcurrentReferenceHashMap(16);

    public ExceptionHandlerMethodResolver(Class<?> handlerType) {
        for (Method method : MethodIntrospector.selectMethods(handlerType, EXCEPTION_HANDLER_METHODS)) {
            for (Class<? extends Throwable> exceptionType : detectExceptionMappings(method)) {
                addExceptionMapping(exceptionType, method);
            }
        }
    }

    private List<Class<? extends Throwable>> detectExceptionMappings(Method method) {
        Class<?>[] parameterTypes;
        List<Class<? extends Throwable>> result = new ArrayList<>();
        detectAnnotationExceptionMappings(method, result);
        if (result.isEmpty()) {
            for (Class<?> paramType : method.getParameterTypes()) {
                if (Throwable.class.isAssignableFrom(paramType)) {
                    result.add(paramType);
                }
            }
        }
        if (result.isEmpty()) {
            throw new IllegalStateException("No exception types mapped to " + method);
        }
        return result;
    }

    private void detectAnnotationExceptionMappings(Method method, List<Class<? extends Throwable>> result) {
        ExceptionHandler ann = (ExceptionHandler) AnnotatedElementUtils.findMergedAnnotation(method, ExceptionHandler.class);
        Assert.state(ann != null, "No ExceptionHandler annotation");
        result.addAll(Arrays.asList(ann.value()));
    }

    private void addExceptionMapping(Class<? extends Throwable> exceptionType, Method method) {
        Method oldMethod = this.mappedMethods.put(exceptionType, method);
        if (oldMethod != null && !oldMethod.equals(method)) {
            throw new IllegalStateException("Ambiguous @ExceptionHandler method mapped for [" + exceptionType + "]: {" + oldMethod + ", " + method + "}");
        }
    }

    public boolean hasExceptionMappings() {
        return !this.mappedMethods.isEmpty();
    }

    @Nullable
    public Method resolveMethod(Exception exception) {
        return resolveMethodByThrowable(exception);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    public Method resolveMethodByThrowable(Throwable exception) {
        Throwable cause;
        Method method = resolveMethodByExceptionType(exception.getClass());
        if (method == null && (cause = exception.getCause()) != null) {
            method = resolveMethodByExceptionType(cause.getClass());
        }
        return method;
    }

    @Nullable
    public Method resolveMethodByExceptionType(Class<? extends Throwable> exceptionType) {
        Method method = this.exceptionLookupCache.get(exceptionType);
        if (method == null) {
            method = getMappedMethod(exceptionType);
            this.exceptionLookupCache.put(exceptionType, method);
        }
        return method;
    }

    @Nullable
    private Method getMappedMethod(Class<? extends Throwable> exceptionType) {
        List<Class<? extends Throwable>> matches = new ArrayList<>();
        for (Class<? extends Throwable> mappedException : this.mappedMethods.keySet()) {
            if (mappedException.isAssignableFrom(exceptionType)) {
                matches.add(mappedException);
            }
        }
        if (!matches.isEmpty()) {
            matches.sort(new ExceptionDepthComparator(exceptionType));
            return this.mappedMethods.get(matches.get(0));
        }
        return null;
    }
}