package org.springframework.scheduling.config;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/config/FixedRateTask.class */
public class FixedRateTask extends IntervalTask {
    public FixedRateTask(Runnable runnable, long interval, long initialDelay) {
        super(runnable, interval, initialDelay);
    }
}