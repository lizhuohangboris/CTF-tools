package org.springframework.boot.autoconfigure.cache;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "spring.cache")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/CacheProperties.class */
public class CacheProperties {
    private CacheType type;
    private List<String> cacheNames = new ArrayList();
    private final Caffeine caffeine = new Caffeine();
    private final Couchbase couchbase = new Couchbase();
    private final EhCache ehcache = new EhCache();
    private final Infinispan infinispan = new Infinispan();
    private final JCache jcache = new JCache();
    private final Redis redis = new Redis();

    public CacheType getType() {
        return this.type;
    }

    public void setType(CacheType mode) {
        this.type = mode;
    }

    public List<String> getCacheNames() {
        return this.cacheNames;
    }

    public void setCacheNames(List<String> cacheNames) {
        this.cacheNames = cacheNames;
    }

    public Caffeine getCaffeine() {
        return this.caffeine;
    }

    public Couchbase getCouchbase() {
        return this.couchbase;
    }

    public EhCache getEhcache() {
        return this.ehcache;
    }

    public Infinispan getInfinispan() {
        return this.infinispan;
    }

    public JCache getJcache() {
        return this.jcache;
    }

    public Redis getRedis() {
        return this.redis;
    }

    public Resource resolveConfigLocation(Resource config) {
        if (config != null) {
            Assert.isTrue(config.exists(), () -> {
                return "Cache configuration does not exist '" + config.getDescription() + "'";
            });
            return config;
        }
        return null;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/CacheProperties$Caffeine.class */
    public static class Caffeine {
        private String spec;

        public String getSpec() {
            return this.spec;
        }

        public void setSpec(String spec) {
            this.spec = spec;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/CacheProperties$Couchbase.class */
    public static class Couchbase {
        private Duration expiration;

        public Duration getExpiration() {
            return this.expiration;
        }

        public void setExpiration(Duration expiration) {
            this.expiration = expiration;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/CacheProperties$EhCache.class */
    public static class EhCache {
        private Resource config;

        public Resource getConfig() {
            return this.config;
        }

        public void setConfig(Resource config) {
            this.config = config;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/CacheProperties$Infinispan.class */
    public static class Infinispan {
        private Resource config;

        public Resource getConfig() {
            return this.config;
        }

        public void setConfig(Resource config) {
            this.config = config;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/CacheProperties$JCache.class */
    public static class JCache {
        private Resource config;
        private String provider;

        public String getProvider() {
            return this.provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public Resource getConfig() {
            return this.config;
        }

        public void setConfig(Resource config) {
            this.config = config;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/CacheProperties$Redis.class */
    public static class Redis {
        private Duration timeToLive;
        private String keyPrefix;
        private boolean cacheNullValues = true;
        private boolean useKeyPrefix = true;

        public Duration getTimeToLive() {
            return this.timeToLive;
        }

        public void setTimeToLive(Duration timeToLive) {
            this.timeToLive = timeToLive;
        }

        public boolean isCacheNullValues() {
            return this.cacheNullValues;
        }

        public void setCacheNullValues(boolean cacheNullValues) {
            this.cacheNullValues = cacheNullValues;
        }

        public String getKeyPrefix() {
            return this.keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public boolean isUseKeyPrefix() {
            return this.useKeyPrefix;
        }

        public void setUseKeyPrefix(boolean useKeyPrefix) {
            this.useKeyPrefix = useKeyPrefix;
        }
    }
}