package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.util.Collection;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/CacheOperationSource.class */
public interface CacheOperationSource {
    @Nullable
    Collection<CacheOperation> getCacheOperations(Method method, @Nullable Class<?> cls);
}