package org.springframework.boot.jdbc;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jdbc/AbstractDataSourceInitializer.class */
public abstract class AbstractDataSourceInitializer {
    private static final String PLATFORM_PLACEHOLDER = "@@platform@@";
    private final DataSource dataSource;
    private final ResourceLoader resourceLoader;

    protected abstract DataSourceInitializationMode getMode();

    protected abstract String getSchemaLocation();

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractDataSourceInitializer(DataSource dataSource, ResourceLoader resourceLoader) {
        Assert.notNull(dataSource, "DataSource must not be null");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.dataSource = dataSource;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    protected void initialize() {
        if (!isEnabled()) {
            return;
        }
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        String schemaLocation = getSchemaLocation();
        if (schemaLocation.contains(PLATFORM_PLACEHOLDER)) {
            String platform = getDatabaseName();
            schemaLocation = schemaLocation.replace(PLATFORM_PLACEHOLDER, platform);
        }
        populator.addScript(this.resourceLoader.getResource(schemaLocation));
        populator.setContinueOnError(true);
        customize(populator);
        DatabasePopulatorUtils.execute(populator, this.dataSource);
    }

    private boolean isEnabled() {
        if (getMode() == DataSourceInitializationMode.NEVER) {
            return false;
        }
        if (getMode() == DataSourceInitializationMode.EMBEDDED && !EmbeddedDatabaseConnection.isEmbedded(this.dataSource)) {
            return false;
        }
        return true;
    }

    protected void customize(ResourceDatabasePopulator populator) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getDatabaseName() {
        try {
            String productName = JdbcUtils.commonDatabaseName(JdbcUtils.extractDatabaseMetaData(this.dataSource, "getDatabaseProductName").toString());
            DatabaseDriver databaseDriver = DatabaseDriver.fromProductName(productName);
            if (databaseDriver == DatabaseDriver.UNKNOWN) {
                throw new IllegalStateException("Unable to detect database type");
            }
            return databaseDriver.getId();
        } catch (MetaDataAccessException ex) {
            throw new IllegalStateException("Unable to detect database type", ex);
        }
    }
}