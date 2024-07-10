package org.springframework.boot.autoconfigure.session;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.data.redis.ReactiveRedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.server.RedisWebSessionConfiguration;

@EnableConfigurationProperties({RedisSessionProperties.class})
@Configuration
@ConditionalOnClass({ReactiveRedisConnectionFactory.class, ReactiveRedisOperationsSessionRepository.class})
@ConditionalOnMissingBean({ReactiveSessionRepository.class})
@ConditionalOnBean({ReactiveRedisConnectionFactory.class})
@Conditional({ReactiveSessionCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/RedisReactiveSessionConfiguration.class */
class RedisReactiveSessionConfiguration {
    RedisReactiveSessionConfiguration() {
    }

    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/RedisReactiveSessionConfiguration$SpringBootRedisWebSessionConfiguration.class */
    static class SpringBootRedisWebSessionConfiguration extends RedisWebSessionConfiguration {
        SpringBootRedisWebSessionConfiguration() {
        }

        @Autowired
        public void customize(SessionProperties sessionProperties, RedisSessionProperties redisSessionProperties) {
            Duration timeout = sessionProperties.getTimeout();
            if (timeout != null) {
                setMaxInactiveIntervalInSeconds((int) timeout.getSeconds());
            }
            setRedisNamespace(redisSessionProperties.getNamespace());
            setRedisFlushMode(redisSessionProperties.getFlushMode());
        }
    }
}