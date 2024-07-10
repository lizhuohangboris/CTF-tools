package org.aopalliance.intercept;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/aopalliance/intercept/MethodInterceptor.class */
public interface MethodInterceptor extends Interceptor {
    Object invoke(MethodInvocation methodInvocation) throws Throwable;
}