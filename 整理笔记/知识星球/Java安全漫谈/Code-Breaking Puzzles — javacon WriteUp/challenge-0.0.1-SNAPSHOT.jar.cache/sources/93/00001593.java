package org.springframework.boot.autoconfigure.cache;

import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@ConditionalOnClass({RedisConnectionFactory.class})
@AutoConfigureAfter({RedisAutoConfiguration.class})
@ConditionalOnMissingBean({CacheManager.class})
@ConditionalOnBean({RedisConnectionFactory.class})
@Conditional({CacheCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/RedisCacheConfiguration.class */
class RedisCacheConfiguration {
    private final CacheProperties cacheProperties;
    private final CacheManagerCustomizers customizerInvoker;
    private final org.springframework.data.redis.cache.RedisCacheConfiguration redisCacheConfiguration;

    RedisCacheConfiguration(CacheProperties cacheProperties, CacheManagerCustomizers customizerInvoker, ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration) {
        this.cacheProperties = cacheProperties;
        this.customizerInvoker = customizerInvoker;
        this.redisCacheConfiguration = redisCacheConfiguration.getIfAvailable();
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, ResourceLoader resourceLoader) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(determineConfiguration(resourceLoader.getClassLoader()));
        List<String> cacheNames = this.cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            builder.initialCacheNames(new LinkedHashSet(cacheNames));
        }
        return this.customizerInvoker.customize(builder.build());
    }

    private org.springframework.data.redis.cache.RedisCacheConfiguration determineConfiguration(ClassLoader classLoader) {
        if (this.redisCacheConfiguration != null) {
            return this.redisCacheConfiguration;
        }
        CacheProperties.Redis redisProperties = this.cacheProperties.getRedis();
        org.springframework.data.redis.cache.RedisCacheConfiguration config = org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(classLoader)));
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixKeysWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }
}