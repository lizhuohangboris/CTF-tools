package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.autoconfigure.orm.jpa.DataSourceInitializedPublisher;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableConfigurationProperties({JpaProperties.class})
@Configuration
@Import({DataSourceInitializedPublisher.Registrar.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/orm/jpa/JpaBaseConfiguration.class */
public abstract class JpaBaseConfiguration implements BeanFactoryAware {
    private final DataSource dataSource;
    private final JpaProperties properties;
    private final JtaTransactionManager jtaTransactionManager;
    private final TransactionManagerCustomizers transactionManagerCustomizers;
    private ConfigurableListableBeanFactory beanFactory;

    protected abstract AbstractJpaVendorAdapter createJpaVendorAdapter();

    protected abstract Map<String, Object> getVendorProperties();

    /* JADX INFO: Access modifiers changed from: protected */
    public JpaBaseConfiguration(DataSource dataSource, JpaProperties properties, ObjectProvider<JtaTransactionManager> jtaTransactionManager, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        this.dataSource = dataSource;
        this.properties = properties;
        this.jtaTransactionManager = jtaTransactionManager.getIfAvailable();
        this.transactionManagerCustomizers = transactionManagerCustomizers.getIfAvailable();
    }

    @ConditionalOnMissingBean
    @Bean
    public PlatformTransactionManager transactionManager() {
        PlatformTransactionManager jpaTransactionManager = new JpaTransactionManager();
        if (this.transactionManagerCustomizers != null) {
            this.transactionManagerCustomizers.customize(jpaTransactionManager);
        }
        return jpaTransactionManager;
    }

    @ConditionalOnMissingBean
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        AbstractJpaVendorAdapter adapter = createJpaVendorAdapter();
        adapter.setShowSql(this.properties.isShowSql());
        adapter.setDatabase(this.properties.determineDatabase(this.dataSource));
        adapter.setDatabasePlatform(this.properties.getDatabasePlatform());
        adapter.setGenerateDdl(this.properties.isGenerateDdl());
        return adapter;
    }

    @ConditionalOnMissingBean
    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaVendorAdapter jpaVendorAdapter, ObjectProvider<PersistenceUnitManager> persistenceUnitManager, ObjectProvider<EntityManagerFactoryBuilderCustomizer> customizers) {
        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(jpaVendorAdapter, this.properties.getProperties(), persistenceUnitManager.getIfAvailable());
        customizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder;
    }

    @ConditionalOnMissingBean({LocalContainerEntityManagerFactoryBean.class, EntityManagerFactory.class})
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder factoryBuilder) {
        Map<String, Object> vendorProperties = getVendorProperties();
        customizeVendorProperties(vendorProperties);
        return factoryBuilder.dataSource(this.dataSource).packages(getPackagesToScan()).properties(vendorProperties).mappingResources(getMappingResources()).jta(isJta()).build();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void customizeVendorProperties(Map<String, Object> vendorProperties) {
    }

    protected String[] getPackagesToScan() {
        List<String> packages = EntityScanPackages.get(this.beanFactory).getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(this.beanFactory)) {
            packages = AutoConfigurationPackages.get(this.beanFactory);
        }
        return StringUtils.toStringArray(packages);
    }

    private String[] getMappingResources() {
        List<String> mappingResources = this.properties.getMappingResources();
        if (ObjectUtils.isEmpty(mappingResources)) {
            return null;
        }
        return StringUtils.toStringArray(mappingResources);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JtaTransactionManager getJtaTransactionManager() {
        return this.jtaTransactionManager;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isJta() {
        return this.jtaTransactionManager != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final JpaProperties getProperties() {
        return this.properties;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final DataSource getDataSource() {
        return this.dataSource;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Configuration
    @ConditionalOnClass({WebMvcConfigurer.class})
    @ConditionalOnMissingBean({OpenEntityManagerInViewInterceptor.class, OpenEntityManagerInViewFilter.class})
    @ConditionalOnProperty(prefix = "spring.jpa", name = {"open-in-view"}, havingValue = "true", matchIfMissing = true)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/orm/jpa/JpaBaseConfiguration$JpaWebConfiguration.class */
    protected static class JpaWebConfiguration {
        protected JpaWebConfiguration() {
        }

        @Configuration
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/orm/jpa/JpaBaseConfiguration$JpaWebConfiguration$JpaWebMvcConfiguration.class */
        protected static class JpaWebMvcConfiguration implements WebMvcConfigurer {
            private static final Log logger = LogFactory.getLog(JpaWebMvcConfiguration.class);
            private final JpaProperties jpaProperties;

            protected JpaWebMvcConfiguration(JpaProperties jpaProperties) {
                this.jpaProperties = jpaProperties;
            }

            @Bean
            public OpenEntityManagerInViewInterceptor openEntityManagerInViewInterceptor() {
                if (this.jpaProperties.getOpenInView() == null) {
                    logger.warn("spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning");
                }
                return new OpenEntityManagerInViewInterceptor();
            }

            @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addWebRequestInterceptor(openEntityManagerInViewInterceptor());
            }
        }
    }
}