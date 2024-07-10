package org.springframework.cache.interceptor;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.PropertyAccessor;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/CacheOperation.class */
public abstract class CacheOperation implements BasicOperation {
    private final String name;
    private final Set<String> cacheNames;
    private final String key;
    private final String keyGenerator;
    private final String cacheManager;
    private final String cacheResolver;
    private final String condition;
    private final String toString;

    /* JADX INFO: Access modifiers changed from: protected */
    public CacheOperation(Builder b) {
        this.name = b.name;
        this.cacheNames = b.cacheNames;
        this.key = b.key;
        this.keyGenerator = b.keyGenerator;
        this.cacheManager = b.cacheManager;
        this.cacheResolver = b.cacheResolver;
        this.condition = b.condition;
        this.toString = b.getOperationDescription().toString();
    }

    public String getName() {
        return this.name;
    }

    @Override // org.springframework.cache.interceptor.BasicOperation
    public Set<String> getCacheNames() {
        return this.cacheNames;
    }

    public String getKey() {
        return this.key;
    }

    public String getKeyGenerator() {
        return this.keyGenerator;
    }

    public String getCacheManager() {
        return this.cacheManager;
    }

    public String getCacheResolver() {
        return this.cacheResolver;
    }

    public String getCondition() {
        return this.condition;
    }

    public boolean equals(Object other) {
        return (other instanceof CacheOperation) && toString().equals(other.toString());
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public final String toString() {
        return this.toString;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/CacheOperation$Builder.class */
    public static abstract class Builder {
        private String name = "";
        private Set<String> cacheNames = Collections.emptySet();
        private String key = "";
        private String keyGenerator = "";
        private String cacheManager = "";
        private String cacheResolver = "";
        private String condition = "";

        public abstract CacheOperation build();

        public void setName(String name) {
            Assert.hasText(name, "Name must not be empty");
            this.name = name;
        }

        public void setCacheName(String cacheName) {
            Assert.hasText(cacheName, "Cache name must not be empty");
            this.cacheNames = Collections.singleton(cacheName);
        }

        public void setCacheNames(String... cacheNames) {
            this.cacheNames = new LinkedHashSet(cacheNames.length);
            for (String cacheName : cacheNames) {
                Assert.hasText(cacheName, "Cache name must be non-empty if specified");
                this.cacheNames.add(cacheName);
            }
        }

        public Set<String> getCacheNames() {
            return this.cacheNames;
        }

        public void setKey(String key) {
            Assert.notNull(key, "Key must not be null");
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }

        public String getKeyGenerator() {
            return this.keyGenerator;
        }

        public String getCacheManager() {
            return this.cacheManager;
        }

        public String getCacheResolver() {
            return this.cacheResolver;
        }

        public void setKeyGenerator(String keyGenerator) {
            Assert.notNull(keyGenerator, "KeyGenerator name must not be null");
            this.keyGenerator = keyGenerator;
        }

        public void setCacheManager(String cacheManager) {
            Assert.notNull(cacheManager, "CacheManager name must not be null");
            this.cacheManager = cacheManager;
        }

        public void setCacheResolver(String cacheResolver) {
            Assert.notNull(cacheResolver, "CacheResolver name must not be null");
            this.cacheResolver = cacheResolver;
        }

        public void setCondition(String condition) {
            Assert.notNull(condition, "Condition must not be null");
            this.condition = condition;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public StringBuilder getOperationDescription() {
            StringBuilder result = new StringBuilder(getClass().getSimpleName());
            result.append(PropertyAccessor.PROPERTY_KEY_PREFIX).append(this.name);
            result.append("] caches=").append(this.cacheNames);
            result.append(" | key='").append(this.key);
            result.append("' | keyGenerator='").append(this.keyGenerator);
            result.append("' | cacheManager='").append(this.cacheManager);
            result.append("' | cacheResolver='").append(this.cacheResolver);
            result.append("' | condition='").append(this.condition).append("'");
            return result;
        }
    }
}