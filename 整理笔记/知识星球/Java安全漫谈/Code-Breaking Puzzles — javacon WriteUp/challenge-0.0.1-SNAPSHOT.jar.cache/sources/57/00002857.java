package org.thymeleaf.extras.java8time.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.TimeZone;
import org.thymeleaf.util.EvaluationUtil;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-extras-java8time-3.0.1.RELEASE.jar:org/thymeleaf/extras/java8time/util/TemporalCreationUtils.class */
public final class TemporalCreationUtils {
    public Temporal create(Object year, Object month, Object day) {
        return LocalDate.of(integer(year), integer(month), integer(day));
    }

    public Temporal create(Object year, Object month, Object day, Object hour, Object minute) {
        return LocalDateTime.of(integer(year), integer(month), integer(day), integer(hour), integer(minute));
    }

    public Temporal create(Object year, Object month, Object day, Object hour, Object minute, Object second) {
        return LocalDateTime.of(integer(year), integer(month), integer(day), integer(hour), integer(minute), integer(second));
    }

    public Temporal create(Object year, Object month, Object day, Object hour, Object minute, Object second, Object nanosecond) {
        return LocalDateTime.of(integer(year), integer(month), integer(day), integer(hour), integer(minute), integer(second), integer(nanosecond));
    }

    public Temporal createNow() {
        return LocalDateTime.now();
    }

    public Temporal createNowForTimeZone(Object zoneId) {
        return ZonedDateTime.now(zoneId(zoneId));
    }

    public Temporal createToday() {
        return LocalDate.now();
    }

    public Temporal createTodayForTimeZone(Object zoneId) {
        return ZonedDateTime.now(zoneId(zoneId)).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public Temporal createDate(String isoDate) {
        return LocalDate.parse(isoDate);
    }

    public Temporal createDateTime(String isoDate) {
        return LocalDateTime.parse(isoDate);
    }

    public Temporal createDate(String isoDate, String pattern) {
        return LocalDate.parse(isoDate, DateTimeFormatter.ofPattern(pattern));
    }

    public Temporal createDateTime(String isoDate, String pattern) {
        return LocalDateTime.parse(isoDate, DateTimeFormatter.ofPattern(pattern));
    }

    private int integer(Object number) {
        Validate.notNull(number, "Argument cannot be null");
        return EvaluationUtil.evaluateAsNumber(number).intValue();
    }

    private ZoneId zoneId(Object zoneId) {
        Validate.notNull(zoneId, "ZoneId cannot be null");
        if (zoneId instanceof ZoneId) {
            return (ZoneId) zoneId;
        }
        if (zoneId instanceof TimeZone) {
            TimeZone timeZone = (TimeZone) zoneId;
            return timeZone.toZoneId();
        }
        return ZoneId.of(zoneId.toString());
    }
}