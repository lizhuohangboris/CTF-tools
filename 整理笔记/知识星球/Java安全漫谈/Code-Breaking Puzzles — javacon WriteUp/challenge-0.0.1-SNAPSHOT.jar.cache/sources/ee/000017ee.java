package org.springframework.boot.autoconfigure.session;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

@EnableConfigurationProperties({RedisSessionProperties.class})
@Configuration
@ConditionalOnClass({RedisTemplate.class, RedisOperationsSessionRepository.class})
@ConditionalOnMissingBean({SessionRepository.class})
@ConditionalOnBean({RedisConnectionFactory.class})
@Conditional({ServletSessionCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/RedisSessionConfiguration.class */
class RedisSessionConfiguration {
    RedisSessionConfiguration() {
    }

    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/RedisSessionConfiguration$SpringBootRedisHttpSessionConfiguration.class */
    public static class SpringBootRedisHttpSessionConfiguration extends RedisHttpSessionConfiguration {
        @Autowired
        public void customize(SessionProperties sessionProperties, RedisSessionProperties redisSessionProperties) {
            Duration timeout = sessionProperties.getTimeout();
            if (timeout != null) {
                setMaxInactiveIntervalInSeconds((int) timeout.getSeconds());
            }
            setRedisNamespace(redisSessionProperties.getNamespace());
            setRedisFlushMode(redisSessionProperties.getFlushMode());
            setCleanupCron(redisSessionProperties.getCleanupCron());
        }
    }
}