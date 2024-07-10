package org.springframework.scheduling.config;

import org.springframework.scheduling.support.CronTrigger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/config/CronTask.class */
public class CronTask extends TriggerTask {
    private final String expression;

    public CronTask(Runnable runnable, String expression) {
        this(runnable, new CronTrigger(expression));
    }

    public CronTask(Runnable runnable, CronTrigger cronTrigger) {
        super(runnable, cronTrigger);
        this.expression = cronTrigger.getExpression();
    }

    public String getExpression() {
        return this.expression;
    }
}