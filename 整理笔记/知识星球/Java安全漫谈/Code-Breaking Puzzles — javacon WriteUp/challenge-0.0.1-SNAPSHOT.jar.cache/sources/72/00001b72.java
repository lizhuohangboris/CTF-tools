package org.springframework.cache.interceptor;

import java.util.Collection;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/SimpleCacheResolver.class */
public class SimpleCacheResolver extends AbstractCacheResolver {
    public SimpleCacheResolver() {
    }

    public SimpleCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.springframework.cache.interceptor.BasicOperation] */
    @Override // org.springframework.cache.interceptor.AbstractCacheResolver
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        return context.getOperation().getCacheNames();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static SimpleCacheResolver of(@Nullable CacheManager cacheManager) {
        if (cacheManager != null) {
            return new SimpleCacheResolver(cacheManager);
        }
        return null;
    }
}