package org.springframework.core.task;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrencyThrottleSupport;
import org.springframework.util.CustomizableThreadCreator;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/task/SimpleAsyncTaskExecutor.class */
public class SimpleAsyncTaskExecutor extends CustomizableThreadCreator implements AsyncListenableTaskExecutor, Serializable {
    public static final int UNBOUNDED_CONCURRENCY = -1;
    public static final int NO_CONCURRENCY = 0;
    private final ConcurrencyThrottleAdapter concurrencyThrottle;
    @Nullable
    private ThreadFactory threadFactory;
    @Nullable
    private TaskDecorator taskDecorator;

    public SimpleAsyncTaskExecutor() {
        this.concurrencyThrottle = new ConcurrencyThrottleAdapter();
    }

    public SimpleAsyncTaskExecutor(String threadNamePrefix) {
        super(threadNamePrefix);
        this.concurrencyThrottle = new ConcurrencyThrottleAdapter();
    }

    public SimpleAsyncTaskExecutor(ThreadFactory threadFactory) {
        this.concurrencyThrottle = new ConcurrencyThrottleAdapter();
        this.threadFactory = threadFactory;
    }

    public void setThreadFactory(@Nullable ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    @Nullable
    public final ThreadFactory getThreadFactory() {
        return this.threadFactory;
    }

    public final void setTaskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
    }

    public void setConcurrencyLimit(int concurrencyLimit) {
        this.concurrencyThrottle.setConcurrencyLimit(concurrencyLimit);
    }

    public final int getConcurrencyLimit() {
        return this.concurrencyThrottle.getConcurrencyLimit();
    }

    public final boolean isThrottleActive() {
        return this.concurrencyThrottle.isThrottleActive();
    }

    @Override // org.springframework.core.task.TaskExecutor, java.util.concurrent.Executor
    public void execute(Runnable task) {
        execute(task, Long.MAX_VALUE);
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public void execute(Runnable task, long startTimeout) {
        Assert.notNull(task, "Runnable must not be null");
        Runnable taskToUse = this.taskDecorator != null ? this.taskDecorator.decorate(task) : task;
        if (isThrottleActive() && startTimeout > 0) {
            this.concurrencyThrottle.beforeAccess();
            doExecute(new ConcurrencyThrottlingRunnable(taskToUse));
            return;
        }
        doExecute(taskToUse);
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public Future<?> submit(Runnable task) {
        FutureTask<Object> future = new FutureTask<>(task, null);
        execute(future, Long.MAX_VALUE);
        return future;
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> future = new FutureTask<>(task);
        execute(future, Long.MAX_VALUE);
        return future;
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public ListenableFuture<?> submitListenable(Runnable task) {
        ListenableFutureTask<Object> future = new ListenableFutureTask<>(task, null);
        execute(future, Long.MAX_VALUE);
        return future;
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        ListenableFutureTask<T> future = new ListenableFutureTask<>(task);
        execute(future, Long.MAX_VALUE);
        return future;
    }

    protected void doExecute(Runnable task) {
        Thread thread = this.threadFactory != null ? this.threadFactory.newThread(task) : createThread(task);
        thread.start();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/task/SimpleAsyncTaskExecutor$ConcurrencyThrottleAdapter.class */
    public static class ConcurrencyThrottleAdapter extends ConcurrencyThrottleSupport {
        private ConcurrencyThrottleAdapter() {
        }

        @Override // org.springframework.util.ConcurrencyThrottleSupport
        public void beforeAccess() {
            super.beforeAccess();
        }

        @Override // org.springframework.util.ConcurrencyThrottleSupport
        public void afterAccess() {
            super.afterAccess();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/task/SimpleAsyncTaskExecutor$ConcurrencyThrottlingRunnable.class */
    public class ConcurrencyThrottlingRunnable implements Runnable {
        private final Runnable target;

        public ConcurrencyThrottlingRunnable(Runnable target) {
            SimpleAsyncTaskExecutor.this = r4;
            this.target = target;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.target.run();
            } finally {
                SimpleAsyncTaskExecutor.this.concurrencyThrottle.afterAccess();
            }
        }
    }
}