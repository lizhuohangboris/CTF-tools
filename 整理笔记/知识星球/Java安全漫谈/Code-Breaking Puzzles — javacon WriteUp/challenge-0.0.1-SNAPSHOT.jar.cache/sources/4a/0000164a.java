package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.repository.support.RedisRepositoryFactoryBean;

@AutoConfigureAfter({RedisAutoConfiguration.class})
@ConditionalOnMissingBean({RedisRepositoryFactoryBean.class})
@ConditionalOnProperty(prefix = "spring.data.redis.repositories", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
@Import({RedisRepositoriesAutoConfigureRegistrar.class})
@Configuration
@ConditionalOnClass({EnableRedisRepositories.class})
@ConditionalOnBean({RedisConnectionFactory.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/redis/RedisRepositoriesAutoConfiguration.class */
public class RedisRepositoriesAutoConfiguration {
}