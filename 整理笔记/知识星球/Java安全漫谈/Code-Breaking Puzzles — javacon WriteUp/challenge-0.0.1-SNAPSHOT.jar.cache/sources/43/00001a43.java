package org.springframework.boot.orm.jpa;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/orm/jpa/EntityManagerFactoryBuilder.class */
public class EntityManagerFactoryBuilder {
    private final JpaVendorAdapter jpaVendorAdapter;
    private final PersistenceUnitManager persistenceUnitManager;
    private final Map<String, Object> jpaProperties;
    private final URL persistenceUnitRootLocation;
    private AsyncTaskExecutor bootstrapExecutor;

    public EntityManagerFactoryBuilder(JpaVendorAdapter jpaVendorAdapter, Map<String, ?> jpaProperties, PersistenceUnitManager persistenceUnitManager) {
        this(jpaVendorAdapter, jpaProperties, persistenceUnitManager, null);
    }

    public EntityManagerFactoryBuilder(JpaVendorAdapter jpaVendorAdapter, Map<String, ?> jpaProperties, PersistenceUnitManager persistenceUnitManager, URL persistenceUnitRootLocation) {
        this.jpaVendorAdapter = jpaVendorAdapter;
        this.persistenceUnitManager = persistenceUnitManager;
        this.jpaProperties = new LinkedHashMap(jpaProperties);
        this.persistenceUnitRootLocation = persistenceUnitRootLocation;
    }

    public Builder dataSource(DataSource dataSource) {
        return new Builder(dataSource);
    }

    public void setBootstrapExecutor(AsyncTaskExecutor bootstrapExecutor) {
        this.bootstrapExecutor = bootstrapExecutor;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/orm/jpa/EntityManagerFactoryBuilder$Builder.class */
    public final class Builder {
        private DataSource dataSource;
        private String[] packagesToScan;
        private String persistenceUnit;
        private Map<String, Object> properties;
        private String[] mappingResources;
        private boolean jta;

        private Builder(DataSource dataSource) {
            this.properties = new HashMap();
            this.dataSource = dataSource;
        }

        public Builder packages(String... packagesToScan) {
            this.packagesToScan = packagesToScan;
            return this;
        }

        public Builder packages(Class<?>... basePackageClasses) {
            Set<String> packages = new HashSet<>();
            for (Class<?> type : basePackageClasses) {
                packages.add(ClassUtils.getPackageName(type));
            }
            this.packagesToScan = StringUtils.toStringArray(packages);
            return this;
        }

        public Builder persistenceUnit(String persistenceUnit) {
            this.persistenceUnit = persistenceUnit;
            return this;
        }

        public Builder properties(Map<String, ?> properties) {
            this.properties.putAll(properties);
            return this;
        }

        public Builder mappingResources(String... mappingResources) {
            this.mappingResources = mappingResources;
            return this;
        }

        public Builder jta(boolean jta) {
            this.jta = jta;
            return this;
        }

        public LocalContainerEntityManagerFactoryBean build() {
            LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
            if (EntityManagerFactoryBuilder.this.persistenceUnitManager != null) {
                entityManagerFactoryBean.setPersistenceUnitManager(EntityManagerFactoryBuilder.this.persistenceUnitManager);
            }
            if (this.persistenceUnit != null) {
                entityManagerFactoryBean.setPersistenceUnitName(this.persistenceUnit);
            }
            entityManagerFactoryBean.setJpaVendorAdapter(EntityManagerFactoryBuilder.this.jpaVendorAdapter);
            if (this.jta) {
                entityManagerFactoryBean.setJtaDataSource(this.dataSource);
            } else {
                entityManagerFactoryBean.setDataSource(this.dataSource);
            }
            entityManagerFactoryBean.setPackagesToScan(this.packagesToScan);
            entityManagerFactoryBean.getJpaPropertyMap().putAll(EntityManagerFactoryBuilder.this.jpaProperties);
            entityManagerFactoryBean.getJpaPropertyMap().putAll(this.properties);
            if (!ObjectUtils.isEmpty((Object[]) this.mappingResources)) {
                entityManagerFactoryBean.setMappingResources(this.mappingResources);
            }
            URL rootLocation = EntityManagerFactoryBuilder.this.persistenceUnitRootLocation;
            if (rootLocation != null) {
                entityManagerFactoryBean.setPersistenceUnitRootLocation(rootLocation.toString());
            }
            if (EntityManagerFactoryBuilder.this.bootstrapExecutor != null) {
                entityManagerFactoryBean.setBootstrapExecutor(EntityManagerFactoryBuilder.this.bootstrapExecutor);
            }
            return entityManagerFactoryBean;
        }
    }
}