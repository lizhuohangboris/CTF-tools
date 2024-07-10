package org.springframework.boot.task;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/task/TaskSchedulerCustomizer.class */
public interface TaskSchedulerCustomizer {
    void customize(ThreadPoolTaskScheduler taskScheduler);
}