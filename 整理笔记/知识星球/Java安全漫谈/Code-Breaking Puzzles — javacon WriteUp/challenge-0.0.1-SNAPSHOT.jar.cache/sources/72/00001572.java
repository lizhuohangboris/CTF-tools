package org.springframework.boot.autoconfigure.cache;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.util.Assert;

@EnableConfigurationProperties({CacheProperties.class})
@AutoConfigureAfter({CouchbaseAutoConfiguration.class, HazelcastAutoConfiguration.class, HibernateJpaAutoConfiguration.class, RedisAutoConfiguration.class})
@ConditionalOnMissingBean(value = {CacheManager.class}, name = {"cacheResolver"})
@Import({CacheConfigurationImportSelector.class})
@Configuration
@ConditionalOnClass({CacheManager.class})
@ConditionalOnBean({CacheAspectSupport.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/CacheAutoConfiguration.class */
public class CacheAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public CacheManagerCustomizers cacheManagerCustomizers(ObjectProvider<CacheManagerCustomizer<?>> customizers) {
        return new CacheManagerCustomizers((List) customizers.orderedStream().collect(Collectors.toList()));
    }

    @Bean
    public CacheManagerValidator cacheAutoConfigurationValidator(CacheProperties cacheProperties, ObjectProvider<CacheManager> cacheManager) {
        return new CacheManagerValidator(cacheProperties, cacheManager);
    }

    @Configuration
    @ConditionalOnClass({LocalContainerEntityManagerFactoryBean.class})
    @ConditionalOnBean({AbstractEntityManagerFactoryBean.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/CacheAutoConfiguration$CacheManagerJpaDependencyConfiguration.class */
    protected static class CacheManagerJpaDependencyConfiguration extends EntityManagerFactoryDependsOnPostProcessor {
        public CacheManagerJpaDependencyConfiguration() {
            super("cacheManager");
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/CacheAutoConfiguration$CacheManagerValidator.class */
    static class CacheManagerValidator implements InitializingBean {
        private final CacheProperties cacheProperties;
        private final ObjectProvider<CacheManager> cacheManager;

        CacheManagerValidator(CacheProperties cacheProperties, ObjectProvider<CacheManager> cacheManager) {
            this.cacheProperties = cacheProperties;
            this.cacheManager = cacheManager;
        }

        @Override // org.springframework.beans.factory.InitializingBean
        public void afterPropertiesSet() {
            Assert.notNull(this.cacheManager.getIfAvailable(), () -> {
                return "No cache manager could be auto-configured, check your configuration (caching type is '" + this.cacheProperties.getType() + "')";
            });
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/CacheAutoConfiguration$CacheConfigurationImportSelector.class */
    static class CacheConfigurationImportSelector implements ImportSelector {
        CacheConfigurationImportSelector() {
        }

        @Override // org.springframework.context.annotation.ImportSelector
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            CacheType[] types = CacheType.values();
            String[] imports = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                imports[i] = CacheConfigurations.getConfigurationClass(types[i]);
            }
            return imports;
        }
    }
}