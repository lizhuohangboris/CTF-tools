package org.springframework.scheduling.concurrent;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.enterprise.concurrent.LastExecution;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ErrorHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/concurrent/ConcurrentTaskScheduler.class */
public class ConcurrentTaskScheduler extends ConcurrentTaskExecutor implements TaskScheduler {
    @Nullable
    private static Class<?> managedScheduledExecutorServiceClass;
    private ScheduledExecutorService scheduledExecutor;
    private boolean enterpriseConcurrentScheduler;
    @Nullable
    private ErrorHandler errorHandler;

    static {
        try {
            managedScheduledExecutorServiceClass = ClassUtils.forName("javax.enterprise.concurrent.ManagedScheduledExecutorService", ConcurrentTaskScheduler.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            managedScheduledExecutorServiceClass = null;
        }
    }

    public ConcurrentTaskScheduler() {
        this.enterpriseConcurrentScheduler = false;
        this.scheduledExecutor = initScheduledExecutor(null);
    }

    public ConcurrentTaskScheduler(ScheduledExecutorService scheduledExecutor) {
        super(scheduledExecutor);
        this.enterpriseConcurrentScheduler = false;
        this.scheduledExecutor = initScheduledExecutor(scheduledExecutor);
    }

    public ConcurrentTaskScheduler(Executor concurrentExecutor, ScheduledExecutorService scheduledExecutor) {
        super(concurrentExecutor);
        this.enterpriseConcurrentScheduler = false;
        this.scheduledExecutor = initScheduledExecutor(scheduledExecutor);
    }

    private ScheduledExecutorService initScheduledExecutor(@Nullable ScheduledExecutorService scheduledExecutor) {
        if (scheduledExecutor != null) {
            this.scheduledExecutor = scheduledExecutor;
            this.enterpriseConcurrentScheduler = managedScheduledExecutorServiceClass != null && managedScheduledExecutorServiceClass.isInstance(scheduledExecutor);
        } else {
            this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
            this.enterpriseConcurrentScheduler = false;
        }
        return this.scheduledExecutor;
    }

    public void setScheduledExecutor(@Nullable ScheduledExecutorService scheduledExecutor) {
        initScheduledExecutor(scheduledExecutor);
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        Assert.notNull(errorHandler, "ErrorHandler must not be null");
        this.errorHandler = errorHandler;
    }

    @Override // org.springframework.scheduling.TaskScheduler
    @Nullable
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        try {
            if (this.enterpriseConcurrentScheduler) {
                return new EnterpriseConcurrentTriggerScheduler().schedule(decorateTask(task, true), trigger);
            }
            ErrorHandler errorHandler = this.errorHandler != null ? this.errorHandler : TaskUtils.getDefaultErrorHandler(true);
            return new ReschedulingRunnable(task, trigger, this.scheduledExecutor, errorHandler).schedule();
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        long initialDelay = startTime.getTime() - System.currentTimeMillis();
        try {
            return this.scheduledExecutor.schedule(decorateTask(task, false), initialDelay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        long initialDelay = startTime.getTime() - System.currentTimeMillis();
        try {
            return this.scheduledExecutor.scheduleAtFixedRate(decorateTask(task, true), initialDelay, period, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        try {
            return this.scheduledExecutor.scheduleAtFixedRate(decorateTask(task, true), 0L, period, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        long initialDelay = startTime.getTime() - System.currentTimeMillis();
        try {
            return this.scheduledExecutor.scheduleWithFixedDelay(decorateTask(task, true), initialDelay, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        try {
            return this.scheduledExecutor.scheduleWithFixedDelay(decorateTask(task, true), 0L, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Runnable decorateTask(Runnable task, boolean isRepeatingTask) {
        Runnable result = TaskUtils.decorateTaskWithErrorHandler(task, this.errorHandler, isRepeatingTask);
        if (this.enterpriseConcurrentScheduler) {
            result = ConcurrentTaskExecutor.ManagedTaskBuilder.buildManagedTask(result, task.toString());
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/concurrent/ConcurrentTaskScheduler$EnterpriseConcurrentTriggerScheduler.class */
    public class EnterpriseConcurrentTriggerScheduler {
        private EnterpriseConcurrentTriggerScheduler() {
            ConcurrentTaskScheduler.this = r4;
        }

        public ScheduledFuture<?> schedule(Runnable task, final Trigger trigger) {
            ManagedScheduledExecutorService executor = ConcurrentTaskScheduler.this.scheduledExecutor;
            return executor.schedule(task, new javax.enterprise.concurrent.Trigger() { // from class: org.springframework.scheduling.concurrent.ConcurrentTaskScheduler.EnterpriseConcurrentTriggerScheduler.1
                {
                    EnterpriseConcurrentTriggerScheduler.this = this;
                }

                @Nullable
                public Date getNextRunTime(@Nullable LastExecution le, Date taskScheduledTime) {
                    return trigger.nextExecutionTime(le != null ? new SimpleTriggerContext(le.getScheduledStart(), le.getRunStart(), le.getRunEnd()) : new SimpleTriggerContext());
                }

                public boolean skipRun(LastExecution lastExecution, Date scheduledRunTime) {
                    return false;
                }
            });
        }
    }
}