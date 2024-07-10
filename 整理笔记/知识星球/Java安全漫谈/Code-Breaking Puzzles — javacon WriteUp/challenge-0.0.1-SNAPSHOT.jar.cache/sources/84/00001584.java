package org.springframework.boot.autoconfigure.cache;

import net.sf.ehcache.Cache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ResourceCondition;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@ConditionalOnClass({Cache.class, EhCacheCacheManager.class})
@ConditionalOnMissingBean({CacheManager.class})
@Conditional({CacheCondition.class, ConfigAvailableCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/EhCacheCacheConfiguration.class */
class EhCacheCacheConfiguration {
    private final CacheProperties cacheProperties;
    private final CacheManagerCustomizers customizers;

    EhCacheCacheConfiguration(CacheProperties cacheProperties, CacheManagerCustomizers customizers) {
        this.cacheProperties = cacheProperties;
        this.customizers = customizers;
    }

    @Bean
    public EhCacheCacheManager cacheManager(net.sf.ehcache.CacheManager ehCacheCacheManager) {
        return this.customizers.customize(new EhCacheCacheManager(ehCacheCacheManager));
    }

    @ConditionalOnMissingBean
    @Bean
    public net.sf.ehcache.CacheManager ehCacheCacheManager() {
        Resource location = this.cacheProperties.resolveConfigLocation(this.cacheProperties.getEhcache().getConfig());
        if (location != null) {
            return EhCacheManagerUtils.buildCacheManager(location);
        }
        return EhCacheManagerUtils.buildCacheManager();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/EhCacheCacheConfiguration$ConfigAvailableCondition.class */
    static class ConfigAvailableCondition extends ResourceCondition {
        ConfigAvailableCondition() {
            super("EhCache", "spring.cache.ehcache.config", "classpath:/ehcache.xml");
        }
    }
}