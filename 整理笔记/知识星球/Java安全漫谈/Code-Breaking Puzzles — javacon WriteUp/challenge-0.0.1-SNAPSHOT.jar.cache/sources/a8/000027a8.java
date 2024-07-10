package org.thymeleaf.cache;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/cache/TTLCacheEntryValidity.class */
public class TTLCacheEntryValidity implements ICacheEntryValidity {
    private final long cacheTTLMs;
    private final long creationTimeInMillis = System.currentTimeMillis();

    public TTLCacheEntryValidity(long cacheTTLMs) {
        this.cacheTTLMs = cacheTTLMs;
    }

    public long getCacheTTLMs() {
        return this.cacheTTLMs;
    }

    @Override // org.thymeleaf.cache.ICacheEntryValidity
    public boolean isCacheable() {
        return true;
    }

    @Override // org.thymeleaf.cache.ICacheEntryValidity
    public boolean isCacheStillValid() {
        long currentTimeInMillis = System.currentTimeMillis();
        return currentTimeInMillis < this.creationTimeInMillis + this.cacheTTLMs;
    }
}