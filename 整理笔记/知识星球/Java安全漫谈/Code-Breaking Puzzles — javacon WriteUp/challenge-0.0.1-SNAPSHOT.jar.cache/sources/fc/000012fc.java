package org.springframework.aop.interceptor;

import java.lang.reflect.Method;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/interceptor/AsyncUncaughtExceptionHandler.class */
public interface AsyncUncaughtExceptionHandler {
    void handleUncaughtException(Throwable th, Method method, Object... objArr);
}