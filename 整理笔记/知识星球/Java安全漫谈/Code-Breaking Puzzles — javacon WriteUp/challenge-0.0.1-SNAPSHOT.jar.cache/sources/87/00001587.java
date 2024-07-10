package org.springframework.boot.autoconfigure.cache;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import java.io.IOException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({HazelcastInstance.class, HazelcastCacheManager.class})
@ConditionalOnSingleCandidate(HazelcastInstance.class)
@ConditionalOnMissingBean({CacheManager.class})
@Conditional({CacheCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/HazelcastCacheConfiguration.class */
class HazelcastCacheConfiguration {
    private final CacheManagerCustomizers customizers;

    HazelcastCacheConfiguration(CacheManagerCustomizers customizers) {
        this.customizers = customizers;
    }

    @Bean
    public HazelcastCacheManager cacheManager(HazelcastInstance existingHazelcastInstance) throws IOException {
        HazelcastCacheManager cacheManager = new HazelcastCacheManager(existingHazelcastInstance);
        return this.customizers.customize(cacheManager);
    }
}