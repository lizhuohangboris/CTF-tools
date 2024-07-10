package org.springframework.boot.task;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/task/TaskExecutorCustomizer.class */
public interface TaskExecutorCustomizer {
    void customize(ThreadPoolTaskExecutor taskExecutor);
}