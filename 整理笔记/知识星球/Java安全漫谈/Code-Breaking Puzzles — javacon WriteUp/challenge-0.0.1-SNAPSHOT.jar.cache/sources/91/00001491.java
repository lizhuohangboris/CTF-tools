package org.springframework.beans.factory.support;

import java.lang.reflect.Method;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/MethodReplacer.class */
public interface MethodReplacer {
    Object reimplement(Object obj, Method method, Object[] objArr) throws Throwable;
}