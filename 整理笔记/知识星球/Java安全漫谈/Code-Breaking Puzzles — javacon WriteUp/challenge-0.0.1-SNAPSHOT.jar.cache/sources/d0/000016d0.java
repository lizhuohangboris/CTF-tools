package org.springframework.boot.autoconfigure.integration;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.boot.jdbc.AbstractDataSourceInitializer;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/integration/IntegrationDataSourceInitializer.class */
public class IntegrationDataSourceInitializer extends AbstractDataSourceInitializer {
    private final IntegrationProperties.Jdbc properties;

    public IntegrationDataSourceInitializer(DataSource dataSource, ResourceLoader resourceLoader, IntegrationProperties properties) {
        super(dataSource, resourceLoader);
        Assert.notNull(properties, "IntegrationProperties must not be null");
        this.properties = properties.getJdbc();
    }

    @Override // org.springframework.boot.jdbc.AbstractDataSourceInitializer
    protected DataSourceInitializationMode getMode() {
        return this.properties.getInitializeSchema();
    }

    @Override // org.springframework.boot.jdbc.AbstractDataSourceInitializer
    protected String getSchemaLocation() {
        return this.properties.getSchema();
    }
}