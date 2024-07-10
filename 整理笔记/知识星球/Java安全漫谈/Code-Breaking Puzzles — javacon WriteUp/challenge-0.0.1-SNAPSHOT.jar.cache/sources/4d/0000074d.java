package org.aopalliance.intercept;

import java.lang.reflect.Constructor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/aopalliance/intercept/ConstructorInvocation.class */
public interface ConstructorInvocation extends Invocation {
    Constructor<?> getConstructor();
}