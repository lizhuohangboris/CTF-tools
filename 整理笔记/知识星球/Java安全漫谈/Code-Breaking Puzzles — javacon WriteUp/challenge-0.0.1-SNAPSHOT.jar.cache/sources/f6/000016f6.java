package org.springframework.boot.autoconfigure.jdbc;

import javax.sql.DataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@EnableConfigurationProperties({DataSourceProperties.class})
@Configuration
@ConditionalOnClass({JdbcTemplate.class, PlatformTransactionManager.class})
@AutoConfigureOrder(Integer.MAX_VALUE)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceTransactionManagerAutoConfiguration.class */
public class DataSourceTransactionManagerAutoConfiguration {

    @Configuration
    @ConditionalOnSingleCandidate(DataSource.class)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceTransactionManagerAutoConfiguration$DataSourceTransactionManagerConfiguration.class */
    static class DataSourceTransactionManagerConfiguration {
        private final DataSource dataSource;
        private final TransactionManagerCustomizers transactionManagerCustomizers;

        DataSourceTransactionManagerConfiguration(DataSource dataSource, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
            this.dataSource = dataSource;
            this.transactionManagerCustomizers = transactionManagerCustomizers.getIfAvailable();
        }

        @ConditionalOnMissingBean({PlatformTransactionManager.class})
        @Bean
        public DataSourceTransactionManager transactionManager(DataSourceProperties properties) {
            PlatformTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(this.dataSource);
            if (this.transactionManagerCustomizers != null) {
                this.transactionManagerCustomizers.customize(dataSourceTransactionManager);
            }
            return dataSourceTransactionManager;
        }
    }
}