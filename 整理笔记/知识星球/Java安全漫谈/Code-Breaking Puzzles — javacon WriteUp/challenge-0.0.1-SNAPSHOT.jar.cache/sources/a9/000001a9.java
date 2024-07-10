package ch.qos.logback.core.rolling;

import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.util.FileSize;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/rolling/SizeAndTimeBasedRollingPolicy.class */
public class SizeAndTimeBasedRollingPolicy<E> extends TimeBasedRollingPolicy<E> {
    FileSize maxFileSize;

    @Override // ch.qos.logback.core.rolling.TimeBasedRollingPolicy, ch.qos.logback.core.rolling.RollingPolicyBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        SizeAndTimeBasedFNATP<E> sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP<>(SizeAndTimeBasedFNATP.Usage.EMBEDDED);
        if (this.maxFileSize == null) {
            addError("maxFileSize property is mandatory.");
            return;
        }
        addInfo("Archive files will be limited to [" + this.maxFileSize + "] each.");
        sizeAndTimeBasedFNATP.setMaxFileSize(this.maxFileSize);
        this.timeBasedFileNamingAndTriggeringPolicy = sizeAndTimeBasedFNATP;
        if (!isUnboundedTotalSizeCap() && this.totalSizeCap.getSize() < this.maxFileSize.getSize()) {
            addError("totalSizeCap of [" + this.totalSizeCap + "] is smaller than maxFileSize [" + this.maxFileSize + "] which is non-sensical");
        } else {
            super.start();
        }
    }

    public void setMaxFileSize(FileSize aMaxFileSize) {
        this.maxFileSize = aMaxFileSize;
    }

    @Override // ch.qos.logback.core.rolling.TimeBasedRollingPolicy
    public String toString() {
        return "c.q.l.core.rolling.SizeAndTimeBasedRollingPolicy@" + hashCode();
    }
}