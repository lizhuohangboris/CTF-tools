package org.apache.tomcat.util.threads;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/threads/TaskQueue.class */
public class TaskQueue extends LinkedBlockingQueue<Runnable> {
    private static final long serialVersionUID = 1;
    private volatile transient ThreadPoolExecutor parent;
    private Integer forcedRemainingCapacity;

    public TaskQueue() {
        this.parent = null;
        this.forcedRemainingCapacity = null;
    }

    public TaskQueue(int capacity) {
        super(capacity);
        this.parent = null;
        this.forcedRemainingCapacity = null;
    }

    public TaskQueue(Collection<? extends Runnable> c) {
        super(c);
        this.parent = null;
        this.forcedRemainingCapacity = null;
    }

    public void setParent(ThreadPoolExecutor tp) {
        this.parent = tp;
    }

    public boolean force(Runnable o) {
        if (this.parent == null || this.parent.isShutdown()) {
            throw new RejectedExecutionException("Executor not running, can't force a command into the queue");
        }
        return super.offer((TaskQueue) o);
    }

    public boolean force(Runnable o, long timeout, TimeUnit unit) throws InterruptedException {
        if (this.parent == null || this.parent.isShutdown()) {
            throw new RejectedExecutionException("Executor not running, can't force a command into the queue");
        }
        return super.offer(o, timeout, unit);
    }

    @Override // java.util.concurrent.LinkedBlockingQueue, java.util.Queue, java.util.concurrent.BlockingQueue
    public boolean offer(Runnable o) {
        if (this.parent != null && this.parent.getPoolSize() != this.parent.getMaximumPoolSize() && this.parent.getSubmittedCount() > this.parent.getPoolSize()) {
            if (this.parent.getPoolSize() < this.parent.getMaximumPoolSize()) {
                return false;
            }
            return super.offer((TaskQueue) o);
        }
        return super.offer((TaskQueue) o);
    }

    @Override // java.util.concurrent.LinkedBlockingQueue, java.util.concurrent.BlockingQueue
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
        Runnable runnable = (Runnable) super.poll(timeout, unit);
        if (runnable == null && this.parent != null) {
            this.parent.stopCurrentThreadIfNeeded();
        }
        return runnable;
    }

    @Override // java.util.concurrent.LinkedBlockingQueue, java.util.concurrent.BlockingQueue
    public Runnable take() throws InterruptedException {
        if (this.parent != null && this.parent.currentThreadShouldBeStopped()) {
            return poll(this.parent.getKeepAliveTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
        }
        return (Runnable) super.take();
    }

    @Override // java.util.concurrent.LinkedBlockingQueue, java.util.concurrent.BlockingQueue
    public int remainingCapacity() {
        if (this.forcedRemainingCapacity != null) {
            return this.forcedRemainingCapacity.intValue();
        }
        return super.remainingCapacity();
    }

    public void setForcedRemainingCapacity(Integer forcedRemainingCapacity) {
        this.forcedRemainingCapacity = forcedRemainingCapacity;
    }
}