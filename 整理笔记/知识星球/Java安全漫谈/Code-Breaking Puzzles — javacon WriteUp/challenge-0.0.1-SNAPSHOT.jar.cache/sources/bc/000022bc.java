package org.springframework.scheduling.concurrent;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ErrorHandler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/concurrent/ThreadPoolTaskScheduler.class */
public class ThreadPoolTaskScheduler extends ExecutorConfigurationSupport implements AsyncListenableTaskExecutor, SchedulingTaskExecutor, TaskScheduler {
    @Nullable
    private volatile ErrorHandler errorHandler;
    @Nullable
    private ScheduledExecutorService scheduledExecutor;
    private volatile int poolSize = 1;
    private volatile boolean removeOnCancelPolicy = false;
    private final Map<Object, ListenableFuture<?>> listenableFutureMap = new ConcurrentReferenceHashMap(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);

    public void setPoolSize(int poolSize) {
        Assert.isTrue(poolSize > 0, "'poolSize' must be 1 or higher");
        this.poolSize = poolSize;
        if (this.scheduledExecutor instanceof ScheduledThreadPoolExecutor) {
            ((ScheduledThreadPoolExecutor) this.scheduledExecutor).setCorePoolSize(poolSize);
        }
    }

    public void setRemoveOnCancelPolicy(boolean removeOnCancelPolicy) {
        this.removeOnCancelPolicy = removeOnCancelPolicy;
        if (this.scheduledExecutor instanceof ScheduledThreadPoolExecutor) {
            ((ScheduledThreadPoolExecutor) this.scheduledExecutor).setRemoveOnCancelPolicy(removeOnCancelPolicy);
        } else if (removeOnCancelPolicy && this.scheduledExecutor != null) {
            this.logger.debug("Could not apply remove-on-cancel policy - not a ScheduledThreadPoolExecutor");
        }
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override // org.springframework.scheduling.concurrent.ExecutorConfigurationSupport
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        this.scheduledExecutor = createExecutor(this.poolSize, threadFactory, rejectedExecutionHandler);
        if (this.removeOnCancelPolicy) {
            if (this.scheduledExecutor instanceof ScheduledThreadPoolExecutor) {
                ((ScheduledThreadPoolExecutor) this.scheduledExecutor).setRemoveOnCancelPolicy(true);
            } else {
                this.logger.debug("Could not apply remove-on-cancel policy - not a ScheduledThreadPoolExecutor");
            }
        }
        return this.scheduledExecutor;
    }

    protected ScheduledExecutorService createExecutor(int poolSize, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        return new ScheduledThreadPoolExecutor(poolSize, threadFactory, rejectedExecutionHandler);
    }

    public ScheduledExecutorService getScheduledExecutor() throws IllegalStateException {
        Assert.state(this.scheduledExecutor != null, "ThreadPoolTaskScheduler not initialized");
        return this.scheduledExecutor;
    }

    public ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() throws IllegalStateException {
        Assert.state(this.scheduledExecutor instanceof ScheduledThreadPoolExecutor, "No ScheduledThreadPoolExecutor available");
        return (ScheduledThreadPoolExecutor) this.scheduledExecutor;
    }

    public int getPoolSize() {
        if (this.scheduledExecutor == null) {
            return this.poolSize;
        }
        return getScheduledThreadPoolExecutor().getPoolSize();
    }

    public boolean isRemoveOnCancelPolicy() {
        if (this.scheduledExecutor == null) {
            return this.removeOnCancelPolicy;
        }
        return getScheduledThreadPoolExecutor().getRemoveOnCancelPolicy();
    }

    public int getActiveCount() {
        if (this.scheduledExecutor == null) {
            return 0;
        }
        return getScheduledThreadPoolExecutor().getActiveCount();
    }

    @Override // org.springframework.core.task.TaskExecutor, java.util.concurrent.Executor
    public void execute(Runnable task) {
        Executor executor = getScheduledExecutor();
        try {
            executor.execute(errorHandlingTask(task, false));
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public void execute(Runnable task, long startTimeout) {
        execute(task);
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public Future<?> submit(Runnable task) {
        ExecutorService executor = getScheduledExecutor();
        try {
            return executor.submit(errorHandlingTask(task, false));
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public <T> Future<T> submit(Callable<T> task) {
        ExecutorService executor = getScheduledExecutor();
        try {
            Callable<T> taskToUse = task;
            ErrorHandler errorHandler = this.errorHandler;
            if (errorHandler != null) {
                taskToUse = new DelegatingErrorHandlingCallable<>(task, errorHandler);
            }
            return executor.submit(taskToUse);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public ListenableFuture<?> submitListenable(Runnable task) {
        ExecutorService executor = getScheduledExecutor();
        try {
            ListenableFutureTask<Object> listenableFuture = new ListenableFutureTask<>(task, null);
            executeAndTrack(executor, listenableFuture);
            return listenableFuture;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        ExecutorService executor = getScheduledExecutor();
        try {
            ListenableFutureTask<T> listenableFuture = new ListenableFutureTask<>(task);
            executeAndTrack(executor, listenableFuture);
            return listenableFuture;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    private void executeAndTrack(ExecutorService executor, ListenableFutureTask<?> listenableFuture) {
        Future<?> scheduledFuture = executor.submit(errorHandlingTask(listenableFuture, false));
        this.listenableFutureMap.put(scheduledFuture, listenableFuture);
        listenableFuture.addCallback(result -> {
            this.listenableFutureMap.remove(scheduledFuture);
        }, ex -> {
            this.listenableFutureMap.remove(scheduledFuture);
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.scheduling.concurrent.ExecutorConfigurationSupport
    public void cancelRemainingTask(Runnable task) {
        super.cancelRemainingTask(task);
        ListenableFuture<?> listenableFuture = this.listenableFutureMap.get(task);
        if (listenableFuture != null) {
            listenableFuture.cancel(true);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    @Nullable
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        ScheduledExecutorService executor = getScheduledExecutor();
        try {
            ErrorHandler errorHandler = this.errorHandler;
            if (errorHandler == null) {
                errorHandler = TaskUtils.getDefaultErrorHandler(true);
            }
            return new ReschedulingRunnable(task, trigger, executor, errorHandler).schedule();
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        ScheduledExecutorService executor = getScheduledExecutor();
        long initialDelay = startTime.getTime() - System.currentTimeMillis();
        try {
            return executor.schedule(errorHandlingTask(task, false), initialDelay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        ScheduledExecutorService executor = getScheduledExecutor();
        long initialDelay = startTime.getTime() - System.currentTimeMillis();
        try {
            return executor.scheduleAtFixedRate(errorHandlingTask(task, true), initialDelay, period, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        ScheduledExecutorService executor = getScheduledExecutor();
        try {
            return executor.scheduleAtFixedRate(errorHandlingTask(task, true), 0L, period, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        ScheduledExecutorService executor = getScheduledExecutor();
        long initialDelay = startTime.getTime() - System.currentTimeMillis();
        try {
            return executor.scheduleWithFixedDelay(errorHandlingTask(task, true), initialDelay, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        ScheduledExecutorService executor = getScheduledExecutor();
        try {
            return executor.scheduleWithFixedDelay(errorHandlingTask(task, true), 0L, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    private Runnable errorHandlingTask(Runnable task, boolean isRepeatingTask) {
        return TaskUtils.decorateTaskWithErrorHandler(task, this.errorHandler, isRepeatingTask);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/concurrent/ThreadPoolTaskScheduler$DelegatingErrorHandlingCallable.class */
    private static class DelegatingErrorHandlingCallable<V> implements Callable<V> {
        private final Callable<V> delegate;
        private final ErrorHandler errorHandler;

        public DelegatingErrorHandlingCallable(Callable<V> delegate, ErrorHandler errorHandler) {
            this.delegate = delegate;
            this.errorHandler = errorHandler;
        }

        @Override // java.util.concurrent.Callable
        @Nullable
        public V call() throws Exception {
            try {
                return this.delegate.call();
            } catch (Throwable ex) {
                this.errorHandler.handleError(ex);
                return null;
            }
        }
    }
}