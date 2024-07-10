package ch.qos.logback.core.rolling;

import ch.qos.logback.core.spi.ContextAwareBase;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/rolling/TriggeringPolicyBase.class */
public abstract class TriggeringPolicyBase<E> extends ContextAwareBase implements TriggeringPolicy<E> {
    private boolean start;

    @Override // ch.qos.logback.core.spi.LifeCycle
    public void start() {
        this.start = true;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        this.start = false;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public boolean isStarted() {
        return this.start;
    }
}