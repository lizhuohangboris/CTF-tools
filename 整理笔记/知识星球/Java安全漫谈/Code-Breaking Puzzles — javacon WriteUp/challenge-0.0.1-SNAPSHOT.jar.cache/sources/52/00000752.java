package org.aopalliance.intercept;

import java.lang.reflect.Method;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/aopalliance/intercept/MethodInvocation.class */
public interface MethodInvocation extends Invocation {
    Method getMethod();
}