package org.springframework.scheduling.concurrent;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.DelegatingErrorHandlingRunnable;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.util.Assert;
import org.springframework.util.ErrorHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/concurrent/ReschedulingRunnable.class */
class ReschedulingRunnable extends DelegatingErrorHandlingRunnable implements ScheduledFuture<Object> {
    private final Trigger trigger;
    private final SimpleTriggerContext triggerContext;
    private final ScheduledExecutorService executor;
    @Nullable
    private ScheduledFuture<?> currentFuture;
    @Nullable
    private Date scheduledExecutionTime;
    private final Object triggerContextMonitor;

    public ReschedulingRunnable(Runnable delegate, Trigger trigger, ScheduledExecutorService executor, ErrorHandler errorHandler) {
        super(delegate, errorHandler);
        this.triggerContext = new SimpleTriggerContext();
        this.triggerContextMonitor = new Object();
        this.trigger = trigger;
        this.executor = executor;
    }

    @Nullable
    public ScheduledFuture<?> schedule() {
        synchronized (this.triggerContextMonitor) {
            this.scheduledExecutionTime = this.trigger.nextExecutionTime(this.triggerContext);
            if (this.scheduledExecutionTime == null) {
                return null;
            }
            long initialDelay = this.scheduledExecutionTime.getTime() - System.currentTimeMillis();
            this.currentFuture = this.executor.schedule(this, initialDelay, TimeUnit.MILLISECONDS);
            return this;
        }
    }

    private ScheduledFuture<?> obtainCurrentFuture() {
        Assert.state(this.currentFuture != null, "No scheduled future");
        return this.currentFuture;
    }

    @Override // org.springframework.scheduling.support.DelegatingErrorHandlingRunnable, java.lang.Runnable
    public void run() {
        Date actualExecutionTime = new Date();
        super.run();
        Date completionTime = new Date();
        synchronized (this.triggerContextMonitor) {
            Assert.state(this.scheduledExecutionTime != null, "No scheduled execution");
            this.triggerContext.update(this.scheduledExecutionTime, actualExecutionTime, completionTime);
            if (!obtainCurrentFuture().isCancelled()) {
                schedule();
            }
        }
    }

    @Override // java.util.concurrent.Future
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancel;
        synchronized (this.triggerContextMonitor) {
            cancel = obtainCurrentFuture().cancel(mayInterruptIfRunning);
        }
        return cancel;
    }

    @Override // java.util.concurrent.Future
    public boolean isCancelled() {
        boolean isCancelled;
        synchronized (this.triggerContextMonitor) {
            isCancelled = obtainCurrentFuture().isCancelled();
        }
        return isCancelled;
    }

    @Override // java.util.concurrent.Future
    public boolean isDone() {
        boolean isDone;
        synchronized (this.triggerContextMonitor) {
            isDone = obtainCurrentFuture().isDone();
        }
        return isDone;
    }

    @Override // java.util.concurrent.Future
    public Object get() throws InterruptedException, ExecutionException {
        ScheduledFuture<?> curr;
        synchronized (this.triggerContextMonitor) {
            curr = obtainCurrentFuture();
        }
        return curr.get();
    }

    @Override // java.util.concurrent.Future
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        ScheduledFuture<?> curr;
        synchronized (this.triggerContextMonitor) {
            curr = obtainCurrentFuture();
        }
        return curr.get(timeout, unit);
    }

    @Override // java.util.concurrent.Delayed
    public long getDelay(TimeUnit unit) {
        ScheduledFuture<?> curr;
        synchronized (this.triggerContextMonitor) {
            curr = obtainCurrentFuture();
        }
        return curr.getDelay(unit);
    }

    @Override // java.lang.Comparable
    public int compareTo(Delayed other) {
        if (this == other) {
            return 0;
        }
        long diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
        if (diff == 0) {
            return 0;
        }
        return diff < 0 ? -1 : 1;
    }
}