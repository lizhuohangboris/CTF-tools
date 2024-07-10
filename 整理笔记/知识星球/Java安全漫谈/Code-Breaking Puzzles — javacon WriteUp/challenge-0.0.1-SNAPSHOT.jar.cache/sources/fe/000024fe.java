package org.springframework.web.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/HandlerMethod.class */
public class HandlerMethod {
    protected final Log logger = LogFactory.getLog(getClass());
    private final Object bean;
    @Nullable
    private final BeanFactory beanFactory;
    private final Class<?> beanType;
    private final Method method;
    private final Method bridgedMethod;
    private final MethodParameter[] parameters;
    @Nullable
    private HttpStatus responseStatus;
    @Nullable
    private String responseStatusReason;
    @Nullable
    private HandlerMethod resolvedFromHandlerMethod;
    @Nullable
    private volatile List<Annotation[][]> interfaceParameterAnnotations;

    public HandlerMethod(Object bean, Method method) {
        Assert.notNull(bean, "Bean is required");
        Assert.notNull(method, "Method is required");
        this.bean = bean;
        this.beanFactory = null;
        this.beanType = ClassUtils.getUserClass(bean);
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        this.parameters = initMethodParameters();
        evaluateResponseStatus();
    }

    public HandlerMethod(Object bean, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Assert.notNull(bean, "Bean is required");
        Assert.notNull(methodName, "Method name is required");
        this.bean = bean;
        this.beanFactory = null;
        this.beanType = ClassUtils.getUserClass(bean);
        this.method = bean.getClass().getMethod(methodName, parameterTypes);
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(this.method);
        this.parameters = initMethodParameters();
        evaluateResponseStatus();
    }

    public HandlerMethod(String beanName, BeanFactory beanFactory, Method method) {
        Assert.hasText(beanName, "Bean name is required");
        Assert.notNull(beanFactory, "BeanFactory is required");
        Assert.notNull(method, "Method is required");
        this.bean = beanName;
        this.beanFactory = beanFactory;
        Class<?> beanType = beanFactory.getType(beanName);
        if (beanType == null) {
            throw new IllegalStateException("Cannot resolve bean type for bean with name '" + beanName + "'");
        }
        this.beanType = ClassUtils.getUserClass(beanType);
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        this.parameters = initMethodParameters();
        evaluateResponseStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HandlerMethod(HandlerMethod handlerMethod) {
        Assert.notNull(handlerMethod, "HandlerMethod is required");
        this.bean = handlerMethod.bean;
        this.beanFactory = handlerMethod.beanFactory;
        this.beanType = handlerMethod.beanType;
        this.method = handlerMethod.method;
        this.bridgedMethod = handlerMethod.bridgedMethod;
        this.parameters = handlerMethod.parameters;
        this.responseStatus = handlerMethod.responseStatus;
        this.responseStatusReason = handlerMethod.responseStatusReason;
        this.resolvedFromHandlerMethod = handlerMethod.resolvedFromHandlerMethod;
    }

    private HandlerMethod(HandlerMethod handlerMethod, Object handler) {
        Assert.notNull(handlerMethod, "HandlerMethod is required");
        Assert.notNull(handler, "Handler object is required");
        this.bean = handler;
        this.beanFactory = handlerMethod.beanFactory;
        this.beanType = handlerMethod.beanType;
        this.method = handlerMethod.method;
        this.bridgedMethod = handlerMethod.bridgedMethod;
        this.parameters = handlerMethod.parameters;
        this.responseStatus = handlerMethod.responseStatus;
        this.responseStatusReason = handlerMethod.responseStatusReason;
        this.resolvedFromHandlerMethod = handlerMethod;
    }

    private MethodParameter[] initMethodParameters() {
        int count = this.bridgedMethod.getParameterCount();
        MethodParameter[] result = new MethodParameter[count];
        for (int i = 0; i < count; i++) {
            HandlerMethodParameter parameter = new HandlerMethodParameter(i);
            GenericTypeResolver.resolveParameterType(parameter, this.beanType);
            result[i] = parameter;
        }
        return result;
    }

    private void evaluateResponseStatus() {
        ResponseStatus annotation = (ResponseStatus) getMethodAnnotation(ResponseStatus.class);
        if (annotation == null) {
            annotation = (ResponseStatus) AnnotatedElementUtils.findMergedAnnotation(getBeanType(), ResponseStatus.class);
        }
        if (annotation != null) {
            this.responseStatus = annotation.code();
            this.responseStatusReason = annotation.reason();
        }
    }

    public Object getBean() {
        return this.bean;
    }

    public Method getMethod() {
        return this.method;
    }

    public Class<?> getBeanType() {
        return this.beanType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Method getBridgedMethod() {
        return this.bridgedMethod;
    }

    public MethodParameter[] getMethodParameters() {
        return this.parameters;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public HttpStatus getResponseStatus() {
        return this.responseStatus;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public String getResponseStatusReason() {
        return this.responseStatusReason;
    }

    public MethodParameter getReturnType() {
        return new HandlerMethodParameter(-1);
    }

    public MethodParameter getReturnValueType(@Nullable Object returnValue) {
        return new ReturnValueMethodParameter(returnValue);
    }

    public boolean isVoid() {
        return Void.TYPE.equals(getReturnType().getParameterType());
    }

    @Nullable
    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
        return (A) AnnotatedElementUtils.findMergedAnnotation(this.method, annotationType);
    }

    public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
        return AnnotatedElementUtils.hasAnnotation(this.method, annotationType);
    }

    @Nullable
    public HandlerMethod getResolvedFromHandlerMethod() {
        return this.resolvedFromHandlerMethod;
    }

    public HandlerMethod createWithResolvedBean() {
        Object handler = this.bean;
        if (this.bean instanceof String) {
            Assert.state(this.beanFactory != null, "Cannot resolve bean name without BeanFactory");
            String beanName = (String) this.bean;
            handler = this.beanFactory.getBean(beanName);
        }
        return new HandlerMethod(this, handler);
    }

    public String getShortLogMessage() {
        return getBeanType().getName() + "#" + this.method.getName() + PropertyAccessor.PROPERTY_KEY_PREFIX + this.method.getParameterCount() + " args]";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<Annotation[][]> getInterfaceParameterAnnotations() {
        Class<?>[] interfaces;
        Method[] methods;
        List<Annotation[][]> parameterAnnotations = this.interfaceParameterAnnotations;
        if (parameterAnnotations == null) {
            parameterAnnotations = new ArrayList<>();
            for (Class<?> ifc : this.method.getDeclaringClass().getInterfaces()) {
                for (Method candidate : ifc.getMethods()) {
                    if (isOverrideFor(candidate)) {
                        parameterAnnotations.add(candidate.getParameterAnnotations());
                    }
                }
            }
            this.interfaceParameterAnnotations = parameterAnnotations;
        }
        return parameterAnnotations;
    }

    private boolean isOverrideFor(Method candidate) {
        if (!candidate.getName().equals(this.method.getName()) || candidate.getParameterCount() != this.method.getParameterCount()) {
            return false;
        }
        Class<?>[] paramTypes = this.method.getParameterTypes();
        if (Arrays.equals(candidate.getParameterTypes(), paramTypes)) {
            return true;
        }
        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i] != ResolvableType.forMethodParameter(candidate, i, this.method.getDeclaringClass()).resolve()) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HandlerMethod)) {
            return false;
        }
        HandlerMethod otherMethod = (HandlerMethod) other;
        return this.bean.equals(otherMethod.bean) && this.method.equals(otherMethod.method);
    }

    public int hashCode() {
        return (this.bean.hashCode() * 31) + this.method.hashCode();
    }

    public String toString() {
        return this.method.toGenericString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/HandlerMethod$HandlerMethodParameter.class */
    public class HandlerMethodParameter extends SynthesizingMethodParameter {
        @Nullable
        private volatile Annotation[] combinedAnnotations;

        public HandlerMethodParameter(int index) {
            super(HandlerMethod.this.bridgedMethod, index);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public HandlerMethodParameter(HandlerMethodParameter original) {
            super(original);
        }

        @Override // org.springframework.core.MethodParameter
        public Class<?> getContainingClass() {
            return HandlerMethod.this.getBeanType();
        }

        @Override // org.springframework.core.MethodParameter
        public <T extends Annotation> T getMethodAnnotation(Class<T> annotationType) {
            return (T) HandlerMethod.this.getMethodAnnotation(annotationType);
        }

        @Override // org.springframework.core.MethodParameter
        public <T extends Annotation> boolean hasMethodAnnotation(Class<T> annotationType) {
            return HandlerMethod.this.hasMethodAnnotation(annotationType);
        }

        @Override // org.springframework.core.MethodParameter
        public Annotation[] getParameterAnnotations() {
            Annotation[] anns = this.combinedAnnotations;
            if (anns == null) {
                anns = super.getParameterAnnotations();
                for (Annotation[][] ifcAnns : HandlerMethod.this.getInterfaceParameterAnnotations()) {
                    Annotation[] paramAnns = ifcAnns[getParameterIndex()];
                    if (paramAnns.length > 0) {
                        List<Annotation> merged = new ArrayList<>(anns.length + paramAnns.length);
                        merged.addAll(Arrays.asList(anns));
                        for (Annotation paramAnn : paramAnns) {
                            boolean existingType = false;
                            Annotation[] annotationArr = anns;
                            int length = annotationArr.length;
                            int i = 0;
                            while (true) {
                                if (i >= length) {
                                    break;
                                }
                                Annotation ann = annotationArr[i];
                                if (ann.annotationType() != paramAnn.annotationType()) {
                                    i++;
                                } else {
                                    existingType = true;
                                    break;
                                }
                            }
                            if (!existingType) {
                                merged.add(paramAnn);
                            }
                        }
                        anns = (Annotation[]) merged.toArray(new Annotation[0]);
                    }
                }
                this.combinedAnnotations = anns;
            }
            return anns;
        }

        @Override // org.springframework.core.annotation.SynthesizingMethodParameter, org.springframework.core.MethodParameter
        /* renamed from: clone */
        public HandlerMethodParameter mo1575clone() {
            return new HandlerMethodParameter(this);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/HandlerMethod$ReturnValueMethodParameter.class */
    private class ReturnValueMethodParameter extends HandlerMethodParameter {
        @Nullable
        private final Object returnValue;

        public ReturnValueMethodParameter(@Nullable Object returnValue) {
            super(-1);
            this.returnValue = returnValue;
        }

        protected ReturnValueMethodParameter(ReturnValueMethodParameter original) {
            super(original);
            this.returnValue = original.returnValue;
        }

        @Override // org.springframework.core.MethodParameter
        public Class<?> getParameterType() {
            return this.returnValue != null ? this.returnValue.getClass() : super.getParameterType();
        }

        @Override // org.springframework.web.method.HandlerMethod.HandlerMethodParameter, org.springframework.core.annotation.SynthesizingMethodParameter, org.springframework.core.MethodParameter
        /* renamed from: clone */
        public ReturnValueMethodParameter mo1575clone() {
            return new ReturnValueMethodParameter(this);
        }
    }
}