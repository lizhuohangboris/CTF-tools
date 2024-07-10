package org.springframework.boot.autoconfigure.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@ConditionalOnMissingBean({CacheManager.class})
@Configuration
@Conditional({CacheCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/NoOpCacheConfiguration.class */
class NoOpCacheConfiguration {
    NoOpCacheConfiguration() {
    }

    @Bean
    public NoOpCacheManager cacheManager() {
        return new NoOpCacheManager();
    }
}