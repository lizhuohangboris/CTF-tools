package org.springframework.core.type.classreading;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/classreading/CachingMetadataReaderFactory.class */
public class CachingMetadataReaderFactory extends SimpleMetadataReaderFactory {
    public static final int DEFAULT_CACHE_LIMIT = 256;
    @Nullable
    private Map<Resource, MetadataReader> metadataReaderCache;

    public CachingMetadataReaderFactory() {
        setCacheLimit(256);
    }

    public CachingMetadataReaderFactory(@Nullable ClassLoader classLoader) {
        super(classLoader);
        setCacheLimit(256);
    }

    public CachingMetadataReaderFactory(@Nullable ResourceLoader resourceLoader) {
        super(resourceLoader);
        if (resourceLoader instanceof DefaultResourceLoader) {
            this.metadataReaderCache = ((DefaultResourceLoader) resourceLoader).getResourceCache(MetadataReader.class);
        } else {
            setCacheLimit(256);
        }
    }

    public void setCacheLimit(int cacheLimit) {
        if (cacheLimit <= 0) {
            this.metadataReaderCache = null;
        } else if (this.metadataReaderCache instanceof LocalResourceCache) {
            ((LocalResourceCache) this.metadataReaderCache).setCacheLimit(cacheLimit);
        } else {
            this.metadataReaderCache = new LocalResourceCache(cacheLimit);
        }
    }

    public int getCacheLimit() {
        if (this.metadataReaderCache instanceof LocalResourceCache) {
            return ((LocalResourceCache) this.metadataReaderCache).getCacheLimit();
        }
        return this.metadataReaderCache != null ? Integer.MAX_VALUE : 0;
    }

    @Override // org.springframework.core.type.classreading.SimpleMetadataReaderFactory, org.springframework.core.type.classreading.MetadataReaderFactory
    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        MetadataReader metadataReader;
        if (this.metadataReaderCache instanceof ConcurrentMap) {
            MetadataReader metadataReader2 = this.metadataReaderCache.get(resource);
            if (metadataReader2 == null) {
                metadataReader2 = super.getMetadataReader(resource);
                this.metadataReaderCache.put(resource, metadataReader2);
            }
            return metadataReader2;
        } else if (this.metadataReaderCache != null) {
            synchronized (this.metadataReaderCache) {
                MetadataReader metadataReader3 = this.metadataReaderCache.get(resource);
                if (metadataReader3 == null) {
                    metadataReader3 = super.getMetadataReader(resource);
                    this.metadataReaderCache.put(resource, metadataReader3);
                }
                metadataReader = metadataReader3;
            }
            return metadataReader;
        } else {
            return super.getMetadataReader(resource);
        }
    }

    public void clearCache() {
        if (this.metadataReaderCache instanceof LocalResourceCache) {
            synchronized (this.metadataReaderCache) {
                this.metadataReaderCache.clear();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/classreading/CachingMetadataReaderFactory$LocalResourceCache.class */
    public static class LocalResourceCache extends LinkedHashMap<Resource, MetadataReader> {
        private volatile int cacheLimit;

        public LocalResourceCache(int cacheLimit) {
            super(cacheLimit, 0.75f, true);
        }

        public void setCacheLimit(int cacheLimit) {
            this.cacheLimit = cacheLimit;
        }

        public int getCacheLimit() {
            return this.cacheLimit;
        }

        @Override // java.util.LinkedHashMap
        protected boolean removeEldestEntry(Map.Entry<Resource, MetadataReader> eldest) {
            return size() > this.cacheLimit;
        }
    }
}