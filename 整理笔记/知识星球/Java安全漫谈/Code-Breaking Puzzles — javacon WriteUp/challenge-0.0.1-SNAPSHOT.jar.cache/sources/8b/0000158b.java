package org.springframework.boot.autoconfigure.cache;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.cache.CacheManager;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnClass({Caching.class, JCacheCacheManager.class})
@ConditionalOnMissingBean({CacheManager.class})
@Conditional({CacheCondition.class, JCacheAvailableCondition.class})
@Import({HazelcastJCacheCustomizationConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/JCacheCacheConfiguration.class */
class JCacheCacheConfiguration implements BeanClassLoaderAware {
    private final CacheProperties cacheProperties;
    private final CacheManagerCustomizers customizers;
    private final javax.cache.configuration.Configuration<?, ?> defaultCacheConfiguration;
    private final ObjectProvider<JCacheManagerCustomizer> cacheManagerCustomizers;
    private final ObjectProvider<JCachePropertiesCustomizer> cachePropertiesCustomizers;
    private ClassLoader beanClassLoader;

    JCacheCacheConfiguration(CacheProperties cacheProperties, CacheManagerCustomizers customizers, ObjectProvider<javax.cache.configuration.Configuration<?, ?>> defaultCacheConfiguration, ObjectProvider<JCacheManagerCustomizer> cacheManagerCustomizers, ObjectProvider<JCachePropertiesCustomizer> cachePropertiesCustomizers) {
        this.cacheProperties = cacheProperties;
        this.customizers = customizers;
        this.defaultCacheConfiguration = defaultCacheConfiguration.getIfAvailable();
        this.cacheManagerCustomizers = cacheManagerCustomizers;
        this.cachePropertiesCustomizers = cachePropertiesCustomizers;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Bean
    public JCacheCacheManager cacheManager(javax.cache.CacheManager jCacheCacheManager) {
        JCacheCacheManager cacheManager = new JCacheCacheManager(jCacheCacheManager);
        return this.customizers.customize(cacheManager);
    }

    @ConditionalOnMissingBean
    @Bean
    public javax.cache.CacheManager jCacheCacheManager() throws IOException {
        javax.cache.CacheManager jCacheCacheManager = createCacheManager();
        List<String> cacheNames = this.cacheProperties.getCacheNames();
        if (!CollectionUtils.isEmpty(cacheNames)) {
            for (String cacheName : cacheNames) {
                jCacheCacheManager.createCache(cacheName, getDefaultCacheConfiguration());
            }
        }
        customize(jCacheCacheManager);
        return jCacheCacheManager;
    }

    private javax.cache.CacheManager createCacheManager() throws IOException {
        CachingProvider cachingProvider = getCachingProvider(this.cacheProperties.getJcache().getProvider());
        Properties properties = createCacheManagerProperties();
        Resource configLocation = this.cacheProperties.resolveConfigLocation(this.cacheProperties.getJcache().getConfig());
        if (configLocation != null) {
            return cachingProvider.getCacheManager(configLocation.getURI(), this.beanClassLoader, properties);
        }
        return cachingProvider.getCacheManager((URI) null, this.beanClassLoader, properties);
    }

    private CachingProvider getCachingProvider(String cachingProviderFqn) {
        if (StringUtils.hasText(cachingProviderFqn)) {
            return Caching.getCachingProvider(cachingProviderFqn);
        }
        return Caching.getCachingProvider();
    }

    private Properties createCacheManagerProperties() {
        Properties properties = new Properties();
        this.cachePropertiesCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(this.cacheProperties, properties);
        });
        return properties;
    }

    private javax.cache.configuration.Configuration<?, ?> getDefaultCacheConfiguration() {
        if (this.defaultCacheConfiguration != null) {
            return this.defaultCacheConfiguration;
        }
        return new MutableConfiguration();
    }

    private void customize(javax.cache.CacheManager cacheManager) {
        this.cacheManagerCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(cacheManager);
        });
    }

    @Order(Integer.MAX_VALUE)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/JCacheCacheConfiguration$JCacheAvailableCondition.class */
    static class JCacheAvailableCondition extends AnyNestedCondition {
        JCacheAvailableCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @Conditional({JCacheProviderAvailableCondition.class})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/JCacheCacheConfiguration$JCacheAvailableCondition$JCacheProvider.class */
        static class JCacheProvider {
            JCacheProvider() {
            }
        }

        @ConditionalOnSingleCandidate(javax.cache.CacheManager.class)
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/JCacheCacheConfiguration$JCacheAvailableCondition$CustomJCacheCacheManager.class */
        static class CustomJCacheCacheManager {
            CustomJCacheCacheManager() {
            }
        }
    }

    @Order(Integer.MAX_VALUE)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/JCacheCacheConfiguration$JCacheProviderAvailableCondition.class */
    static class JCacheProviderAvailableCondition extends SpringBootCondition {
        JCacheProviderAvailableCondition() {
        }

        @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            ConditionMessage.Builder message = ConditionMessage.forCondition("JCache", new Object[0]);
            if (context.getEnvironment().containsProperty("spring.cache.jcache.provider")) {
                return ConditionOutcome.match(message.because("JCache provider specified"));
            }
            Iterator<CachingProvider> providers = Caching.getCachingProviders().iterator();
            if (!providers.hasNext()) {
                return ConditionOutcome.noMatch(message.didNotFind("JSR-107 provider").atAll());
            }
            providers.next();
            if (providers.hasNext()) {
                return ConditionOutcome.noMatch(message.foundExactly("multiple JSR-107 providers"));
            }
            return ConditionOutcome.match(message.foundExactly("single JSR-107 provider"));
        }
    }
}