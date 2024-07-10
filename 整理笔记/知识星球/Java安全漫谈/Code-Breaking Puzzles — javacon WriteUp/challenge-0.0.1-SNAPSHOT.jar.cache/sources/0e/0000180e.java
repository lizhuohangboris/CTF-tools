package org.springframework.boot.autoconfigure.task;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.task.scheduling")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/task/TaskSchedulingProperties.class */
public class TaskSchedulingProperties {
    private final Pool pool = new Pool();
    private String threadNamePrefix = "scheduling-";

    public Pool getPool() {
        return this.pool;
    }

    public String getThreadNamePrefix() {
        return this.threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/task/TaskSchedulingProperties$Pool.class */
    public static class Pool {
        private int size = 1;

        public int getSize() {
            return this.size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
}