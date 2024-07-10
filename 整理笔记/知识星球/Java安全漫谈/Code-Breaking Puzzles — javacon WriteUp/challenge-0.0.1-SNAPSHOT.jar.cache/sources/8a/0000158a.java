package org.springframework.boot.autoconfigure.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.catalina.Lifecycle;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.spring.provider.SpringEmbeddedCacheManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

@Configuration
@ConditionalOnClass({SpringEmbeddedCacheManager.class})
@ConditionalOnMissingBean({CacheManager.class})
@Conditional({CacheCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/InfinispanCacheConfiguration.class */
public class InfinispanCacheConfiguration {
    private final CacheProperties cacheProperties;
    private final CacheManagerCustomizers customizers;
    private final ConfigurationBuilder defaultConfigurationBuilder;

    public InfinispanCacheConfiguration(CacheProperties cacheProperties, CacheManagerCustomizers customizers, ObjectProvider<ConfigurationBuilder> defaultConfigurationBuilder) {
        this.cacheProperties = cacheProperties;
        this.customizers = customizers;
        this.defaultConfigurationBuilder = defaultConfigurationBuilder.getIfAvailable();
    }

    @Bean
    public SpringEmbeddedCacheManager cacheManager(EmbeddedCacheManager embeddedCacheManager) {
        SpringEmbeddedCacheManager cacheManager = new SpringEmbeddedCacheManager(embeddedCacheManager);
        return this.customizers.customize(cacheManager);
    }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = Lifecycle.STOP_EVENT)
    public EmbeddedCacheManager infinispanCacheManager() throws IOException {
        EmbeddedCacheManager cacheManager = createEmbeddedCacheManager();
        List<String> cacheNames = this.cacheProperties.getCacheNames();
        if (!CollectionUtils.isEmpty(cacheNames)) {
            cacheNames.forEach(cacheName -> {
                cacheManager.defineConfiguration(cacheName, getDefaultCacheConfiguration());
            });
        }
        return cacheManager;
    }

    private EmbeddedCacheManager createEmbeddedCacheManager() throws IOException {
        Resource location = this.cacheProperties.resolveConfigLocation(this.cacheProperties.getInfinispan().getConfig());
        if (location != null) {
            InputStream in = location.getInputStream();
            Throwable th = null;
            try {
                DefaultCacheManager defaultCacheManager = new DefaultCacheManager(in);
                if (in != null) {
                    if (0 != 0) {
                        try {
                            in.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        in.close();
                    }
                }
                return defaultCacheManager;
            } finally {
            }
        } else {
            return new DefaultCacheManager();
        }
    }

    private org.infinispan.configuration.cache.Configuration getDefaultCacheConfiguration() {
        if (this.defaultConfigurationBuilder != null) {
            return this.defaultConfigurationBuilder.build();
        }
        return new ConfigurationBuilder().build();
    }
}