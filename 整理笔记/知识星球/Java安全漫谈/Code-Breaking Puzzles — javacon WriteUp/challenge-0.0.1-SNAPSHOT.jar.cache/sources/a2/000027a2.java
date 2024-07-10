package org.thymeleaf.cache;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/cache/NonCacheableCacheEntryValidity.class */
public class NonCacheableCacheEntryValidity implements ICacheEntryValidity {
    public static final NonCacheableCacheEntryValidity INSTANCE = new NonCacheableCacheEntryValidity();

    @Override // org.thymeleaf.cache.ICacheEntryValidity
    public boolean isCacheable() {
        return false;
    }

    @Override // org.thymeleaf.cache.ICacheEntryValidity
    public boolean isCacheStillValid() {
        return false;
    }
}