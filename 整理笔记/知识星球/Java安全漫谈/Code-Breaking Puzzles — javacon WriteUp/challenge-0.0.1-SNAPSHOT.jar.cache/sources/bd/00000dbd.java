package org.apache.tomcat.util.threads;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/threads/ThreadPoolExecutor.class */
public class ThreadPoolExecutor extends java.util.concurrent.ThreadPoolExecutor {
    protected static final StringManager sm = StringManager.getManager("org.apache.tomcat.util.threads.res");
    private final AtomicInteger submittedCount;
    private final AtomicLong lastContextStoppedTime;
    private final AtomicLong lastTimeThreadKilledItself;
    private long threadRenewalDelay;

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        this.submittedCount = new AtomicInteger(0);
        this.lastContextStoppedTime = new AtomicLong(0L);
        this.lastTimeThreadKilledItself = new AtomicLong(0L);
        this.threadRenewalDelay = 1000L;
        prestartAllCoreThreads();
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.submittedCount = new AtomicInteger(0);
        this.lastContextStoppedTime = new AtomicLong(0L);
        this.lastTimeThreadKilledItself = new AtomicLong(0L);
        this.threadRenewalDelay = 1000L;
        prestartAllCoreThreads();
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new RejectHandler());
        this.submittedCount = new AtomicInteger(0);
        this.lastContextStoppedTime = new AtomicLong(0L);
        this.lastTimeThreadKilledItself = new AtomicLong(0L);
        this.threadRenewalDelay = 1000L;
        prestartAllCoreThreads();
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new RejectHandler());
        this.submittedCount = new AtomicInteger(0);
        this.lastContextStoppedTime = new AtomicLong(0L);
        this.lastTimeThreadKilledItself = new AtomicLong(0L);
        this.threadRenewalDelay = 1000L;
        prestartAllCoreThreads();
    }

    public long getThreadRenewalDelay() {
        return this.threadRenewalDelay;
    }

    public void setThreadRenewalDelay(long threadRenewalDelay) {
        this.threadRenewalDelay = threadRenewalDelay;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    protected void afterExecute(Runnable r, Throwable t) {
        this.submittedCount.decrementAndGet();
        if (t == null) {
            stopCurrentThreadIfNeeded();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void stopCurrentThreadIfNeeded() {
        if (currentThreadShouldBeStopped()) {
            long lastTime = this.lastTimeThreadKilledItself.longValue();
            if (lastTime + this.threadRenewalDelay < System.currentTimeMillis() && this.lastTimeThreadKilledItself.compareAndSet(lastTime, System.currentTimeMillis() + 1)) {
                String msg = sm.getString("threadPoolExecutor.threadStoppedToAvoidPotentialLeak", Thread.currentThread().getName());
                throw new StopPooledThreadException(msg);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean currentThreadShouldBeStopped() {
        if (this.threadRenewalDelay >= 0 && (Thread.currentThread() instanceof TaskThread)) {
            TaskThread currentTaskThread = (TaskThread) Thread.currentThread();
            if (currentTaskThread.getCreationTime() < this.lastContextStoppedTime.longValue()) {
                return true;
            }
            return false;
        }
        return false;
    }

    public int getSubmittedCount() {
        return this.submittedCount.get();
    }

    @Override // java.util.concurrent.ThreadPoolExecutor, java.util.concurrent.Executor
    public void execute(Runnable command) {
        execute(command, 0L, TimeUnit.MILLISECONDS);
    }

    public void execute(Runnable command, long timeout, TimeUnit unit) {
        this.submittedCount.incrementAndGet();
        try {
            super.execute(command);
        } catch (RejectedExecutionException rx) {
            if (super.getQueue() instanceof TaskQueue) {
                TaskQueue queue = (TaskQueue) super.getQueue();
                try {
                    if (!queue.force(command, timeout, unit)) {
                        this.submittedCount.decrementAndGet();
                        throw new RejectedExecutionException("Queue capacity is full.");
                    }
                    return;
                } catch (InterruptedException x) {
                    this.submittedCount.decrementAndGet();
                    throw new RejectedExecutionException(x);
                }
            }
            this.submittedCount.decrementAndGet();
            throw rx;
        }
    }

    public void contextStopping() {
        this.lastContextStoppedTime.set(System.currentTimeMillis());
        int savedCorePoolSize = getCorePoolSize();
        TaskQueue taskQueue = getQueue() instanceof TaskQueue ? (TaskQueue) getQueue() : null;
        if (taskQueue != null) {
            taskQueue.setForcedRemainingCapacity(0);
        }
        setCorePoolSize(0);
        if (taskQueue != null) {
            taskQueue.setForcedRemainingCapacity(null);
        }
        setCorePoolSize(savedCorePoolSize);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/threads/ThreadPoolExecutor$RejectHandler.class */
    private static class RejectHandler implements RejectedExecutionHandler {
        private RejectHandler() {
        }

        @Override // java.util.concurrent.RejectedExecutionHandler
        public void rejectedExecution(Runnable r, java.util.concurrent.ThreadPoolExecutor executor) {
            throw new RejectedExecutionException();
        }
    }
}