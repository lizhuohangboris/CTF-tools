package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/util/InterruptUtil.class */
public class InterruptUtil extends ContextAwareBase {
    final boolean previouslyInterrupted;

    public InterruptUtil(Context context) {
        setContext(context);
        this.previouslyInterrupted = Thread.currentThread().isInterrupted();
    }

    public void maskInterruptFlag() {
        if (this.previouslyInterrupted) {
            Thread.interrupted();
        }
    }

    public void unmaskInterruptFlag() {
        if (this.previouslyInterrupted) {
            try {
                Thread.currentThread().interrupt();
            } catch (SecurityException se) {
                addError("Failed to intrreupt current thread", se);
            }
        }
    }
}