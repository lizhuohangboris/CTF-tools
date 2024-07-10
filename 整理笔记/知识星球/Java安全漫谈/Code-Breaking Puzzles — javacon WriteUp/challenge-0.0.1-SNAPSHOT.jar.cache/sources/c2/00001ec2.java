package org.springframework.core.task;

import java.io.Serializable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/task/SyncTaskExecutor.class */
public class SyncTaskExecutor implements TaskExecutor, Serializable {
    @Override // org.springframework.core.task.TaskExecutor, java.util.concurrent.Executor
    public void execute(Runnable task) {
        Assert.notNull(task, "Runnable must not be null");
        task.run();
    }
}