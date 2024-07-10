package org.springframework.aop.aspectj.annotation;

import java.lang.reflect.Method;
import java.util.List;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/annotation/AspectJAdvisorFactory.class */
public interface AspectJAdvisorFactory {
    boolean isAspect(Class<?> cls);

    void validate(Class<?> cls) throws AopConfigException;

    List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory metadataAwareAspectInstanceFactory);

    @Nullable
    Advisor getAdvisor(Method method, MetadataAwareAspectInstanceFactory metadataAwareAspectInstanceFactory, int i, String str);

    @Nullable
    Advice getAdvice(Method method, AspectJExpressionPointcut aspectJExpressionPointcut, MetadataAwareAspectInstanceFactory metadataAwareAspectInstanceFactory, int i, String str);
}