package org.springframework.boot.autoconfigure.integration;

import javax.management.MBeanServer;
import javax.sql.DataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.config.EnableIntegrationManagement;
import org.springframework.integration.config.IntegrationManagementConfigurer;
import org.springframework.integration.gateway.GatewayProxyFactoryBean;
import org.springframework.integration.jdbc.store.JdbcMessageStore;
import org.springframework.integration.jmx.config.EnableIntegrationMBeanExport;
import org.springframework.integration.monitor.IntegrationMBeanExporter;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({IntegrationProperties.class})
@Configuration
@ConditionalOnClass({EnableIntegration.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class, JmxAutoConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration.class */
public class IntegrationAutoConfiguration {

    @Configuration
    @EnableIntegration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationConfiguration.class */
    protected static class IntegrationConfiguration {
        protected IntegrationConfiguration() {
        }
    }

    @Configuration
    @ConditionalOnClass({EnableIntegrationMBeanExport.class})
    @ConditionalOnMissingBean(value = {IntegrationMBeanExporter.class}, search = SearchStrategy.CURRENT)
    @ConditionalOnBean({MBeanServer.class})
    @ConditionalOnProperty(prefix = "spring.jmx", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationJmxConfiguration.class */
    protected static class IntegrationJmxConfiguration implements EnvironmentAware, BeanFactoryAware {
        private BeanFactory beanFactory;
        private Environment environment;

        protected IntegrationJmxConfiguration() {
        }

        @Override // org.springframework.beans.factory.BeanFactoryAware
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        @Override // org.springframework.context.EnvironmentAware
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Bean
        public IntegrationMBeanExporter integrationMbeanExporter() {
            IntegrationMBeanExporter exporter = new IntegrationMBeanExporter();
            String defaultDomain = this.environment.getProperty("spring.jmx.default-domain");
            if (StringUtils.hasLength(defaultDomain)) {
                exporter.setDefaultDomain(defaultDomain);
            }
            String serverBean = this.environment.getProperty("spring.jmx.server", "mbeanServer");
            exporter.setServer((MBeanServer) this.beanFactory.getBean(serverBean, MBeanServer.class));
            return exporter;
        }
    }

    @ConditionalOnMissingBean(value = {IntegrationManagementConfigurer.class}, name = {"integrationManagementConfigurer"}, search = SearchStrategy.CURRENT)
    @Configuration
    @ConditionalOnClass({EnableIntegrationManagement.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationManagementConfiguration.class */
    protected static class IntegrationManagementConfiguration {
        protected IntegrationManagementConfiguration() {
        }

        @EnableIntegrationManagement(defaultCountsEnabled = "true")
        @Configuration
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationManagementConfiguration$EnableIntegrationManagementConfiguration.class */
        protected static class EnableIntegrationManagementConfiguration {
            protected EnableIntegrationManagementConfiguration() {
            }
        }
    }

    @ConditionalOnMissingBean({GatewayProxyFactoryBean.class})
    @Configuration
    @Import({IntegrationAutoConfigurationScanRegistrar.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationComponentScanConfiguration.class */
    protected static class IntegrationComponentScanConfiguration {
        protected IntegrationComponentScanConfiguration() {
        }
    }

    @Configuration
    @ConditionalOnClass({JdbcMessageStore.class})
    @ConditionalOnSingleCandidate(DataSource.class)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationJdbcConfiguration.class */
    protected static class IntegrationJdbcConfiguration {
        protected IntegrationJdbcConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        public IntegrationDataSourceInitializer integrationDataSourceInitializer(DataSource dataSource, ResourceLoader resourceLoader, IntegrationProperties properties) {
            return new IntegrationDataSourceInitializer(dataSource, resourceLoader, properties);
        }
    }
}