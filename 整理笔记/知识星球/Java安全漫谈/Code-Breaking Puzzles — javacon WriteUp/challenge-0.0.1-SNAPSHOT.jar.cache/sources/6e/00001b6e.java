package org.springframework.cache.interceptor;

import java.lang.reflect.Method;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/KeyGenerator.class */
public interface KeyGenerator {
    Object generate(Object obj, Method method, Object... objArr);
}