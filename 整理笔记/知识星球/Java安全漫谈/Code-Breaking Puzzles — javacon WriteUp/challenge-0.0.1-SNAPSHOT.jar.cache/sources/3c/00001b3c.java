package org.springframework.cache.annotation;

import org.springframework.cache.config.CacheManagementConfigUtils;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration
@Role(2)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/annotation/ProxyCachingConfiguration.class */
public class ProxyCachingConfiguration extends AbstractCachingConfiguration {
    @Bean(name = {CacheManagementConfigUtils.CACHE_ADVISOR_BEAN_NAME})
    @Role(2)
    public BeanFactoryCacheOperationSourceAdvisor cacheAdvisor() {
        BeanFactoryCacheOperationSourceAdvisor advisor = new BeanFactoryCacheOperationSourceAdvisor();
        advisor.setCacheOperationSource(cacheOperationSource());
        advisor.setAdvice(cacheInterceptor());
        if (this.enableCaching != null) {
            advisor.setOrder(((Integer) this.enableCaching.getNumber("order")).intValue());
        }
        return advisor;
    }

    @Bean
    @Role(2)
    public CacheOperationSource cacheOperationSource() {
        return new AnnotationCacheOperationSource();
    }

    @Bean
    @Role(2)
    public CacheInterceptor cacheInterceptor() {
        CacheInterceptor interceptor = new CacheInterceptor();
        interceptor.configure(this.errorHandler, this.keyGenerator, this.cacheResolver, this.cacheManager);
        interceptor.setCacheOperationSource(cacheOperationSource());
        return interceptor;
    }
}