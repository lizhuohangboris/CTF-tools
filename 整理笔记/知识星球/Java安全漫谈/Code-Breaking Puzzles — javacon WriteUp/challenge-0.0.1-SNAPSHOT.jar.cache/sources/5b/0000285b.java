package org.thymeleaf.extras.java8time.util;

import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-extras-java8time-3.0.1.RELEASE.jar:org/thymeleaf/extras/java8time/util/TemporalSetUtils.class */
public final class TemporalSetUtils {
    private final TemporalFormattingUtils temporalFormattingUtils;

    public TemporalSetUtils(Locale locale, ZoneId defaultZoneId) {
        Validate.notNull(locale, "Locale cannot be null");
        Validate.notNull(defaultZoneId, "ZoneId cannot be null");
        this.temporalFormattingUtils = new TemporalFormattingUtils(locale, defaultZoneId);
    }

    public Set<String> setFormat(Set<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return setFormat(target, (v1) -> {
            return r2.format(v1);
        });
    }

    public <T extends Temporal> Set<String> setFormat(Set<T> target, Locale locale) {
        return setFormat(target, time -> {
            return this.temporalFormattingUtils.format(time, locale);
        });
    }

    public <T extends Temporal> Set<String> setFormat(Set<T> target, String pattern) {
        return setFormat(target, time -> {
            return this.temporalFormattingUtils.format(time, pattern);
        });
    }

    public <T extends Temporal> Set<String> setFormat(Set<T> target, String pattern, Locale locale) {
        return setFormat(target, time -> {
            return this.temporalFormattingUtils.format(time, pattern, locale);
        });
    }

    public Set<Integer> setDay(Set<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return setFormat(target, (v1) -> {
            return r2.day(v1);
        });
    }

    public Set<Integer> setMonth(Set<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return setFormat(target, (v1) -> {
            return r2.month(v1);
        });
    }

    public Set<String> setMonthName(Set<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return setFormat(target, (v1) -> {
            return r2.monthName(v1);
        });
    }

    public Set<String> setMonthNameShort(Set<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return setFormat(target, (v1) -> {
            return r2.monthNameShort(v1);
        });
    }

    public Set<Integer> setYear(Set<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return setFormat(target, (v1) -> {
            return r2.year(v1);
        });
    }

    public Set<Integer> setDayOfWeek(Set<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return setFormat(target, (v1) -> {
            return r2.dayOfWeek(v1);
        });
    }

    public Set<String> setDayOfWeekName(Set<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return setFormat(target, (v1) -> {
            return r2.dayOfWeekName(v1);
        });
    }

    public Set<String> setDayOfWeekNameShort(Set<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return setFormat(target, (v1) -> {
            return r2.dayOfWeekNameShort(v1);
        });
    }

    public Set<Integer> setHour(Set<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return setFormat(target, (v1) -> {
            return r2.hour(v1);
        });
    }

    public Set<Integer> setMinute(Set<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return setFormat(target, (v1) -> {
            return r2.minute(v1);
        });
    }

    public Set<Integer> setSecond(Set<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return setFormat(target, (v1) -> {
            return r2.second(v1);
        });
    }

    public Set<Integer> setNanosecond(Set<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return setFormat(target, (v1) -> {
            return r2.nanosecond(v1);
        });
    }

    public Set<String> setFormatISO(Set<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return setFormat(target, (v1) -> {
            return r2.formatISO(v1);
        });
    }

    private <R, T extends Temporal> Set<R> setFormat(Set<T> target, Function<T, R> mapFunction) {
        Validate.notNull(target, "Target cannot be null");
        return (Set) target.stream().map(time -> {
            return mapFunction.apply(time);
        }).collect(Collectors.toSet());
    }
}