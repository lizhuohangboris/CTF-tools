package ch.qos.logback.core.encoder;

import ch.qos.logback.core.spi.ContextAwareBase;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/encoder/EncoderBase.class */
public abstract class EncoderBase<E> extends ContextAwareBase implements Encoder<E> {
    protected boolean started;

    @Override // ch.qos.logback.core.spi.LifeCycle
    public boolean isStarted() {
        return this.started;
    }

    public void start() {
        this.started = true;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        this.started = false;
    }
}