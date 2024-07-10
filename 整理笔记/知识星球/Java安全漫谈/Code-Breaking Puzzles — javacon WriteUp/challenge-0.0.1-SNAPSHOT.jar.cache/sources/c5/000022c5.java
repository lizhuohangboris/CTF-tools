package org.springframework.scheduling.config;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/config/IntervalTask.class */
public class IntervalTask extends Task {
    private final long interval;
    private final long initialDelay;

    public IntervalTask(Runnable runnable, long interval, long initialDelay) {
        super(runnable);
        this.interval = interval;
        this.initialDelay = initialDelay;
    }

    public IntervalTask(Runnable runnable, long interval) {
        this(runnable, interval, 0L);
    }

    public long getInterval() {
        return this.interval;
    }

    public long getInitialDelay() {
        return this.initialDelay;
    }
}