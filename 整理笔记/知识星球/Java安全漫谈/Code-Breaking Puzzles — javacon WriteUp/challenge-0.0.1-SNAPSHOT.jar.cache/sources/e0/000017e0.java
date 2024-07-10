package org.springframework.boot.autoconfigure.session;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.AbstractDataSourceInitializer;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/JdbcSessionDataSourceInitializer.class */
public class JdbcSessionDataSourceInitializer extends AbstractDataSourceInitializer {
    private final JdbcSessionProperties properties;

    public JdbcSessionDataSourceInitializer(DataSource dataSource, ResourceLoader resourceLoader, JdbcSessionProperties properties) {
        super(dataSource, resourceLoader);
        Assert.notNull(properties, "JdbcSessionProperties must not be null");
        this.properties = properties;
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