package org.apache.catalina.webresources;

import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.util.FileSize;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.catalina.WebResource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.scan.Constants;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/webresources/Cache.class */
public class Cache {
    private static final Log log = LogFactory.getLog(Cache.class);
    protected static final StringManager sm = StringManager.getManager(Cache.class);
    private static final long TARGET_FREE_PERCENT_GET = 5;
    private static final long TARGET_FREE_PERCENT_BACKGROUND = 10;
    private static final int OBJECT_MAX_SIZE_FACTOR = 20;
    private final StandardRoot root;
    private final AtomicLong size = new AtomicLong(0);
    private long ttl = 5000;
    private long maxSize = SizeBasedTriggeringPolicy.DEFAULT_MAX_FILE_SIZE;
    private int objectMaxSize = ((int) this.maxSize) / 20;
    private AtomicLong lookupCount = new AtomicLong(0);
    private AtomicLong hitCount = new AtomicLong(0);
    private final ConcurrentMap<String, CachedResource> resourceCache = new ConcurrentHashMap();

    public Cache(StandardRoot root) {
        this.root = root;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public WebResource getResource(String path, boolean useClassLoaderResources) {
        if (noCache(path)) {
            return this.root.getResourceInternal(path, useClassLoaderResources);
        }
        this.lookupCount.incrementAndGet();
        CachedResource cacheEntry = this.resourceCache.get(path);
        if (cacheEntry != null && !cacheEntry.validateResource(useClassLoaderResources)) {
            removeCacheEntry(path);
            cacheEntry = null;
        }
        if (cacheEntry == null) {
            int objectMaxSizeBytes = getObjectMaxSizeBytes();
            CachedResource newCacheEntry = new CachedResource(this, this.root, path, getTtl(), objectMaxSizeBytes, useClassLoaderResources);
            cacheEntry = this.resourceCache.putIfAbsent(path, newCacheEntry);
            if (cacheEntry == null) {
                cacheEntry = newCacheEntry;
                cacheEntry.validateResource(useClassLoaderResources);
                long delta = cacheEntry.getSize();
                this.size.addAndGet(delta);
                if (this.size.get() > this.maxSize) {
                    long targetSize = (this.maxSize * 95) / 100;
                    long newSize = evict(targetSize, this.resourceCache.values().iterator());
                    if (newSize > this.maxSize) {
                        removeCacheEntry(path);
                        log.warn(sm.getString("cache.addFail", path, this.root.getContext().getName()));
                    }
                }
            } else {
                cacheEntry.validateResource(useClassLoaderResources);
            }
        } else {
            this.hitCount.incrementAndGet();
        }
        return cacheEntry;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public WebResource[] getResources(String path, boolean useClassLoaderResources) {
        this.lookupCount.incrementAndGet();
        CachedResource cacheEntry = this.resourceCache.get(path);
        if (cacheEntry != null && !cacheEntry.validateResources(useClassLoaderResources)) {
            removeCacheEntry(path);
            cacheEntry = null;
        }
        if (cacheEntry == null) {
            int objectMaxSizeBytes = getObjectMaxSizeBytes();
            CachedResource newCacheEntry = new CachedResource(this, this.root, path, getTtl(), objectMaxSizeBytes, useClassLoaderResources);
            cacheEntry = this.resourceCache.putIfAbsent(path, newCacheEntry);
            if (cacheEntry == null) {
                cacheEntry = newCacheEntry;
                cacheEntry.validateResources(useClassLoaderResources);
                long delta = cacheEntry.getSize();
                this.size.addAndGet(delta);
                if (this.size.get() > this.maxSize) {
                    long targetSize = (this.maxSize * 95) / 100;
                    long newSize = evict(targetSize, this.resourceCache.values().iterator());
                    if (newSize > this.maxSize) {
                        removeCacheEntry(path);
                        log.warn(sm.getString("cache.addFail", path));
                    }
                }
            } else {
                cacheEntry.validateResources(useClassLoaderResources);
            }
        } else {
            this.hitCount.incrementAndGet();
        }
        return cacheEntry.getWebResources();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void backgroundProcess() {
        TreeSet<CachedResource> orderedResources = new TreeSet<>(new EvictionOrder());
        orderedResources.addAll(this.resourceCache.values());
        Iterator<CachedResource> iter = orderedResources.iterator();
        long targetSize = (this.maxSize * 90) / 100;
        long newSize = evict(targetSize, iter);
        if (newSize > targetSize) {
            log.info(sm.getString("cache.backgroundEvictFail", Long.valueOf((long) TARGET_FREE_PERCENT_BACKGROUND), this.root.getContext().getName(), Long.valueOf(newSize / FileSize.KB_COEFFICIENT)));
        }
    }

    private boolean noCache(String path) {
        if (!path.endsWith(ClassUtils.CLASS_FILE_SUFFIX) || (!path.startsWith("/WEB-INF/classes/") && !path.startsWith(Constants.WEB_INF_LIB))) {
            if (path.startsWith(Constants.WEB_INF_LIB) && path.endsWith(".jar")) {
                return true;
            }
            return false;
        }
        return true;
    }

    private long evict(long targetSize, Iterator<CachedResource> iter) {
        long now = System.currentTimeMillis();
        long newSize = this.size.get();
        while (newSize > targetSize && iter.hasNext()) {
            CachedResource resource = iter.next();
            if (resource.getNextCheck() <= now) {
                removeCacheEntry(resource.getWebappPath());
                newSize = this.size.get();
            }
        }
        return newSize;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeCacheEntry(String path) {
        CachedResource cachedResource = this.resourceCache.remove(path);
        if (cachedResource != null) {
            long delta = cachedResource.getSize();
            this.size.addAndGet(-delta);
        }
    }

    public long getTtl() {
        return this.ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public long getMaxSize() {
        return this.maxSize / FileSize.KB_COEFFICIENT;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize * FileSize.KB_COEFFICIENT;
    }

    public long getLookupCount() {
        return this.lookupCount.get();
    }

    public long getHitCount() {
        return this.hitCount.get();
    }

    public void setObjectMaxSize(int objectMaxSize) {
        if (objectMaxSize * FileSize.KB_COEFFICIENT > 2147483647L) {
            log.warn(sm.getString("cache.objectMaxSizeTooBigBytes", Integer.valueOf(objectMaxSize)));
            this.objectMaxSize = Integer.MAX_VALUE;
        }
        this.objectMaxSize = objectMaxSize * 1024;
    }

    public int getObjectMaxSize() {
        return this.objectMaxSize / 1024;
    }

    public int getObjectMaxSizeBytes() {
        return this.objectMaxSize;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void enforceObjectMaxSizeLimit() {
        long limit = this.maxSize / 20;
        if (limit <= 2147483647L && this.objectMaxSize > limit) {
            log.warn(sm.getString("cache.objectMaxSizeTooBig", Integer.valueOf(this.objectMaxSize / 1024), Integer.valueOf(((int) limit) / 1024)));
            this.objectMaxSize = (int) limit;
        }
    }

    public void clear() {
        this.resourceCache.clear();
        this.size.set(0L);
    }

    public long getSize() {
        return this.size.get() / FileSize.KB_COEFFICIENT;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/webresources/Cache$EvictionOrder.class */
    public static class EvictionOrder implements Comparator<CachedResource> {
        private EvictionOrder() {
        }

        @Override // java.util.Comparator
        public int compare(CachedResource cr1, CachedResource cr2) {
            long nc1 = cr1.getNextCheck();
            long nc2 = cr2.getNextCheck();
            if (nc1 == nc2) {
                return 0;
            }
            if (nc1 > nc2) {
                return -1;
            }
            return 1;
        }
    }
}