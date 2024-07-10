package ch.qos.logback.core.rolling;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.helper.TimeBasedArchiveRemover;
import java.io.File;
import java.util.Date;

@NoAutoStart
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/rolling/DefaultTimeBasedFileNamingAndTriggeringPolicy.class */
public class DefaultTimeBasedFileNamingAndTriggeringPolicy<E> extends TimeBasedFileNamingAndTriggeringPolicyBase<E> {
    @Override // ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicyBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        super.start();
        if (!super.isErrorFree()) {
            return;
        }
        if (this.tbrp.fileNamePattern.hasIntegerTokenCOnverter()) {
            addError("Filename pattern [" + this.tbrp.fileNamePattern + "] contains an integer token converter, i.e. %i, INCOMPATIBLE with this configuration. Remove it.");
            return;
        }
        this.archiveRemover = new TimeBasedArchiveRemover(this.tbrp.fileNamePattern, this.rc);
        this.archiveRemover.setContext(this.context);
        this.started = true;
    }

    @Override // ch.qos.logback.core.rolling.TriggeringPolicy
    public boolean isTriggeringEvent(File activeFile, E event) {
        long time = getCurrentTime();
        if (time >= this.nextCheck) {
            Date dateOfElapsedPeriod = this.dateInCurrentPeriod;
            addInfo("Elapsed period: " + dateOfElapsedPeriod);
            this.elapsedPeriodsFileName = this.tbrp.fileNamePatternWithoutCompSuffix.convert(dateOfElapsedPeriod);
            setDateInCurrentPeriod(time);
            computeNextCheck();
            return true;
        }
        return false;
    }

    public String toString() {
        return "c.q.l.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy";
    }
}