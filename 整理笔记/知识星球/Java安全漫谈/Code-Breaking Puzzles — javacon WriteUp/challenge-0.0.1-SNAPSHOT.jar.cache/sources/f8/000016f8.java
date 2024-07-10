package org.springframework.boot.autoconfigure.jdbc;

import javax.annotation.PreDestroy;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

@EnableConfigurationProperties({DataSourceProperties.class})
@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/EmbeddedDataSourceConfiguration.class */
public class EmbeddedDataSourceConfiguration implements BeanClassLoaderAware {
    private EmbeddedDatabase database;
    private ClassLoader classLoader;
    private final DataSourceProperties properties;

    public EmbeddedDataSourceConfiguration(DataSourceProperties properties) {
        this.properties = properties;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Bean
    public EmbeddedDatabase dataSource() {
        this.database = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseConnection.get(this.classLoader).getType()).setName(this.properties.determineDatabaseName()).build();
        return this.database;
    }

    @PreDestroy
    public void close() {
        if (this.database != null) {
            this.database.shutdown();
        }
    }
}