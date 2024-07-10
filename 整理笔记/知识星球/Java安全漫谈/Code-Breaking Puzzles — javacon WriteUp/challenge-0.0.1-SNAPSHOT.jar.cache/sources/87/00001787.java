package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.Map;
import java.util.function.Supplier;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.jdbc.DataSourceSchemaCreatedEvent;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/orm/jpa/DataSourceInitializedPublisher.class */
public class DataSourceInitializedPublisher implements BeanPostProcessor {
    @Autowired
    private ApplicationContext applicationContext;
    private DataSource dataSource;
    private JpaProperties jpaProperties;
    private HibernateProperties hibernateProperties;

    DataSourceInitializedPublisher() {
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof LocalContainerEntityManagerFactoryBean) {
            LocalContainerEntityManagerFactoryBean factory = (LocalContainerEntityManagerFactoryBean) bean;
            factory.setJpaVendorAdapter(new DataSourceSchemaCreatedPublisher(factory));
        }
        return bean;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource) {
            this.dataSource = (DataSource) bean;
        }
        if (bean instanceof JpaProperties) {
            this.jpaProperties = (JpaProperties) bean;
        }
        if (bean instanceof HibernateProperties) {
            this.hibernateProperties = (HibernateProperties) bean;
        }
        if (bean instanceof LocalContainerEntityManagerFactoryBean) {
            LocalContainerEntityManagerFactoryBean factory = (LocalContainerEntityManagerFactoryBean) bean;
            if (factory.getBootstrapExecutor() == null) {
                publishEventIfRequired(factory.getNativeEntityManagerFactory());
            }
        }
        return bean;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void publishEventIfRequired(EntityManagerFactory entityManagerFactory) {
        DataSource dataSource = findDataSource(entityManagerFactory);
        if (dataSource != null && isInitializingDatabase(dataSource)) {
            this.applicationContext.publishEvent((ApplicationEvent) new DataSourceSchemaCreatedEvent(dataSource));
        }
    }

    private DataSource findDataSource(EntityManagerFactory entityManagerFactory) {
        Object dataSource = entityManagerFactory.getProperties().get("javax.persistence.nonJtaDataSource");
        return dataSource instanceof DataSource ? (DataSource) dataSource : this.dataSource;
    }

    private boolean isInitializingDatabase(DataSource dataSource) {
        if (this.jpaProperties == null || this.hibernateProperties == null) {
            return true;
        }
        Supplier<String> defaultDdlAuto = () -> {
            return EmbeddedDatabaseConnection.isEmbedded(dataSource) ? "create-drop" : "none";
        };
        Map<String, Object> hibernate = this.hibernateProperties.determineHibernateProperties(this.jpaProperties.getProperties(), new HibernateSettings().ddlAuto(defaultDdlAuto));
        if (hibernate.containsKey("hibernate.hbm2ddl.auto")) {
            return true;
        }
        return false;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/orm/jpa/DataSourceInitializedPublisher$Registrar.class */
    static class Registrar implements ImportBeanDefinitionRegistrar {
        private static final String BEAN_NAME = "dataSourceInitializedPublisher";

        Registrar() {
        }

        @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            if (!registry.containsBeanDefinition(BEAN_NAME)) {
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClass(DataSourceInitializedPublisher.class);
                beanDefinition.setRole(2);
                beanDefinition.setSynthetic(true);
                registry.registerBeanDefinition(BEAN_NAME, beanDefinition);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/orm/jpa/DataSourceInitializedPublisher$DataSourceSchemaCreatedPublisher.class */
    final class DataSourceSchemaCreatedPublisher implements JpaVendorAdapter {
        private final JpaVendorAdapter delegate;
        private final LocalContainerEntityManagerFactoryBean factory;

        private DataSourceSchemaCreatedPublisher(LocalContainerEntityManagerFactoryBean factory) {
            this.delegate = factory.getJpaVendorAdapter();
            this.factory = factory;
        }

        public PersistenceProvider getPersistenceProvider() {
            return this.delegate.getPersistenceProvider();
        }

        public String getPersistenceProviderRootPackage() {
            return this.delegate.getPersistenceProviderRootPackage();
        }

        public Map<String, ?> getJpaPropertyMap(PersistenceUnitInfo pui) {
            return this.delegate.getJpaPropertyMap(pui);
        }

        public Map<String, ?> getJpaPropertyMap() {
            return this.delegate.getJpaPropertyMap();
        }

        public JpaDialect getJpaDialect() {
            return this.delegate.getJpaDialect();
        }

        public Class<? extends EntityManagerFactory> getEntityManagerFactoryInterface() {
            return this.delegate.getEntityManagerFactoryInterface();
        }

        public Class<? extends EntityManager> getEntityManagerInterface() {
            return this.delegate.getEntityManagerInterface();
        }

        public void postProcessEntityManagerFactory(EntityManagerFactory emf) {
            this.delegate.postProcessEntityManagerFactory(emf);
            AsyncTaskExecutor bootstrapExecutor = this.factory.getBootstrapExecutor();
            if (bootstrapExecutor != null) {
                bootstrapExecutor.execute(() -> {
                    DataSourceInitializedPublisher.this.publishEventIfRequired(emf);
                });
            }
        }
    }
}