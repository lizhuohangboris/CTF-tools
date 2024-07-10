package org.springframework.scheduling;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/SchedulingAwareRunnable.class */
public interface SchedulingAwareRunnable extends Runnable {
    boolean isLongLived();
}