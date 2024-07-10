package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.SchemaManagementProvider;
import org.springframework.boot.jdbc.metadata.CompositeDataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.orm.jpa.hibernate.SpringJtaPlatform;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.ClassUtils;

@EnableConfigurationProperties({HibernateProperties.class})
@Configuration
@ConditionalOnSingleCandidate(DataSource.class)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration.class */
class HibernateJpaConfiguration extends JpaBaseConfiguration {
    private static final String JTA_PLATFORM = "hibernate.transaction.jta.platform";
    private static final String PROVIDER_DISABLES_AUTOCOMMIT = "hibernate.connection.provider_disables_autocommit";
    private final HibernateProperties hibernateProperties;
    private final HibernateDefaultDdlAutoProvider defaultDdlAutoProvider;
    private DataSourcePoolMetadataProvider poolMetadataProvider;
    private final List<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers;
    private static final Log logger = LogFactory.getLog(HibernateJpaConfiguration.class);
    private static final String[] NO_JTA_PLATFORM_CLASSES = {"org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform", "org.hibernate.service.jta.platform.internal.NoJtaPlatform"};

    HibernateJpaConfiguration(DataSource dataSource, JpaProperties jpaProperties, ConfigurableListableBeanFactory beanFactory, ObjectProvider<JtaTransactionManager> jtaTransactionManager, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers, HibernateProperties hibernateProperties, ObjectProvider<Collection<DataSourcePoolMetadataProvider>> metadataProviders, ObjectProvider<SchemaManagementProvider> providers, ObjectProvider<PhysicalNamingStrategy> physicalNamingStrategy, ObjectProvider<ImplicitNamingStrategy> implicitNamingStrategy, ObjectProvider<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers) {
        super(dataSource, jpaProperties, jtaTransactionManager, transactionManagerCustomizers);
        this.hibernateProperties = hibernateProperties;
        this.defaultDdlAutoProvider = new HibernateDefaultDdlAutoProvider(providers);
        this.poolMetadataProvider = new CompositeDataSourcePoolMetadataProvider(metadataProviders.getIfAvailable());
        this.hibernatePropertiesCustomizers = determineHibernatePropertiesCustomizers(physicalNamingStrategy.getIfAvailable(), implicitNamingStrategy.getIfAvailable(), beanFactory, (List) hibernatePropertiesCustomizers.orderedStream().collect(Collectors.toList()));
    }

    private List<HibernatePropertiesCustomizer> determineHibernatePropertiesCustomizers(PhysicalNamingStrategy physicalNamingStrategy, ImplicitNamingStrategy implicitNamingStrategy, ConfigurableListableBeanFactory beanFactory, List<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers) {
        List<HibernatePropertiesCustomizer> customizers = new ArrayList<>();
        customizers.add(properties -> {
            properties.put("hibernate.resource.beans.container", new SpringBeanContainer(beanFactory));
        });
        if (physicalNamingStrategy != null || implicitNamingStrategy != null) {
            customizers.add(new NamingStrategiesHibernatePropertiesCustomizer(physicalNamingStrategy, implicitNamingStrategy));
        }
        customizers.addAll(hibernatePropertiesCustomizers);
        return customizers;
    }

    @Override // org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Override // org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration
    protected Map<String, Object> getVendorProperties() {
        Supplier<String> defaultDdlMode = () -> {
            return this.defaultDdlAutoProvider.getDefaultDdlAuto(getDataSource());
        };
        return new LinkedHashMap(this.hibernateProperties.determineHibernateProperties(getProperties().getProperties(), new HibernateSettings().ddlAuto(defaultDdlMode).hibernatePropertiesCustomizers(this.hibernatePropertiesCustomizers)));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration
    public void customizeVendorProperties(Map<String, Object> vendorProperties) {
        super.customizeVendorProperties(vendorProperties);
        if (!vendorProperties.containsKey(JTA_PLATFORM)) {
            configureJtaPlatform(vendorProperties);
        }
        if (!vendorProperties.containsKey(PROVIDER_DISABLES_AUTOCOMMIT)) {
            configureProviderDisablesAutocommit(vendorProperties);
        }
    }

    private void configureJtaPlatform(Map<String, Object> vendorProperties) throws LinkageError {
        JtaTransactionManager jtaTransactionManager = getJtaTransactionManager();
        if (jtaTransactionManager == null) {
            vendorProperties.put(JTA_PLATFORM, getNoJtaPlatformManager());
        } else if (!runningOnWebSphere()) {
            configureSpringJtaPlatform(vendorProperties, jtaTransactionManager);
        }
    }

    private void configureProviderDisablesAutocommit(Map<String, Object> vendorProperties) {
        if (isDataSourceAutoCommitDisabled() && !isJta()) {
            vendorProperties.put(PROVIDER_DISABLES_AUTOCOMMIT, "true");
        }
    }

    private boolean isDataSourceAutoCommitDisabled() {
        DataSourcePoolMetadata poolMetadata = this.poolMetadataProvider.getDataSourcePoolMetadata(getDataSource());
        return poolMetadata != null && Boolean.FALSE.equals(poolMetadata.getDefaultAutoCommit());
    }

    private boolean runningOnWebSphere() {
        return ClassUtils.isPresent("com.ibm.websphere.jtaextensions.ExtendedJTATransaction", getClass().getClassLoader());
    }

    private void configureSpringJtaPlatform(Map<String, Object> vendorProperties, JtaTransactionManager jtaTransactionManager) {
        try {
            vendorProperties.put(JTA_PLATFORM, new SpringJtaPlatform(jtaTransactionManager));
        } catch (LinkageError ex) {
            if (!isUsingJndi()) {
                throw new IllegalStateException("Unable to set Hibernate JTA platform, are you using the correct version of Hibernate?", ex);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Unable to set Hibernate JTA platform : " + ex.getMessage());
            }
        }
    }

    private boolean isUsingJndi() {
        try {
            return JndiLocatorDelegate.isDefaultJndiEnvironmentAvailable();
        } catch (Error e) {
            return false;
        }
    }

    private Object getNoJtaPlatformManager() {
        String[] strArr;
        for (String candidate : NO_JTA_PLATFORM_CLASSES) {
            try {
                return Class.forName(candidate).newInstance();
            } catch (Exception e) {
            }
        }
        throw new IllegalStateException("No available JtaPlatform candidates amongst " + Arrays.toString(NO_JTA_PLATFORM_CLASSES));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration$NamingStrategiesHibernatePropertiesCustomizer.class */
    public static class NamingStrategiesHibernatePropertiesCustomizer implements HibernatePropertiesCustomizer {
        private final PhysicalNamingStrategy physicalNamingStrategy;
        private final ImplicitNamingStrategy implicitNamingStrategy;

        NamingStrategiesHibernatePropertiesCustomizer(PhysicalNamingStrategy physicalNamingStrategy, ImplicitNamingStrategy implicitNamingStrategy) {
            this.physicalNamingStrategy = physicalNamingStrategy;
            this.implicitNamingStrategy = implicitNamingStrategy;
        }

        @Override // org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
        public void customize(Map<String, Object> hibernateProperties) {
            if (this.physicalNamingStrategy != null) {
                hibernateProperties.put("hibernate.physical_naming_strategy", this.physicalNamingStrategy);
            }
            if (this.implicitNamingStrategy != null) {
                hibernateProperties.put("hibernate.implicit_naming_strategy", this.implicitNamingStrategy);
            }
        }
    }
}