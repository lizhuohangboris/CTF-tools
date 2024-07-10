package ch.qos.logback.core.rolling;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import ch.qos.logback.core.rolling.helper.RollingCalendar;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.File;
import java.util.Date;
import java.util.Locale;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/rolling/TimeBasedFileNamingAndTriggeringPolicyBase.class */
public abstract class TimeBasedFileNamingAndTriggeringPolicyBase<E> extends ContextAwareBase implements TimeBasedFileNamingAndTriggeringPolicy<E> {
    private static String COLLIDING_DATE_FORMAT_URL = "http://logback.qos.ch/codes.html#rfa_collision_in_dateFormat";
    protected TimeBasedRollingPolicy<E> tbrp;
    protected String elapsedPeriodsFileName;
    protected RollingCalendar rc;
    protected long nextCheck;
    protected ArchiveRemover archiveRemover = null;
    protected long artificialCurrentTime = -1;
    protected Date dateInCurrentPeriod = null;
    protected boolean started = false;
    protected boolean errorFree = true;

    @Override // ch.qos.logback.core.spi.LifeCycle
    public boolean isStarted() {
        return this.started;
    }

    public void start() {
        DateTokenConverter<Object> dtc = this.tbrp.fileNamePattern.getPrimaryDateTokenConverter();
        if (dtc == null) {
            throw new IllegalStateException("FileNamePattern [" + this.tbrp.fileNamePattern.getPattern() + "] does not contain a valid DateToken");
        }
        if (dtc.getTimeZone() != null) {
            this.rc = new RollingCalendar(dtc.getDatePattern(), dtc.getTimeZone(), Locale.getDefault());
        } else {
            this.rc = new RollingCalendar(dtc.getDatePattern());
        }
        addInfo("The date pattern is '" + dtc.getDatePattern() + "' from file name pattern '" + this.tbrp.fileNamePattern.getPattern() + "'.");
        this.rc.printPeriodicity(this);
        if (!this.rc.isCollisionFree()) {
            addError("The date format in FileNamePattern will result in collisions in the names of archived log files.");
            addError(CoreConstants.MORE_INFO_PREFIX + COLLIDING_DATE_FORMAT_URL);
            withErrors();
            return;
        }
        setDateInCurrentPeriod(new Date(getCurrentTime()));
        if (this.tbrp.getParentsRawFileProperty() != null) {
            File currentFile = new File(this.tbrp.getParentsRawFileProperty());
            if (currentFile.exists() && currentFile.canRead()) {
                setDateInCurrentPeriod(new Date(currentFile.lastModified()));
            }
        }
        addInfo("Setting initial period to " + this.dateInCurrentPeriod);
        computeNextCheck();
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        this.started = false;
    }

    public void computeNextCheck() {
        this.nextCheck = this.rc.getNextTriggeringDate(this.dateInCurrentPeriod).getTime();
    }

    public void setDateInCurrentPeriod(long now) {
        this.dateInCurrentPeriod.setTime(now);
    }

    public void setDateInCurrentPeriod(Date _dateInCurrentPeriod) {
        this.dateInCurrentPeriod = _dateInCurrentPeriod;
    }

    @Override // ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy
    public String getElapsedPeriodsFileName() {
        return this.elapsedPeriodsFileName;
    }

    public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
        return this.tbrp.fileNamePatternWithoutCompSuffix.convert(this.dateInCurrentPeriod);
    }

    @Override // ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy
    public void setCurrentTime(long timeInMillis) {
        this.artificialCurrentTime = timeInMillis;
    }

    @Override // ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy
    public long getCurrentTime() {
        if (this.artificialCurrentTime >= 0) {
            return this.artificialCurrentTime;
        }
        return System.currentTimeMillis();
    }

    @Override // ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy
    public void setTimeBasedRollingPolicy(TimeBasedRollingPolicy<E> _tbrp) {
        this.tbrp = _tbrp;
    }

    @Override // ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy
    public ArchiveRemover getArchiveRemover() {
        return this.archiveRemover;
    }

    public void withErrors() {
        this.errorFree = false;
    }

    public boolean isErrorFree() {
        return this.errorFree;
    }
}