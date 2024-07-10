package org.springframework.cache.support;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/support/AbstractCacheManager.class */
public abstract class AbstractCacheManager implements CacheManager, InitializingBean {
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap(16);
    private volatile Set<String> cacheNames = Collections.emptySet();

    protected abstract Collection<? extends Cache> loadCaches();

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        initializeCaches();
    }

    public void initializeCaches() {
        Collection<? extends Cache> caches = loadCaches();
        synchronized (this.cacheMap) {
            this.cacheNames = Collections.emptySet();
            this.cacheMap.clear();
            Set<String> cacheNames = new LinkedHashSet<>(caches.size());
            for (Cache cache : caches) {
                String name = cache.getName();
                this.cacheMap.put(name, decorateCache(cache));
                cacheNames.add(name);
            }
            this.cacheNames = Collections.unmodifiableSet(cacheNames);
        }
    }

    @Override // org.springframework.cache.CacheManager
    @Nullable
    public Cache getCache(String name) {
        Cache cache;
        Cache cache2 = this.cacheMap.get(name);
        if (cache2 != null) {
            return cache2;
        }
        synchronized (this.cacheMap) {
            Cache cache3 = this.cacheMap.get(name);
            if (cache3 == null) {
                cache3 = getMissingCache(name);
                if (cache3 != null) {
                    cache3 = decorateCache(cache3);
                    this.cacheMap.put(name, cache3);
                    updateCacheNames(name);
                }
            }
            cache = cache3;
        }
        return cache;
    }

    @Override // org.springframework.cache.CacheManager
    public Collection<String> getCacheNames() {
        return this.cacheNames;
    }

    @Nullable
    protected final Cache lookupCache(String name) {
        return this.cacheMap.get(name);
    }

    @Deprecated
    protected final void addCache(Cache cache) {
        String name = cache.getName();
        synchronized (this.cacheMap) {
            if (this.cacheMap.put(name, decorateCache(cache)) == null) {
                updateCacheNames(name);
            }
        }
    }

    private void updateCacheNames(String name) {
        Set<String> cacheNames = new LinkedHashSet<>(this.cacheNames.size() + 1);
        cacheNames.addAll(this.cacheNames);
        cacheNames.add(name);
        this.cacheNames = Collections.unmodifiableSet(cacheNames);
    }

    protected Cache decorateCache(Cache cache) {
        return cache;
    }

    @Nullable
    protected Cache getMissingCache(String name) {
        return null;
    }
}