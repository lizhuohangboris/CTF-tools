package org.springframework.cache.interceptor;

import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;
import org.springframework.util.function.SingletonSupplier;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/AbstractCacheInvoker.class */
public abstract class AbstractCacheInvoker {
    protected SingletonSupplier<CacheErrorHandler> errorHandler;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractCacheInvoker() {
        this.errorHandler = SingletonSupplier.of(SimpleCacheErrorHandler::new);
    }

    protected AbstractCacheInvoker(CacheErrorHandler errorHandler) {
        this.errorHandler = SingletonSupplier.of(errorHandler);
    }

    public void setErrorHandler(CacheErrorHandler errorHandler) {
        this.errorHandler = SingletonSupplier.of(errorHandler);
    }

    public CacheErrorHandler getErrorHandler() {
        return this.errorHandler.obtain();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Cache.ValueWrapper doGet(Cache cache, Object key) {
        try {
            return cache.get(key);
        } catch (RuntimeException ex) {
            getErrorHandler().handleCacheGetError(ex, cache, key);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doPut(Cache cache, Object key, @Nullable Object result) {
        try {
            cache.put(key, result);
        } catch (RuntimeException ex) {
            getErrorHandler().handleCachePutError(ex, cache, key, result);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doEvict(Cache cache, Object key) {
        try {
            cache.evict(key);
        } catch (RuntimeException ex) {
            getErrorHandler().handleCacheEvictError(ex, cache, key);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doClear(Cache cache) {
        try {
            cache.clear();
        } catch (RuntimeException ex) {
            getErrorHandler().handleCacheClearError(ex, cache);
        }
    }
}