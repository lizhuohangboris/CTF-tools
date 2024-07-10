package org.springframework.boot.jta.atomikos;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.time.Duration;
import java.util.Properties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.jta.atomikos.properties")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jta/atomikos/AtomikosProperties.class */
public class AtomikosProperties {
    private String service;
    private String transactionManagerUniqueName;
    private boolean forceShutdownOnVmExit;
    private String logBaseDir;
    private boolean threadedTwoPhaseCommit;
    private Duration maxTimeout = Duration.ofMillis(300000);
    private Duration defaultJtaTimeout = Duration.ofMillis(AbstractComponentTracker.LINGERING_TIMEOUT);
    private int maxActives = 50;
    private boolean enableLogging = true;
    private boolean serialJtaTransactions = true;
    private boolean allowSubTransactions = true;
    private long defaultMaxWaitTimeOnShutdown = Long.MAX_VALUE;
    private String logBaseName = "tmlog";
    private long checkpointInterval = 500;
    private final Recovery recovery = new Recovery();

    public void setService(String service) {
        this.service = service;
    }

    public String getService() {
        return this.service;
    }

    public void setMaxTimeout(Duration maxTimeout) {
        this.maxTimeout = maxTimeout;
    }

    public Duration getMaxTimeout() {
        return this.maxTimeout;
    }

    public void setDefaultJtaTimeout(Duration defaultJtaTimeout) {
        this.defaultJtaTimeout = defaultJtaTimeout;
    }

    public Duration getDefaultJtaTimeout() {
        return this.defaultJtaTimeout;
    }

    public void setMaxActives(int maxActives) {
        this.maxActives = maxActives;
    }

    public int getMaxActives() {
        return this.maxActives;
    }

    public void setEnableLogging(boolean enableLogging) {
        this.enableLogging = enableLogging;
    }

    public boolean isEnableLogging() {
        return this.enableLogging;
    }

    public void setTransactionManagerUniqueName(String uniqueName) {
        this.transactionManagerUniqueName = uniqueName;
    }

    public String getTransactionManagerUniqueName() {
        return this.transactionManagerUniqueName;
    }

    public void setSerialJtaTransactions(boolean serialJtaTransactions) {
        this.serialJtaTransactions = serialJtaTransactions;
    }

    public boolean isSerialJtaTransactions() {
        return this.serialJtaTransactions;
    }

    public void setAllowSubTransactions(boolean allowSubTransactions) {
        this.allowSubTransactions = allowSubTransactions;
    }

    public boolean isAllowSubTransactions() {
        return this.allowSubTransactions;
    }

    public void setForceShutdownOnVmExit(boolean forceShutdownOnVmExit) {
        this.forceShutdownOnVmExit = forceShutdownOnVmExit;
    }

    public boolean isForceShutdownOnVmExit() {
        return this.forceShutdownOnVmExit;
    }

    public void setDefaultMaxWaitTimeOnShutdown(long defaultMaxWaitTimeOnShutdown) {
        this.defaultMaxWaitTimeOnShutdown = defaultMaxWaitTimeOnShutdown;
    }

    public long getDefaultMaxWaitTimeOnShutdown() {
        return this.defaultMaxWaitTimeOnShutdown;
    }

    public void setLogBaseName(String logBaseName) {
        this.logBaseName = logBaseName;
    }

    public String getLogBaseName() {
        return this.logBaseName;
    }

    public void setLogBaseDir(String logBaseDir) {
        this.logBaseDir = logBaseDir;
    }

    public String getLogBaseDir() {
        return this.logBaseDir;
    }

    public void setCheckpointInterval(long checkpointInterval) {
        this.checkpointInterval = checkpointInterval;
    }

    public long getCheckpointInterval() {
        return this.checkpointInterval;
    }

    public void setThreadedTwoPhaseCommit(boolean threadedTwoPhaseCommit) {
        this.threadedTwoPhaseCommit = threadedTwoPhaseCommit;
    }

    public boolean isThreadedTwoPhaseCommit() {
        return this.threadedTwoPhaseCommit;
    }

    public Recovery getRecovery() {
        return this.recovery;
    }

    public Properties asProperties() {
        Properties properties = new Properties();
        set(properties, "service", getService());
        set(properties, "max_timeout", getMaxTimeout());
        set(properties, "default_jta_timeout", getDefaultJtaTimeout());
        set(properties, "max_actives", Integer.valueOf(getMaxActives()));
        set(properties, "enable_logging", Boolean.valueOf(isEnableLogging()));
        set(properties, "tm_unique_name", getTransactionManagerUniqueName());
        set(properties, "serial_jta_transactions", Boolean.valueOf(isSerialJtaTransactions()));
        set(properties, "allow_subtransactions", Boolean.valueOf(isAllowSubTransactions()));
        set(properties, "force_shutdown_on_vm_exit", Boolean.valueOf(isForceShutdownOnVmExit()));
        set(properties, "default_max_wait_time_on_shutdown", Long.valueOf(getDefaultMaxWaitTimeOnShutdown()));
        set(properties, "log_base_name", getLogBaseName());
        set(properties, "log_base_dir", getLogBaseDir());
        set(properties, "checkpoint_interval", Long.valueOf(getCheckpointInterval()));
        set(properties, "threaded_2pc", Boolean.valueOf(isThreadedTwoPhaseCommit()));
        Recovery recovery = getRecovery();
        set(properties, "forget_orphaned_log_entries_delay", recovery.getForgetOrphanedLogEntriesDelay());
        set(properties, "recovery_delay", recovery.getDelay());
        set(properties, "oltp_max_retries", Integer.valueOf(recovery.getMaxRetries()));
        set(properties, "oltp_retry_interval", recovery.getRetryInterval());
        return properties;
    }

    private void set(Properties properties, String key, Object value) {
        String id = "com.atomikos.icatch." + key;
        if (value != null && !properties.containsKey(id)) {
            properties.setProperty(id, asString(value));
        }
    }

    private String asString(Object value) {
        if (value instanceof Duration) {
            return String.valueOf(((Duration) value).toMillis());
        }
        return value.toString();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jta/atomikos/AtomikosProperties$Recovery.class */
    public static class Recovery {
        private Duration forgetOrphanedLogEntriesDelay = Duration.ofMillis(CoreConstants.MILLIS_IN_ONE_DAY);
        private Duration delay = Duration.ofMillis(AbstractComponentTracker.LINGERING_TIMEOUT);
        private int maxRetries = 5;
        private Duration retryInterval = Duration.ofMillis(AbstractComponentTracker.LINGERING_TIMEOUT);

        public Duration getForgetOrphanedLogEntriesDelay() {
            return this.forgetOrphanedLogEntriesDelay;
        }

        public void setForgetOrphanedLogEntriesDelay(Duration forgetOrphanedLogEntriesDelay) {
            this.forgetOrphanedLogEntriesDelay = forgetOrphanedLogEntriesDelay;
        }

        public Duration getDelay() {
            return this.delay;
        }

        public void setDelay(Duration delay) {
            this.delay = delay;
        }

        public int getMaxRetries() {
            return this.maxRetries;
        }

        public void setMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
        }

        public Duration getRetryInterval() {
            return this.retryInterval;
        }

        public void setRetryInterval(Duration retryInterval) {
            this.retryInterval = retryInterval;
        }
    }
}