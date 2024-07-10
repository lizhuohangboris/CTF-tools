package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.annotation.ReactiveTypeHandler;
import org.springframework.web.util.NestedServletException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ServletInvocableHandlerMethod.class */
public class ServletInvocableHandlerMethod extends InvocableHandlerMethod {
    private static final Method CALLABLE_METHOD = ClassUtils.getMethod(Callable.class, "call", new Class[0]);
    @Nullable
    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

    public ServletInvocableHandlerMethod(Object handler, Method method) {
        super(handler, method);
    }

    public ServletInvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }

    public void setHandlerMethodReturnValueHandlers(HandlerMethodReturnValueHandlerComposite returnValueHandlers) {
        this.returnValueHandlers = returnValueHandlers;
    }

    public void invokeAndHandle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
        Object returnValue = invokeForRequest(webRequest, mavContainer, providedArgs);
        setResponseStatus(webRequest);
        if (returnValue == null) {
            if (isRequestNotModified(webRequest) || getResponseStatus() != null || mavContainer.isRequestHandled()) {
                mavContainer.setRequestHandled(true);
                return;
            }
        } else if (StringUtils.hasText(getResponseStatusReason())) {
            mavContainer.setRequestHandled(true);
            return;
        }
        mavContainer.setRequestHandled(false);
        Assert.state(this.returnValueHandlers != null, "No return value handlers");
        try {
            this.returnValueHandlers.handleReturnValue(returnValue, getReturnValueType(returnValue), mavContainer, webRequest);
        } catch (Exception ex) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace(formatErrorForReturnValue(returnValue), ex);
            }
            throw ex;
        }
    }

    private void setResponseStatus(ServletWebRequest webRequest) throws IOException {
        HttpStatus status = getResponseStatus();
        if (status == null) {
            return;
        }
        HttpServletResponse response = webRequest.getResponse();
        if (response != null) {
            String reason = getResponseStatusReason();
            if (StringUtils.hasText(reason)) {
                response.sendError(status.value(), reason);
            } else {
                response.setStatus(status.value());
            }
        }
        webRequest.getRequest().setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, status);
    }

    private boolean isRequestNotModified(ServletWebRequest webRequest) {
        return webRequest.isNotModified();
    }

    private String formatErrorForReturnValue(@Nullable Object returnValue) {
        return "Error handling return value=[" + returnValue + "]" + (returnValue != null ? ", type=" + returnValue.getClass().getName() : "") + " in " + toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ServletInvocableHandlerMethod wrapConcurrentResult(Object result) {
        return new ConcurrentResultHandlerMethod(result, new ConcurrentResultMethodParameter(result));
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ServletInvocableHandlerMethod$ConcurrentResultHandlerMethod.class */
    private class ConcurrentResultHandlerMethod extends ServletInvocableHandlerMethod {
        private final MethodParameter returnType;

        public ConcurrentResultHandlerMethod(Object result, ConcurrentResultMethodParameter returnType) {
            super(() -> {
                if (result instanceof Exception) {
                    throw ((Exception) result);
                }
                if (result instanceof Throwable) {
                    throw new NestedServletException("Async processing failed", (Throwable) result);
                }
                return result;
            }, ServletInvocableHandlerMethod.CALLABLE_METHOD);
            if (ServletInvocableHandlerMethod.this.returnValueHandlers != null) {
                setHandlerMethodReturnValueHandlers(ServletInvocableHandlerMethod.this.returnValueHandlers);
            }
            this.returnType = returnType;
        }

        @Override // org.springframework.web.method.HandlerMethod
        public Class<?> getBeanType() {
            return ServletInvocableHandlerMethod.this.getBeanType();
        }

        @Override // org.springframework.web.method.HandlerMethod
        public MethodParameter getReturnValueType(@Nullable Object returnValue) {
            return this.returnType;
        }

        @Override // org.springframework.web.method.HandlerMethod
        public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
            return (A) ServletInvocableHandlerMethod.this.getMethodAnnotation(annotationType);
        }

        @Override // org.springframework.web.method.HandlerMethod
        public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
            return ServletInvocableHandlerMethod.this.hasMethodAnnotation(annotationType);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ServletInvocableHandlerMethod$ConcurrentResultMethodParameter.class */
    private class ConcurrentResultMethodParameter extends HandlerMethod.HandlerMethodParameter {
        @Nullable
        private final Object returnValue;
        private final ResolvableType returnType;

        public ConcurrentResultMethodParameter(Object returnValue) {
            super(-1);
            ResolvableType generic;
            this.returnValue = returnValue;
            if (returnValue instanceof ReactiveTypeHandler.CollectedValuesList) {
                generic = ((ReactiveTypeHandler.CollectedValuesList) returnValue).getReturnType();
            } else {
                generic = ResolvableType.forType(super.getGenericParameterType()).getGeneric(new int[0]);
            }
            this.returnType = generic;
        }

        public ConcurrentResultMethodParameter(ConcurrentResultMethodParameter original) {
            super(original);
            this.returnValue = original.returnValue;
            this.returnType = original.returnType;
        }

        @Override // org.springframework.core.MethodParameter
        public Class<?> getParameterType() {
            if (this.returnValue != null) {
                return this.returnValue.getClass();
            }
            if (!ResolvableType.NONE.equals(this.returnType)) {
                return this.returnType.toClass();
            }
            return super.getParameterType();
        }

        @Override // org.springframework.core.MethodParameter
        public Type getGenericParameterType() {
            return this.returnType.getType();
        }

        @Override // org.springframework.web.method.HandlerMethod.HandlerMethodParameter, org.springframework.core.MethodParameter
        public <T extends Annotation> boolean hasMethodAnnotation(Class<T> annotationType) {
            return super.hasMethodAnnotation(annotationType) || (annotationType == ResponseBody.class && (this.returnValue instanceof ReactiveTypeHandler.CollectedValuesList));
        }

        @Override // org.springframework.web.method.HandlerMethod.HandlerMethodParameter, org.springframework.core.annotation.SynthesizingMethodParameter, org.springframework.core.MethodParameter
        /* renamed from: clone */
        public ConcurrentResultMethodParameter mo1575clone() {
            return new ConcurrentResultMethodParameter(this);
        }
    }
}