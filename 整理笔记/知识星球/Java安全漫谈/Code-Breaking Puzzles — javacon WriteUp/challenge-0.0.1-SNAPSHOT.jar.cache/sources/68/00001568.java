package org.springframework.boot.autoconfigure.batch;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@ConditionalOnMissingBean({BatchConfigurer.class})
@ConditionalOnClass({PlatformTransactionManager.class})
@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/batch/BatchConfigurerConfiguration.class */
class BatchConfigurerConfiguration {
    BatchConfigurerConfiguration() {
    }

    @ConditionalOnMissingBean(name = {"entityManagerFactory"})
    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/batch/BatchConfigurerConfiguration$JdbcBatchConfiguration.class */
    static class JdbcBatchConfiguration {
        JdbcBatchConfiguration() {
        }

        @Bean
        public BasicBatchConfigurer batchConfigurer(BatchProperties properties, DataSource dataSource, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
            return new BasicBatchConfigurer(properties, dataSource, transactionManagerCustomizers.getIfAvailable());
        }
    }

    @Configuration
    @ConditionalOnClass(name = {"javax.persistence.EntityManagerFactory"})
    @ConditionalOnBean(name = {"entityManagerFactory"})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/batch/BatchConfigurerConfiguration$JpaBatchConfiguration.class */
    static class JpaBatchConfiguration {
        JpaBatchConfiguration() {
        }

        @Bean
        public JpaBatchConfigurer batchConfigurer(BatchProperties properties, DataSource dataSource, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers, EntityManagerFactory entityManagerFactory) {
            return new JpaBatchConfigurer(properties, dataSource, transactionManagerCustomizers.getIfAvailable(), entityManagerFactory);
        }
    }
}