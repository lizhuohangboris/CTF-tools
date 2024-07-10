package org.springframework.boot.autoconfigure.cache;

import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@ConditionalOnMissingBean({CacheManager.class})
@Configuration
@Conditional({CacheCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/SimpleCacheConfiguration.class */
class SimpleCacheConfiguration {
    private final CacheProperties cacheProperties;
    private final CacheManagerCustomizers customizerInvoker;

    SimpleCacheConfiguration(CacheProperties cacheProperties, CacheManagerCustomizers customizerInvoker) {
        this.cacheProperties = cacheProperties;
        this.customizerInvoker = customizerInvoker;
    }

    @Bean
    public ConcurrentMapCacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        List<String> cacheNames = this.cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            cacheManager.setCacheNames(cacheNames);
        }
        return (ConcurrentMapCacheManager) this.customizerInvoker.customize(cacheManager);
    }
}