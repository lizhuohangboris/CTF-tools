package org.springframework.boot.autoconfigure.quartz;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.AbstractDataSourceInitializer;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/quartz/QuartzDataSourceInitializer.class */
public class QuartzDataSourceInitializer extends AbstractDataSourceInitializer {
    private final QuartzProperties properties;

    public QuartzDataSourceInitializer(DataSource dataSource, ResourceLoader resourceLoader, QuartzProperties properties) {
        super(dataSource, resourceLoader);
        Assert.notNull(properties, "QuartzProperties must not be null");
        this.properties = properties;
    }

    @Override // org.springframework.boot.jdbc.AbstractDataSourceInitializer
    protected void customize(ResourceDatabasePopulator populator) {
        populator.setCommentPrefix(this.properties.getJdbc().getCommentPrefix());
    }

    @Override // org.springframework.boot.jdbc.AbstractDataSourceInitializer
    protected DataSourceInitializationMode getMode() {
        return this.properties.getJdbc().getInitializeSchema();
    }

    @Override // org.springframework.boot.jdbc.AbstractDataSourceInitializer
    protected String getSchemaLocation() {
        return this.properties.getJdbc().getSchema();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.jdbc.AbstractDataSourceInitializer
    public String getDatabaseName() {
        String databaseName = super.getDatabaseName();
        if ("db2".equals(databaseName)) {
            return "db2_v95";
        }
        if ("mysql".equals(databaseName)) {
            return "mysql_innodb";
        }
        if ("postgresql".equals(databaseName)) {
            return "postgres";
        }
        if ("sqlserver".equals(databaseName)) {
            return "sqlServer";
        }
        return databaseName;
    }
}