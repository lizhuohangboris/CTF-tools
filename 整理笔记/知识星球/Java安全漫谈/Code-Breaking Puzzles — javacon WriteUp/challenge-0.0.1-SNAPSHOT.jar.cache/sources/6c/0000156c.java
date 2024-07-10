package org.springframework.boot.autoconfigure.batch;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceInitializationMode;

@ConfigurationProperties(prefix = "spring.batch")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/batch/BatchProperties.class */
public class BatchProperties {
    private static final String DEFAULT_SCHEMA_LOCATION = "classpath:org/springframework/batch/core/schema-@@platform@@.sql";
    private String tablePrefix;
    private String schema = DEFAULT_SCHEMA_LOCATION;
    private DataSourceInitializationMode initializeSchema = DataSourceInitializationMode.EMBEDDED;
    private final Job job = new Job();

    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTablePrefix() {
        return this.tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public DataSourceInitializationMode getInitializeSchema() {
        return this.initializeSchema;
    }

    public void setInitializeSchema(DataSourceInitializationMode initializeSchema) {
        this.initializeSchema = initializeSchema;
    }

    public Job getJob() {
        return this.job;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/batch/BatchProperties$Job.class */
    public static class Job {
        private String names = "";

        public String getNames() {
            return this.names;
        }

        public void setNames(String names) {
            this.names = names;
        }
    }
}