package org.springframework.core.task.support;

import java.util.concurrent.Executor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/task/support/ConcurrentExecutorAdapter.class */
public class ConcurrentExecutorAdapter implements Executor {
    private final TaskExecutor taskExecutor;

    public ConcurrentExecutorAdapter(TaskExecutor taskExecutor) {
        Assert.notNull(taskExecutor, "TaskExecutor must not be null");
        this.taskExecutor = taskExecutor;
    }

    @Override // java.util.concurrent.Executor
    public void execute(Runnable command) {
        this.taskExecutor.execute(command);
    }
}