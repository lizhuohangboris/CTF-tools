package ch.qos.logback.core.util;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/util/FixedDelay.class */
public class FixedDelay implements DelayStrategy {
    private final long subsequentDelay;
    private long nextDelay;

    public FixedDelay(long initialDelay, long subsequentDelay) {
        this.nextDelay = initialDelay;
        this.subsequentDelay = subsequentDelay;
    }

    public FixedDelay(int delay) {
        this(delay, delay);
    }

    @Override // ch.qos.logback.core.util.DelayStrategy
    public long nextDelay() {
        long delay = this.nextDelay;
        this.nextDelay = this.subsequentDelay;
        return delay;
    }
}