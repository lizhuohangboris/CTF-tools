package org.springframework.cache.annotation;

import java.util.Collection;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/annotation/AbstractCachingConfiguration.class */
public abstract class AbstractCachingConfiguration implements ImportAware {
    @Nullable
    protected AnnotationAttributes enableCaching;
    @Nullable
    protected Supplier<CacheManager> cacheManager;
    @Nullable
    protected Supplier<CacheResolver> cacheResolver;
    @Nullable
    protected Supplier<KeyGenerator> keyGenerator;
    @Nullable
    protected Supplier<CacheErrorHandler> errorHandler;

    @Override // org.springframework.context.annotation.ImportAware
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableCaching = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableCaching.class.getName(), false));
        if (this.enableCaching == null) {
            throw new IllegalArgumentException("@EnableCaching is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Autowired(required = false)
    void setConfigurers(Collection<CachingConfigurer> configurers) {
        if (CollectionUtils.isEmpty(configurers)) {
            return;
        }
        if (configurers.size() > 1) {
            throw new IllegalStateException(configurers.size() + " implementations of CachingConfigurer were found when only 1 was expected. Refactor the configuration such that CachingConfigurer is implemented only once or not at all.");
        }
        CachingConfigurer configurer = configurers.iterator().next();
        useCachingConfigurer(configurer);
    }

    protected void useCachingConfigurer(CachingConfigurer config) {
        config.getClass();
        this.cacheManager = this::cacheManager;
        config.getClass();
        this.cacheResolver = this::cacheResolver;
        config.getClass();
        this.keyGenerator = this::keyGenerator;
        config.getClass();
        this.errorHandler = this::errorHandler;
    }
}