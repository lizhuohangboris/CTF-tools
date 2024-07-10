package org.springframework.boot.autoconfigure.session;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceInitializationMode;

@ConfigurationProperties(prefix = "spring.session.jdbc")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/JdbcSessionProperties.class */
public class JdbcSessionProperties {
    private static final String DEFAULT_SCHEMA_LOCATION = "classpath:org/springframework/session/jdbc/schema-@@platform@@.sql";
    private static final String DEFAULT_TABLE_NAME = "SPRING_SESSION";
    private static final String DEFAULT_CLEANUP_CRON = "0 * * * * *";
    private String schema = DEFAULT_SCHEMA_LOCATION;
    private String tableName = DEFAULT_TABLE_NAME;
    private String cleanupCron = DEFAULT_CLEANUP_CRON;
    private DataSourceInitializationMode initializeSchema = DataSourceInitializationMode.EMBEDDED;

    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getCleanupCron() {
        return this.cleanupCron;
    }

    public void setCleanupCron(String cleanupCron) {
        this.cleanupCron = cleanupCron;
    }

    public DataSourceInitializationMode getInitializeSchema() {
        return this.initializeSchema;
    }

    public void setInitializeSchema(DataSourceInitializationMode initializeSchema) {
        this.initializeSchema = initializeSchema;
    }
}