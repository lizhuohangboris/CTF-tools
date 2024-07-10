package org.thymeleaf.extras.java8time.util;

import java.lang.reflect.Array;
import java.time.ZoneId;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-extras-java8time-3.0.1.RELEASE.jar:org/thymeleaf/extras/java8time/util/TemporalArrayUtils.class */
public final class TemporalArrayUtils {
    private final TemporalFormattingUtils temporalFormattingUtils;

    public TemporalArrayUtils(Locale locale, ZoneId defaultZoneId) {
        Validate.notNull(locale, "Locale cannot be null");
        Validate.notNull(defaultZoneId, "ZoneId cannot be null");
        this.temporalFormattingUtils = new TemporalFormattingUtils(locale, defaultZoneId);
    }

    public String[] arrayFormat(Object[] target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return (String[]) arrayFormat(target, this::format, String.class);
    }

    public String[] arrayFormat(Object[] target, Locale locale) {
        return (String[]) arrayFormat(target, time -> {
            return this.temporalFormattingUtils.format(time, locale);
        }, String.class);
    }

    public String[] arrayFormat(Object[] target, String pattern) {
        return (String[]) arrayFormat(target, time -> {
            return this.temporalFormattingUtils.format(time, pattern);
        }, String.class);
    }

    public String[] arrayFormat(Object[] target, String pattern, Locale locale) {
        return (String[]) arrayFormat(target, time -> {
            return this.temporalFormattingUtils.format(time, pattern, locale);
        }, String.class);
    }

    public Integer[] arrayDay(Object[] target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return (Integer[]) arrayFormat(target, this::day, Integer.class);
    }

    public Integer[] arrayMonth(Object[] target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return (Integer[]) arrayFormat(target, this::month, Integer.class);
    }

    public String[] arrayMonthName(Object[] target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return (String[]) arrayFormat(target, this::monthName, String.class);
    }

    public String[] arrayMonthNameShort(Object[] target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return (String[]) arrayFormat(target, this::monthNameShort, String.class);
    }

    public Integer[] arrayYear(Object[] target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return (Integer[]) arrayFormat(target, this::year, Integer.class);
    }

    public Integer[] arrayDayOfWeek(Object[] target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return (Integer[]) arrayFormat(target, this::dayOfWeek, Integer.class);
    }

    public String[] arrayDayOfWeekName(Object[] target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return (String[]) arrayFormat(target, this::dayOfWeekName, String.class);
    }

    public String[] arrayDayOfWeekNameShort(Object[] target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return (String[]) arrayFormat(target, this::dayOfWeekNameShort, String.class);
    }

    public Integer[] arrayHour(Object[] target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return (Integer[]) arrayFormat(target, this::hour, Integer.class);
    }

    public Integer[] arrayMinute(Object[] target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return (Integer[]) arrayFormat(target, this::minute, Integer.class);
    }

    public Integer[] arraySecond(Object[] target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return (Integer[]) arrayFormat(target, this::second, Integer.class);
    }

    public Integer[] arrayNanosecond(Object[] target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return (Integer[]) arrayFormat(target, this::nanosecond, Integer.class);
    }

    public String[] arrayFormatISO(Object[] target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return (String[]) arrayFormat(target, this::formatISO, String.class);
    }

    private <R> R[] arrayFormat(Object[] target, Function<Object, R> mapFunction, Class<R> returnType) {
        Validate.notNull(target, "Target cannot be null");
        return (R[]) Stream.of(target).map(time -> {
            return mapFunction.apply(time);
        }).toArray(length -> {
            return (Object[]) Array.newInstance(returnType, length);
        });
    }
}