package org.thymeleaf.extras.java8time.util;

import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-extras-java8time-3.0.1.RELEASE.jar:org/thymeleaf/extras/java8time/util/TemporalListUtils.class */
public final class TemporalListUtils {
    private final TemporalFormattingUtils temporalFormattingUtils;

    public TemporalListUtils(Locale locale, ZoneId defaultZoneId) {
        Validate.notNull(locale, "Locale cannot be null");
        Validate.notNull(defaultZoneId, "ZoneId cannot be null");
        this.temporalFormattingUtils = new TemporalFormattingUtils(locale, defaultZoneId);
    }

    public List<String> listFormat(List<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return listFormat(target, (v1) -> {
            return r2.format(v1);
        });
    }

    public <T extends Temporal> List<String> listFormat(List<T> target, Locale locale) {
        return listFormat(target, time -> {
            return this.temporalFormattingUtils.format(time, locale);
        });
    }

    public <T extends Temporal> List<String> listFormat(List<T> target, String pattern) {
        return listFormat(target, time -> {
            return this.temporalFormattingUtils.format(time, pattern);
        });
    }

    public <T extends Temporal> List<String> listFormat(List<T> target, String pattern, Locale locale) {
        return listFormat(target, time -> {
            return this.temporalFormattingUtils.format(time, pattern, locale);
        });
    }

    public List<Integer> listDay(List<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return listFormat(target, (v1) -> {
            return r2.day(v1);
        });
    }

    public List<Integer> listMonth(List<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return listFormat(target, (v1) -> {
            return r2.month(v1);
        });
    }

    public List<String> listMonthName(List<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return listFormat(target, (v1) -> {
            return r2.monthName(v1);
        });
    }

    public List<String> listMonthNameShort(List<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return listFormat(target, (v1) -> {
            return r2.monthNameShort(v1);
        });
    }

    public List<Integer> listYear(List<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return listFormat(target, (v1) -> {
            return r2.year(v1);
        });
    }

    public List<Integer> listDayOfWeek(List<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return listFormat(target, (v1) -> {
            return r2.dayOfWeek(v1);
        });
    }

    public List<String> listDayOfWeekName(List<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return listFormat(target, (v1) -> {
            return r2.dayOfWeekName(v1);
        });
    }

    public List<String> listDayOfWeekNameShort(List<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return listFormat(target, (v1) -> {
            return r2.dayOfWeekNameShort(v1);
        });
    }

    public List<Integer> listHour(List<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return listFormat(target, (v1) -> {
            return r2.hour(v1);
        });
    }

    public List<Integer> listMinute(List<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return listFormat(target, (v1) -> {
            return r2.minute(v1);
        });
    }

    public List<Integer> listSecond(List<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return listFormat(target, (v1) -> {
            return r2.second(v1);
        });
    }

    public List<Integer> listNanosecond(List<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return listFormat(target, (v1) -> {
            return r2.nanosecond(v1);
        });
    }

    public List<String> listFormatISO(List<? extends Temporal> target) {
        TemporalFormattingUtils temporalFormattingUtils = this.temporalFormattingUtils;
        temporalFormattingUtils.getClass();
        return listFormat(target, (v1) -> {
            return r2.formatISO(v1);
        });
    }

    private <R, T extends Temporal> List<R> listFormat(List<T> target, Function<T, R> mapFunction) {
        Validate.notNull(target, "Target cannot be null");
        return (List) target.stream().map(time -> {
            return mapFunction.apply(time);
        }).collect(Collectors.toList());
    }
}