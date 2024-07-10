package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import org.springframework.cache.interceptor.BasicOperation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/CacheOperationInvocationContext.class */
public interface CacheOperationInvocationContext<O extends BasicOperation> {
    O getOperation();

    Object getTarget();

    Method getMethod();

    Object[] getArgs();
}