package org.springframework.boot.autoconfigure.quartz;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceInitializationMode;

@ConfigurationProperties("spring.quartz")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/quartz/QuartzProperties.class */
public class QuartzProperties {
    private String schedulerName;
    private JobStoreType jobStoreType = JobStoreType.MEMORY;
    private boolean autoStartup = true;
    private Duration startupDelay = Duration.ofSeconds(0);
    private boolean waitForJobsToCompleteOnShutdown = false;
    private boolean overwriteExistingJobs = false;
    private final Map<String, String> properties = new HashMap();
    private final Jdbc jdbc = new Jdbc();

    public JobStoreType getJobStoreType() {
        return this.jobStoreType;
    }

    public void setJobStoreType(JobStoreType jobStoreType) {
        this.jobStoreType = jobStoreType;
    }

    public String getSchedulerName() {
        return this.schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public boolean isAutoStartup() {
        return this.autoStartup;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    public Duration getStartupDelay() {
        return this.startupDelay;
    }

    public void setStartupDelay(Duration startupDelay) {
        this.startupDelay = startupDelay;
    }

    public boolean isWaitForJobsToCompleteOnShutdown() {
        return this.waitForJobsToCompleteOnShutdown;
    }

    public void setWaitForJobsToCompleteOnShutdown(boolean waitForJobsToCompleteOnShutdown) {
        this.waitForJobsToCompleteOnShutdown = waitForJobsToCompleteOnShutdown;
    }

    public boolean isOverwriteExistingJobs() {
        return this.overwriteExistingJobs;
    }

    public void setOverwriteExistingJobs(boolean overwriteExistingJobs) {
        this.overwriteExistingJobs = overwriteExistingJobs;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public Jdbc getJdbc() {
        return this.jdbc;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/quartz/QuartzProperties$Jdbc.class */
    public static class Jdbc {
        private static final String DEFAULT_SCHEMA_LOCATION = "classpath:org/quartz/impl/jdbcjobstore/tables_@@platform@@.sql";
        private String schema = DEFAULT_SCHEMA_LOCATION;
        private DataSourceInitializationMode initializeSchema = DataSourceInitializationMode.EMBEDDED;
        private String commentPrefix = "--";

        public String getSchema() {
            return this.schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        public DataSourceInitializationMode getInitializeSchema() {
            return this.initializeSchema;
        }

        public void setInitializeSchema(DataSourceInitializationMode initializeSchema) {
            this.initializeSchema = initializeSchema;
        }

        public String getCommentPrefix() {
            return this.commentPrefix;
        }

        public void setCommentPrefix(String commentPrefix) {
            this.commentPrefix = commentPrefix;
        }
    }
}