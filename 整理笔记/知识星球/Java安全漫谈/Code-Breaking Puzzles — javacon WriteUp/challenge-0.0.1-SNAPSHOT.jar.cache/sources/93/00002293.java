package org.springframework.scheduling;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/TaskScheduler.class */
public interface TaskScheduler {
    @Nullable
    ScheduledFuture<?> schedule(Runnable runnable, Trigger trigger);

    ScheduledFuture<?> schedule(Runnable runnable, Date date);

    ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, Date date, long j);

    ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long j);

    ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, Date date, long j);

    ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long j);

    default ScheduledFuture<?> schedule(Runnable task, Instant startTime) {
        return schedule(task, Date.from(startTime));
    }

    default ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Instant startTime, Duration period) {
        return scheduleAtFixedRate(task, Date.from(startTime), period.toMillis());
    }

    default ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Duration period) {
        return scheduleAtFixedRate(task, period.toMillis());
    }

    default ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay) {
        return scheduleWithFixedDelay(task, Date.from(startTime), delay.toMillis());
    }

    default ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Duration delay) {
        return scheduleWithFixedDelay(task, delay.toMillis());
    }
}