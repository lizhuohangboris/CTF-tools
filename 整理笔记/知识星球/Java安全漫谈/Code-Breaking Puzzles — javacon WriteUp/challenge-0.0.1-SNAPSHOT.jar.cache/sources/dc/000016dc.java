package org.springframework.boot.autoconfigure.jdbc;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.jdbc.DataSourceConfiguration;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@EnableConfigurationProperties({DataSourceProperties.class})
@Configuration
@ConditionalOnClass({DataSource.class, EmbeddedDatabaseType.class})
@Import({DataSourcePoolMetadataProvidersConfiguration.class, DataSourceInitializationConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceAutoConfiguration.class */
public class DataSourceAutoConfiguration {

    @Configuration
    @ConditionalOnMissingBean({DataSource.class, XADataSource.class})
    @Conditional({EmbeddedDatabaseCondition.class})
    @Import({EmbeddedDataSourceConfiguration.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceAutoConfiguration$EmbeddedDatabaseConfiguration.class */
    protected static class EmbeddedDatabaseConfiguration {
        protected EmbeddedDatabaseConfiguration() {
        }
    }

    @Configuration
    @ConditionalOnMissingBean({DataSource.class, XADataSource.class})
    @Conditional({PooledDataSourceCondition.class})
    @Import({DataSourceConfiguration.Hikari.class, DataSourceConfiguration.Tomcat.class, DataSourceConfiguration.Dbcp2.class, DataSourceConfiguration.Generic.class, DataSourceJmxConfiguration.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceAutoConfiguration$PooledDataSourceConfiguration.class */
    protected static class PooledDataSourceConfiguration {
        protected PooledDataSourceConfiguration() {
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceAutoConfiguration$PooledDataSourceCondition.class */
    static class PooledDataSourceCondition extends AnyNestedCondition {
        PooledDataSourceCondition() {
            super(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnProperty(prefix = "spring.datasource", name = {"type"})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceAutoConfiguration$PooledDataSourceCondition$ExplicitType.class */
        static class ExplicitType {
            ExplicitType() {
            }
        }

        @Conditional({PooledDataSourceAvailableCondition.class})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceAutoConfiguration$PooledDataSourceCondition$PooledDataSourceAvailable.class */
        static class PooledDataSourceAvailable {
            PooledDataSourceAvailable() {
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceAutoConfiguration$PooledDataSourceAvailableCondition.class */
    static class PooledDataSourceAvailableCondition extends SpringBootCondition {
        PooledDataSourceAvailableCondition() {
        }

        @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            ConditionMessage.Builder message = ConditionMessage.forCondition("PooledDataSource", new Object[0]);
            if (getDataSourceClassLoader(context) != null) {
                return ConditionOutcome.match(message.foundExactly("supported DataSource"));
            }
            return ConditionOutcome.noMatch(message.didNotFind("supported DataSource").atAll());
        }

        private ClassLoader getDataSourceClassLoader(ConditionContext context) {
            Class<?> dataSourceClass = DataSourceBuilder.findType(context.getClassLoader());
            if (dataSourceClass != null) {
                return dataSourceClass.getClassLoader();
            }
            return null;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceAutoConfiguration$EmbeddedDatabaseCondition.class */
    static class EmbeddedDatabaseCondition extends SpringBootCondition {
        private final SpringBootCondition pooledCondition = new PooledDataSourceCondition();

        EmbeddedDatabaseCondition() {
        }

        @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            ConditionMessage.Builder message = ConditionMessage.forCondition("EmbeddedDataSource", new Object[0]);
            if (anyMatches(context, metadata, this.pooledCondition)) {
                return ConditionOutcome.noMatch(message.foundExactly("supported pooled data source"));
            }
            EmbeddedDatabaseType type = EmbeddedDatabaseConnection.get(context.getClassLoader()).getType();
            return type == null ? ConditionOutcome.noMatch(message.didNotFind("embedded database").atAll()) : ConditionOutcome.match(message.found("embedded database").items(type));
        }
    }
}