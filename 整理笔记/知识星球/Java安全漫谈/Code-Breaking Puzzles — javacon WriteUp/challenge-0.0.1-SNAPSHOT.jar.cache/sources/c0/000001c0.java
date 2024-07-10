package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/rolling/helper/RollingCalendar.class */
public class RollingCalendar extends GregorianCalendar {
    private static final long serialVersionUID = -5937537740925066161L;
    static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
    PeriodicityType periodicityType;
    String datePattern;

    public RollingCalendar(String datePattern) {
        this.periodicityType = PeriodicityType.ERRONEOUS;
        this.datePattern = datePattern;
        this.periodicityType = computePeriodicityType();
    }

    public RollingCalendar(String datePattern, TimeZone tz, Locale locale) {
        super(tz, locale);
        this.periodicityType = PeriodicityType.ERRONEOUS;
        this.datePattern = datePattern;
        this.periodicityType = computePeriodicityType();
    }

    public PeriodicityType getPeriodicityType() {
        return this.periodicityType;
    }

    public PeriodicityType computePeriodicityType() {
        GregorianCalendar calendar = new GregorianCalendar(GMT_TIMEZONE, Locale.getDefault());
        Date epoch = new Date(0L);
        if (this.datePattern != null) {
            PeriodicityType[] arr$ = PeriodicityType.VALID_ORDERED_LIST;
            for (PeriodicityType i : arr$) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.datePattern);
                simpleDateFormat.setTimeZone(GMT_TIMEZONE);
                String r0 = simpleDateFormat.format(epoch);
                Date next = innerGetEndOfThisPeriod(calendar, i, epoch);
                String r1 = simpleDateFormat.format(next);
                if (r0 != null && r1 != null && !r0.equals(r1)) {
                    return i;
                }
            }
        }
        return PeriodicityType.ERRONEOUS;
    }

    public boolean isCollisionFree() {
        switch (this.periodicityType) {
            case TOP_OF_HOUR:
                return !collision(43200000L);
            case TOP_OF_DAY:
                if (collision(CoreConstants.MILLIS_IN_ONE_WEEK) || collision(2678400000L) || collision(31536000000L)) {
                    return false;
                }
                return true;
            case TOP_OF_WEEK:
                if (collision(2937600000L) || collision(31622400000L)) {
                    return false;
                }
                return true;
            default:
                return true;
        }
    }

    private boolean collision(long delta) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.datePattern);
        simpleDateFormat.setTimeZone(GMT_TIMEZONE);
        Date epoch0 = new Date(0L);
        String r0 = simpleDateFormat.format(epoch0);
        Date epoch12 = new Date(delta);
        String r12 = simpleDateFormat.format(epoch12);
        return r0.equals(r12);
    }

    public void printPeriodicity(ContextAwareBase cab) {
        switch (this.periodicityType) {
            case TOP_OF_HOUR:
                cab.addInfo("Roll-over at the top of every hour.");
                return;
            case TOP_OF_DAY:
                cab.addInfo("Roll-over at midnight.");
                return;
            case TOP_OF_WEEK:
                cab.addInfo("Rollover at the start of week.");
                return;
            case TOP_OF_MILLISECOND:
                cab.addInfo("Roll-over every millisecond.");
                return;
            case TOP_OF_SECOND:
                cab.addInfo("Roll-over every second.");
                return;
            case TOP_OF_MINUTE:
                cab.addInfo("Roll-over every minute.");
                return;
            case HALF_DAY:
                cab.addInfo("Roll-over at midday and midnight.");
                return;
            case TOP_OF_MONTH:
                cab.addInfo("Rollover at start of every month.");
                return;
            default:
                cab.addInfo("Unknown periodicity.");
                return;
        }
    }

    public long periodBarriersCrossed(long start, long end) {
        if (start > end) {
            throw new IllegalArgumentException("Start cannot come before end");
        }
        long startFloored = getStartOfCurrentPeriodWithGMTOffsetCorrection(start, getTimeZone());
        long endFloored = getStartOfCurrentPeriodWithGMTOffsetCorrection(end, getTimeZone());
        long diff = endFloored - startFloored;
        switch (this.periodicityType) {
            case TOP_OF_HOUR:
                return ((int) diff) / CoreConstants.MILLIS_IN_ONE_HOUR;
            case TOP_OF_DAY:
                return diff / CoreConstants.MILLIS_IN_ONE_DAY;
            case TOP_OF_WEEK:
                return diff / CoreConstants.MILLIS_IN_ONE_WEEK;
            case TOP_OF_MILLISECOND:
                return diff;
            case TOP_OF_SECOND:
                return diff / 1000;
            case TOP_OF_MINUTE:
                return diff / 60000;
            case HALF_DAY:
            default:
                throw new IllegalStateException("Unknown periodicity type.");
            case TOP_OF_MONTH:
                return diffInMonths(start, end);
        }
    }

    public static int diffInMonths(long startTime, long endTime) {
        if (startTime > endTime) {
            throw new IllegalArgumentException("startTime cannot be larger than endTime");
        }
        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(startTime);
        Calendar endCal = Calendar.getInstance();
        endCal.setTimeInMillis(endTime);
        int yearDiff = endCal.get(1) - startCal.get(1);
        int monthDiff = endCal.get(2) - startCal.get(2);
        return (yearDiff * 12) + monthDiff;
    }

    private static Date innerGetEndOfThisPeriod(Calendar cal, PeriodicityType periodicityType, Date now) {
        return innerGetEndOfNextNthPeriod(cal, periodicityType, now, 1);
    }

    private static Date innerGetEndOfNextNthPeriod(Calendar cal, PeriodicityType periodicityType, Date now, int numPeriods) {
        cal.setTime(now);
        switch (periodicityType) {
            case TOP_OF_HOUR:
                cal.set(12, 0);
                cal.set(13, 0);
                cal.set(14, 0);
                cal.add(11, numPeriods);
                break;
            case TOP_OF_DAY:
                cal.set(11, 0);
                cal.set(12, 0);
                cal.set(13, 0);
                cal.set(14, 0);
                cal.add(5, numPeriods);
                break;
            case TOP_OF_WEEK:
                cal.set(7, cal.getFirstDayOfWeek());
                cal.set(11, 0);
                cal.set(12, 0);
                cal.set(13, 0);
                cal.set(14, 0);
                cal.add(3, numPeriods);
                break;
            case TOP_OF_MILLISECOND:
                cal.add(14, numPeriods);
                break;
            case TOP_OF_SECOND:
                cal.set(14, 0);
                cal.add(13, numPeriods);
                break;
            case TOP_OF_MINUTE:
                cal.set(13, 0);
                cal.set(14, 0);
                cal.add(12, numPeriods);
                break;
            case HALF_DAY:
            default:
                throw new IllegalStateException("Unknown periodicity type.");
            case TOP_OF_MONTH:
                cal.set(5, 1);
                cal.set(11, 0);
                cal.set(12, 0);
                cal.set(13, 0);
                cal.set(14, 0);
                cal.add(2, numPeriods);
                break;
        }
        return cal.getTime();
    }

    public Date getEndOfNextNthPeriod(Date now, int periods) {
        return innerGetEndOfNextNthPeriod(this, this.periodicityType, now, periods);
    }

    public Date getNextTriggeringDate(Date now) {
        return getEndOfNextNthPeriod(now, 1);
    }

    public long getStartOfCurrentPeriodWithGMTOffsetCorrection(long now, TimeZone timezone) {
        Calendar aCal = Calendar.getInstance(timezone);
        aCal.setTimeInMillis(now);
        Date toppedDate = getEndOfNextNthPeriod(aCal.getTime(), 0);
        Calendar secondCalendar = Calendar.getInstance(timezone);
        secondCalendar.setTimeInMillis(toppedDate.getTime());
        long gmtOffset = secondCalendar.get(15) + secondCalendar.get(16);
        return toppedDate.getTime() + gmtOffset;
    }
}