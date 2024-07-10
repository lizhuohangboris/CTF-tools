package org.springframework.scheduling.support;

import java.util.Date;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TriggerContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/support/SimpleTriggerContext.class */
public class SimpleTriggerContext implements TriggerContext {
    @Nullable
    private volatile Date lastScheduledExecutionTime;
    @Nullable
    private volatile Date lastActualExecutionTime;
    @Nullable
    private volatile Date lastCompletionTime;

    public SimpleTriggerContext() {
    }

    public SimpleTriggerContext(Date lastScheduledExecutionTime, Date lastActualExecutionTime, Date lastCompletionTime) {
        this.lastScheduledExecutionTime = lastScheduledExecutionTime;
        this.lastActualExecutionTime = lastActualExecutionTime;
        this.lastCompletionTime = lastCompletionTime;
    }

    public void update(Date lastScheduledExecutionTime, Date lastActualExecutionTime, Date lastCompletionTime) {
        this.lastScheduledExecutionTime = lastScheduledExecutionTime;
        this.lastActualExecutionTime = lastActualExecutionTime;
        this.lastCompletionTime = lastCompletionTime;
    }

    @Override // org.springframework.scheduling.TriggerContext
    @Nullable
    public Date lastScheduledExecutionTime() {
        return this.lastScheduledExecutionTime;
    }

    @Override // org.springframework.scheduling.TriggerContext
    @Nullable
    public Date lastActualExecutionTime() {
        return this.lastActualExecutionTime;
    }

    @Override // org.springframework.scheduling.TriggerContext
    @Nullable
    public Date lastCompletionTime() {
        return this.lastCompletionTime;
    }
}