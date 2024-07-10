package org.aopalliance.intercept;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/aopalliance/intercept/ConstructorInterceptor.class */
public interface ConstructorInterceptor extends Interceptor {
    Object construct(ConstructorInvocation constructorInvocation) throws Throwable;
}