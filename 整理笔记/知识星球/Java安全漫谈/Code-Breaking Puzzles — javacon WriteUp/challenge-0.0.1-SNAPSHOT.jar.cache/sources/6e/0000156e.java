package org.springframework.boot.autoconfigure.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.context.ApplicationEvent;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/batch/JobExecutionEvent.class */
public class JobExecutionEvent extends ApplicationEvent {
    private final JobExecution execution;

    public JobExecutionEvent(JobExecution execution) {
        super(execution);
        this.execution = execution;
    }

    public JobExecution getJobExecution() {
        return this.execution;
    }
}