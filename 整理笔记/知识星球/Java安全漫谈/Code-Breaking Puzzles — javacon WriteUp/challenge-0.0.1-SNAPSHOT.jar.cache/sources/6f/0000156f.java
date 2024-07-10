package org.springframework.boot.autoconfigure.batch;

import java.util.ArrayList;
import java.util.List;
import org.springframework.batch.core.JobExecution;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.context.ApplicationListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/batch/JobExecutionExitCodeGenerator.class */
public class JobExecutionExitCodeGenerator implements ApplicationListener<JobExecutionEvent>, ExitCodeGenerator {
    private final List<JobExecution> executions = new ArrayList();

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(JobExecutionEvent event) {
        this.executions.add(event.getJobExecution());
    }

    @Override // org.springframework.boot.ExitCodeGenerator
    public int getExitCode() {
        for (JobExecution execution : this.executions) {
            if (execution.getStatus().ordinal() > 0) {
                return execution.getStatus().ordinal();
            }
        }
        return 0;
    }
}