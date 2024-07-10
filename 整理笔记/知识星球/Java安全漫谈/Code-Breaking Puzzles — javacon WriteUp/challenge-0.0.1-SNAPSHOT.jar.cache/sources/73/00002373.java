package org.springframework.util.backoff;

import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/backoff/ExponentialBackOff.class */
public class ExponentialBackOff implements BackOff {
    public static final long DEFAULT_INITIAL_INTERVAL = 2000;
    public static final double DEFAULT_MULTIPLIER = 1.5d;
    public static final long DEFAULT_MAX_INTERVAL = 30000;
    public static final long DEFAULT_MAX_ELAPSED_TIME = Long.MAX_VALUE;
    private long initialInterval;
    private double multiplier;
    private long maxInterval;
    private long maxElapsedTime;

    public ExponentialBackOff() {
        this.initialInterval = DEFAULT_INITIAL_INTERVAL;
        this.multiplier = 1.5d;
        this.maxInterval = DEFAULT_MAX_INTERVAL;
        this.maxElapsedTime = Long.MAX_VALUE;
    }

    public ExponentialBackOff(long initialInterval, double multiplier) {
        this.initialInterval = DEFAULT_INITIAL_INTERVAL;
        this.multiplier = 1.5d;
        this.maxInterval = DEFAULT_MAX_INTERVAL;
        this.maxElapsedTime = Long.MAX_VALUE;
        checkMultiplier(multiplier);
        this.initialInterval = initialInterval;
        this.multiplier = multiplier;
    }

    public void setInitialInterval(long initialInterval) {
        this.initialInterval = initialInterval;
    }

    public long getInitialInterval() {
        return this.initialInterval;
    }

    public void setMultiplier(double multiplier) {
        checkMultiplier(multiplier);
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return this.multiplier;
    }

    public void setMaxInterval(long maxInterval) {
        this.maxInterval = maxInterval;
    }

    public long getMaxInterval() {
        return this.maxInterval;
    }

    public void setMaxElapsedTime(long maxElapsedTime) {
        this.maxElapsedTime = maxElapsedTime;
    }

    public long getMaxElapsedTime() {
        return this.maxElapsedTime;
    }

    @Override // org.springframework.util.backoff.BackOff
    public BackOffExecution start() {
        return new ExponentialBackOffExecution();
    }

    private void checkMultiplier(double multiplier) {
        Assert.isTrue(multiplier >= 1.0d, () -> {
            return "Invalid multiplier '" + multiplier + "'. Should be greater than or equal to 1. A multiplier of 1 is equivalent to a fixed interval.";
        });
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/backoff/ExponentialBackOff$ExponentialBackOffExecution.class */
    private class ExponentialBackOffExecution implements BackOffExecution {
        private long currentInterval;
        private long currentElapsedTime;

        private ExponentialBackOffExecution() {
            this.currentInterval = -1L;
            this.currentElapsedTime = 0L;
        }

        @Override // org.springframework.util.backoff.BackOffExecution
        public long nextBackOff() {
            if (this.currentElapsedTime >= ExponentialBackOff.this.maxElapsedTime) {
                return -1L;
            }
            long nextInterval = computeNextInterval();
            this.currentElapsedTime += nextInterval;
            return nextInterval;
        }

        private long computeNextInterval() {
            long maxInterval = ExponentialBackOff.this.getMaxInterval();
            if (this.currentInterval >= maxInterval) {
                return maxInterval;
            }
            if (this.currentInterval < 0) {
                long initialInterval = ExponentialBackOff.this.getInitialInterval();
                this.currentInterval = initialInterval < maxInterval ? initialInterval : maxInterval;
            } else {
                this.currentInterval = multiplyInterval(maxInterval);
            }
            return this.currentInterval;
        }

        private long multiplyInterval(long maxInterval) {
            long i = (long) (this.currentInterval * ExponentialBackOff.this.getMultiplier());
            return i > maxInterval ? maxInterval : i;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("ExponentialBackOff{");
            sb.append("currentInterval=").append(this.currentInterval < 0 ? "n/a" : this.currentInterval + "ms");
            sb.append(", multiplier=").append(ExponentialBackOff.this.getMultiplier());
            sb.append('}');
            return sb.toString();
        }
    }
}