package org.springframework.boot.autoconfigure.data.redis;

import java.net.UnknownHostException;
import java.time.Duration;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ConditionalOnClass({GenericObjectPool.class, JedisConnection.class, Jedis.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/redis/JedisConnectionConfiguration.class */
class JedisConnectionConfiguration extends RedisConnectionConfiguration {
    private final RedisProperties properties;
    private final ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers;

    JedisConnectionConfiguration(RedisProperties properties, ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration, ObjectProvider<RedisClusterConfiguration> clusterConfiguration, ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
        super(properties, sentinelConfiguration, clusterConfiguration);
        this.properties = properties;
        this.builderCustomizers = builderCustomizers;
    }

    @ConditionalOnMissingBean({RedisConnectionFactory.class})
    @Bean
    public JedisConnectionFactory redisConnectionFactory() throws UnknownHostException {
        return createJedisConnectionFactory();
    }

    private JedisConnectionFactory createJedisConnectionFactory() {
        JedisClientConfiguration clientConfiguration = getJedisClientConfiguration();
        if (getSentinelConfig() != null) {
            return new JedisConnectionFactory(getSentinelConfig(), clientConfiguration);
        }
        if (getClusterConfiguration() != null) {
            return new JedisConnectionFactory(getClusterConfiguration(), clientConfiguration);
        }
        return new JedisConnectionFactory(getStandaloneConfig(), clientConfiguration);
    }

    private JedisClientConfiguration getJedisClientConfiguration() {
        JedisClientConfiguration.JedisClientConfigurationBuilder builder = applyProperties(JedisClientConfiguration.builder());
        RedisProperties.Pool pool = this.properties.getJedis().getPool();
        if (pool != null) {
            applyPooling(pool, builder);
        }
        if (StringUtils.hasText(this.properties.getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        customize(builder);
        return builder.build();
    }

    private JedisClientConfiguration.JedisClientConfigurationBuilder applyProperties(JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        if (this.properties.isSsl()) {
            builder.useSsl();
        }
        if (this.properties.getTimeout() != null) {
            Duration timeout = this.properties.getTimeout();
            builder.readTimeout(timeout).connectTimeout(timeout);
        }
        return builder;
    }

    private void applyPooling(RedisProperties.Pool pool, JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        builder.usePooling().poolConfig(jedisPoolConfig(pool));
    }

    private JedisPoolConfig jedisPoolConfig(RedisProperties.Pool pool) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(pool.getMaxActive());
        config.setMaxIdle(pool.getMaxIdle());
        config.setMinIdle(pool.getMinIdle());
        if (pool.getMaxWait() != null) {
            config.setMaxWaitMillis(pool.getMaxWait().toMillis());
        }
        return config;
    }

    private void customizeConfigurationFromUrl(JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        RedisConnectionConfiguration.ConnectionInfo connectionInfo = parseUrl(this.properties.getUrl());
        if (connectionInfo.isUseSsl()) {
            builder.useSsl();
        }
    }

    private void customize(JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        this.builderCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
    }
}