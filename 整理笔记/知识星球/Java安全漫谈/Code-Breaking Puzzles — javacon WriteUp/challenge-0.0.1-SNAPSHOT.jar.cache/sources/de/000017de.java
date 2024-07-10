package org.springframework.boot.autoconfigure.session;

import java.time.Duration;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.session.SessionRepository;
import org.springframework.session.jdbc.JdbcOperationsSessionRepository;
import org.springframework.session.jdbc.config.annotation.web.http.JdbcHttpSessionConfiguration;

@EnableConfigurationProperties({JdbcSessionProperties.class})
@Configuration
@ConditionalOnClass({JdbcTemplate.class, JdbcOperationsSessionRepository.class})
@ConditionalOnMissingBean({SessionRepository.class})
@ConditionalOnBean({DataSource.class})
@Conditional({ServletSessionCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/JdbcSessionConfiguration.class */
class JdbcSessionConfiguration {
    JdbcSessionConfiguration() {
    }

    @ConditionalOnMissingBean
    @Bean
    public JdbcSessionDataSourceInitializer jdbcSessionDataSourceInitializer(DataSource dataSource, ResourceLoader resourceLoader, JdbcSessionProperties properties) {
        return new JdbcSessionDataSourceInitializer(dataSource, resourceLoader, properties);
    }

    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/JdbcSessionConfiguration$SpringBootJdbcHttpSessionConfiguration.class */
    public static class SpringBootJdbcHttpSessionConfiguration extends JdbcHttpSessionConfiguration {
        @Autowired
        public void customize(SessionProperties sessionProperties, JdbcSessionProperties jdbcSessionProperties) {
            Duration timeout = sessionProperties.getTimeout();
            if (timeout != null) {
                setMaxInactiveIntervalInSeconds(Integer.valueOf((int) timeout.getSeconds()));
            }
            setTableName(jdbcSessionProperties.getTableName());
            setCleanupCron(jdbcSessionProperties.getCleanupCron());
        }
    }
}