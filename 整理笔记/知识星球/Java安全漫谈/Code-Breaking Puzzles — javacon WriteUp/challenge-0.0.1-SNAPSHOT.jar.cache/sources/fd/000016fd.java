package org.springframework.boot.autoconfigure.jdbc;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@EnableConfigurationProperties({JdbcProperties.class})
@Configuration
@ConditionalOnClass({DataSource.class, JdbcTemplate.class})
@ConditionalOnSingleCandidate(DataSource.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/JdbcTemplateAutoConfiguration.class */
public class JdbcTemplateAutoConfiguration {

    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/JdbcTemplateAutoConfiguration$JdbcTemplateConfiguration.class */
    static class JdbcTemplateConfiguration {
        private final DataSource dataSource;
        private final JdbcProperties properties;

        JdbcTemplateConfiguration(DataSource dataSource, JdbcProperties properties) {
            this.dataSource = dataSource;
            this.properties = properties;
        }

        @ConditionalOnMissingBean({JdbcOperations.class})
        @Bean
        @Primary
        public JdbcTemplate jdbcTemplate() {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
            JdbcProperties.Template template = this.properties.getTemplate();
            jdbcTemplate.setFetchSize(template.getFetchSize());
            jdbcTemplate.setMaxRows(template.getMaxRows());
            if (template.getQueryTimeout() != null) {
                jdbcTemplate.setQueryTimeout((int) template.getQueryTimeout().getSeconds());
            }
            return jdbcTemplate;
        }
    }

    @Configuration
    @Import({JdbcTemplateConfiguration.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/JdbcTemplateAutoConfiguration$NamedParameterJdbcTemplateConfiguration.class */
    static class NamedParameterJdbcTemplateConfiguration {
        NamedParameterJdbcTemplateConfiguration() {
        }

        @ConditionalOnSingleCandidate(JdbcTemplate.class)
        @ConditionalOnMissingBean({NamedParameterJdbcOperations.class})
        @Bean
        @Primary
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
            return new NamedParameterJdbcTemplate(jdbcTemplate);
        }
    }
}