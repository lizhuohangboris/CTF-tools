package org.springframework.cache.annotation;

import java.lang.reflect.Method;
import java.util.Collection;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/annotation/CacheAnnotationParser.class */
public interface CacheAnnotationParser {
    @Nullable
    Collection<CacheOperation> parseCacheAnnotations(Class<?> cls);

    @Nullable
    Collection<CacheOperation> parseCacheAnnotations(Method method);
}