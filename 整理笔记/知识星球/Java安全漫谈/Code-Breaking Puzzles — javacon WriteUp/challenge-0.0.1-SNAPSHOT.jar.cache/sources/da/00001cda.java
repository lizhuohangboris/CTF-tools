package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ContextAnnotationAutowireCandidateResolver.class */
public class ContextAnnotationAutowireCandidateResolver extends QualifierAnnotationAutowireCandidateResolver {
    @Override // org.springframework.beans.factory.support.SimpleAutowireCandidateResolver, org.springframework.beans.factory.support.AutowireCandidateResolver
    @Nullable
    public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, @Nullable String beanName) {
        if (isLazy(descriptor)) {
            return buildLazyResolutionProxy(descriptor, beanName);
        }
        return null;
    }

    protected boolean isLazy(DependencyDescriptor descriptor) {
        Annotation[] annotations;
        Lazy lazy;
        for (Annotation ann : descriptor.getAnnotations()) {
            Lazy lazy2 = (Lazy) AnnotationUtils.getAnnotation(ann, Lazy.class);
            if (lazy2 != null && lazy2.value()) {
                return true;
            }
        }
        MethodParameter methodParam = descriptor.getMethodParameter();
        if (methodParam != null) {
            Method method = methodParam.getMethod();
            if ((method == null || Void.TYPE == method.getReturnType()) && (lazy = (Lazy) AnnotationUtils.getAnnotation(methodParam.getAnnotatedElement(), Lazy.class)) != null && lazy.value()) {
                return true;
            }
            return false;
        }
        return false;
    }

    protected Object buildLazyResolutionProxy(final DependencyDescriptor descriptor, @Nullable final String beanName) {
        Assert.state(getBeanFactory() instanceof DefaultListableBeanFactory, "BeanFactory needs to be a DefaultListableBeanFactory");
        final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) getBeanFactory();
        TargetSource ts = new TargetSource() { // from class: org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver.1
            @Override // org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
            public Class<?> getTargetClass() {
                return descriptor.getDependencyType();
            }

            @Override // org.springframework.aop.TargetSource
            public boolean isStatic() {
                return false;
            }

            @Override // org.springframework.aop.TargetSource
            public Object getTarget() {
                Object target = beanFactory.doResolveDependency(descriptor, beanName, null, null);
                if (target == null) {
                    Class<?> type = getTargetClass();
                    if (Map.class == type) {
                        return Collections.emptyMap();
                    }
                    if (List.class == type) {
                        return Collections.emptyList();
                    }
                    if (Set.class == type || Collection.class == type) {
                        return Collections.emptySet();
                    }
                    throw new NoSuchBeanDefinitionException(descriptor.getResolvableType(), "Optional dependency not present for lazy injection point");
                }
                return target;
            }

            @Override // org.springframework.aop.TargetSource
            public void releaseTarget(Object target) {
            }
        };
        ProxyFactory pf = new ProxyFactory();
        pf.setTargetSource(ts);
        Class<?> dependencyType = descriptor.getDependencyType();
        if (dependencyType.isInterface()) {
            pf.addInterface(dependencyType);
        }
        return pf.getProxy(beanFactory.getBeanClassLoader());
    }
}