package org.thymeleaf.cache;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/cache/AlwaysValidCacheEntryValidity.class */
public class AlwaysValidCacheEntryValidity implements ICacheEntryValidity {
    public static final AlwaysValidCacheEntryValidity INSTANCE = new AlwaysValidCacheEntryValidity();

    @Override // org.thymeleaf.cache.ICacheEntryValidity
    public boolean isCacheable() {
        return true;
    }

    @Override // org.thymeleaf.cache.ICacheEntryValidity
    public boolean isCacheStillValid() {
        return true;
    }
}