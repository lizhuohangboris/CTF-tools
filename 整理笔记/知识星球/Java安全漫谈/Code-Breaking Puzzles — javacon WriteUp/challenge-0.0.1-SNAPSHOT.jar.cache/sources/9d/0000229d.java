package org.springframework.scheduling.annotation;

import java.util.concurrent.Executor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/annotation/AsyncConfigurer.class */
public interface AsyncConfigurer {
    @Nullable
    default Executor getAsyncExecutor() {
        return null;
    }

    @Nullable
    default AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}