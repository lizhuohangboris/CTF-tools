package org.springframework.core.task;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/task/TaskDecorator.class */
public interface TaskDecorator {
    Runnable decorate(Runnable runnable);
}