package org.springframework.boot.autoconfigure.session;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.mongo.MongoOperationsSessionRepository;
import org.springframework.session.data.mongo.config.annotation.web.http.MongoHttpSessionConfiguration;

@EnableConfigurationProperties({MongoSessionProperties.class})
@Configuration
@ConditionalOnClass({MongoOperations.class, MongoOperationsSessionRepository.class})
@ConditionalOnMissingBean({SessionRepository.class})
@ConditionalOnBean({MongoOperations.class})
@Conditional({ServletSessionCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/MongoSessionConfiguration.class */
class MongoSessionConfiguration {
    MongoSessionConfiguration() {
    }

    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/MongoSessionConfiguration$SpringBootMongoHttpSessionConfiguration.class */
    public static class SpringBootMongoHttpSessionConfiguration extends MongoHttpSessionConfiguration {
        @Autowired
        public void customize(SessionProperties sessionProperties, MongoSessionProperties mongoSessionProperties) {
            Duration timeout = sessionProperties.getTimeout();
            if (timeout != null) {
                setMaxInactiveIntervalInSeconds(Integer.valueOf((int) timeout.getSeconds()));
            }
            setCollectionName(mongoSessionProperties.getCollectionName());
        }
    }
}