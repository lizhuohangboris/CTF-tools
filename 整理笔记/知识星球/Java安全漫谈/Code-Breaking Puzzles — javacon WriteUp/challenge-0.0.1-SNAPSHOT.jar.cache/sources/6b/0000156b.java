package org.springframework.boot.autoconfigure.batch;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.AbstractDataSourceInitializer;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/batch/BatchDataSourceInitializer.class */
public class BatchDataSourceInitializer extends AbstractDataSourceInitializer {
    private final BatchProperties properties;

    public BatchDataSourceInitializer(DataSource dataSource, ResourceLoader resourceLoader, BatchProperties properties) {
        super(dataSource, resourceLoader);
        Assert.notNull(properties, "BatchProperties must not be null");
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.jdbc.AbstractDataSourceInitializer
    public String getDatabaseName() {
        String databaseName = super.getDatabaseName();
        if ("oracle".equals(databaseName)) {
            return "oracle10g";
        }
        return databaseName;
    }
}