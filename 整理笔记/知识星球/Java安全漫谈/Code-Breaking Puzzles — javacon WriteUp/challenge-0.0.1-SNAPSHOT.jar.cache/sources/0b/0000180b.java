package org.springframework.boot.autoconfigure.task;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.task.execution")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/task/TaskExecutionProperties.class */
public class TaskExecutionProperties {
    private final Pool pool = new Pool();
    private String threadNamePrefix = "task-";

    public Pool getPool() {
        return this.pool;
    }

    public String getThreadNamePrefix() {
        return this.threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/task/TaskExecutionProperties$Pool.class */
    public static class Pool {
        private int queueCapacity = Integer.MAX_VALUE;
        private int coreSize = 8;
        private int maxSize = Integer.MAX_VALUE;
        private boolean allowCoreThreadTimeout = true;
        private Duration keepAlive = Duration.ofSeconds(60);

        public int getQueueCapacity() {
            return this.queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public int getCoreSize() {
            return this.coreSize;
        }

        public void setCoreSize(int coreSize) {
            this.coreSize = coreSize;
        }

        public int getMaxSize() {
            return this.maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public boolean isAllowCoreThreadTimeout() {
            return this.allowCoreThreadTimeout;
        }

        public void setAllowCoreThreadTimeout(boolean allowCoreThreadTimeout) {
            this.allowCoreThreadTimeout = allowCoreThreadTimeout;
        }

        public Duration getKeepAlive() {
            return this.keepAlive;
        }

        public void setKeepAlive(Duration keepAlive) {
            this.keepAlive = keepAlive;
        }
    }
}