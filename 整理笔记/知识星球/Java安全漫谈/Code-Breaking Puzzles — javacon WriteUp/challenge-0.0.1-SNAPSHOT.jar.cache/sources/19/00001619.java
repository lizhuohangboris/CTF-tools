package org.springframework.boot.autoconfigure.data.jpa;

import javax.sql.DataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilderCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

@AutoConfigureAfter({HibernateJpaAutoConfiguration.class, TaskExecutionAutoConfiguration.class})
@ConditionalOnMissingBean({JpaRepositoryFactoryBean.class, JpaRepositoryConfigExtension.class})
@ConditionalOnProperty(prefix = "spring.data.jpa.repositories", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
@Import({JpaRepositoriesAutoConfigureRegistrar.class})
@Configuration
@ConditionalOnClass({JpaRepository.class})
@ConditionalOnBean({DataSource.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/jpa/JpaRepositoriesAutoConfiguration.class */
public class JpaRepositoriesAutoConfiguration {
    @Conditional({BootstrapExecutorCondition.class})
    @Bean
    public EntityManagerFactoryBuilderCustomizer entityManagerFactoryBootstrapExecutorCustomizer(ObjectProvider<AsyncTaskExecutor> taskExecutor) {
        return builder -> {
            builder.setBootstrapExecutor((AsyncTaskExecutor) taskExecutor.getIfAvailable());
        };
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/jpa/JpaRepositoriesAutoConfiguration$BootstrapExecutorCondition.class */
    private static final class BootstrapExecutorCondition extends AnyNestedCondition {
        BootstrapExecutorCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "spring.data.jpa.repositories", name = {"bootstrap-mode"}, havingValue = "deferred", matchIfMissing = false)
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/jpa/JpaRepositoriesAutoConfiguration$BootstrapExecutorCondition$DeferredBootstrapMode.class */
        static class DeferredBootstrapMode {
            DeferredBootstrapMode() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.data.jpa.repositories", name = {"bootstrap-mode"}, havingValue = "lazy", matchIfMissing = false)
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/jpa/JpaRepositoriesAutoConfiguration$BootstrapExecutorCondition$LazyBootstrapMode.class */
        static class LazyBootstrapMode {
            LazyBootstrapMode() {
            }
        }
    }
}