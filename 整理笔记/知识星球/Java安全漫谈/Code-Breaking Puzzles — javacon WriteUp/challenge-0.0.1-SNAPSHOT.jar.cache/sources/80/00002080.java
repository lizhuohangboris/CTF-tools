package org.springframework.http.client.reactive;

import java.util.concurrent.Executor;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.util.ProcessorUtils;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/reactive/JettyResourceFactory.class */
public class JettyResourceFactory implements InitializingBean, DisposableBean {
    @Nullable
    private Executor executor;
    @Nullable
    private ByteBufferPool byteBufferPool;
    @Nullable
    private Scheduler scheduler;
    private String threadPrefix = "jetty-http";

    public void setExecutor(@Nullable Executor executor) {
        this.executor = executor;
    }

    public void setByteBufferPool(@Nullable ByteBufferPool byteBufferPool) {
        this.byteBufferPool = byteBufferPool;
    }

    public void setScheduler(@Nullable Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setThreadPrefix(String threadPrefix) {
        Assert.notNull(threadPrefix, "Thread prefix is required");
        this.threadPrefix = threadPrefix;
    }

    @Nullable
    public Executor getExecutor() {
        return this.executor;
    }

    @Nullable
    public ByteBufferPool getByteBufferPool() {
        return this.byteBufferPool;
    }

    @Nullable
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        int availableProcessors;
        String name = this.threadPrefix + "@" + Integer.toHexString(hashCode());
        if (this.executor == null) {
            QueuedThreadPool threadPool = new QueuedThreadPool();
            threadPool.setName(name);
            this.executor = threadPool;
        }
        if (this.byteBufferPool == null) {
            if (this.executor instanceof ThreadPool.SizedThreadPool) {
                availableProcessors = this.executor.getMaxThreads() / 2;
            } else {
                availableProcessors = ProcessorUtils.availableProcessors() * 2;
            }
            this.byteBufferPool = new MappedByteBufferPool(2048, availableProcessors);
        }
        if (this.scheduler == null) {
            this.scheduler = new ScheduledExecutorScheduler(name + "-scheduler", false);
        }
        if (this.executor instanceof LifeCycle) {
            this.executor.start();
        }
        this.scheduler.start();
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws Exception {
        try {
            if (this.executor instanceof LifeCycle) {
                this.executor.stop();
            }
        } catch (Throwable th) {
        }
        try {
            if (this.scheduler != null) {
                this.scheduler.stop();
            }
        } catch (Throwable th2) {
        }
    }
}