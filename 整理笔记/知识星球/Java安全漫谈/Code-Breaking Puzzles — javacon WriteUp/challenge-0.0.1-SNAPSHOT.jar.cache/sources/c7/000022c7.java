package org.springframework.scheduling.config;

import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/config/ScheduledTaskHolder.class */
public interface ScheduledTaskHolder {
    Set<ScheduledTask> getScheduledTasks();
}