package org.springframework.core.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/task/AsyncTaskExecutor.class */
public interface AsyncTaskExecutor extends TaskExecutor {
    public static final long TIMEOUT_IMMEDIATE = 0;
    public static final long TIMEOUT_INDEFINITE = Long.MAX_VALUE;

    void execute(Runnable runnable, long j);

    Future<?> submit(Runnable runnable);

    <T> Future<T> submit(Callable<T> callable);
}