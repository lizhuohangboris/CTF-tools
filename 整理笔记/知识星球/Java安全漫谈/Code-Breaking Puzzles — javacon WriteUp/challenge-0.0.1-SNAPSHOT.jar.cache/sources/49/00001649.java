package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import reactor.core.publisher.Flux;

@Configuration
@ConditionalOnClass({ReactiveRedisConnectionFactory.class, ReactiveRedisTemplate.class, Flux.class})
@AutoConfigureAfter({RedisAutoConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/redis/RedisReactiveAutoConfiguration.class */
public class RedisReactiveAutoConfiguration {
    @ConditionalOnMissingBean(name = {"reactiveRedisTemplate"})
    @ConditionalOnBean({ReactiveRedisConnectionFactory.class})
    @Bean
    public ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, ResourceLoader resourceLoader) {
        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(resourceLoader.getClassLoader());
        RedisSerializationContext<Object, Object> serializationContext = RedisSerializationContext.newSerializationContext().key(jdkSerializer).value(jdkSerializer).hashKey(jdkSerializer).hashValue(jdkSerializer).build();
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
    }
}