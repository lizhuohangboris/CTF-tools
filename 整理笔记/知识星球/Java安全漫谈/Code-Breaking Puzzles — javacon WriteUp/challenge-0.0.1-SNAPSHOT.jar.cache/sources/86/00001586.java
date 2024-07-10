package org.springframework.boot.autoconfigure.cache;

import java.util.Collection;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean({CacheManager.class})
@ConditionalOnBean({Cache.class})
@Conditional({CacheCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/GenericCacheConfiguration.class */
class GenericCacheConfiguration {
    private final CacheManagerCustomizers customizers;

    GenericCacheConfiguration(CacheManagerCustomizers customizers) {
        this.customizers = customizers;
    }

    @Bean
    public SimpleCacheManager cacheManager(Collection<Cache> caches) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return (SimpleCacheManager) this.customizers.customize(cacheManager);
    }
}