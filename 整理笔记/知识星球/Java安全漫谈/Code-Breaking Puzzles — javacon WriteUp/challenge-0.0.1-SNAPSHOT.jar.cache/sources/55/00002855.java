package org.thymeleaf.extras.java8time.expression;

import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.thymeleaf.extras.java8time.util.TemporalArrayUtils;
import org.thymeleaf.extras.java8time.util.TemporalCreationUtils;
import org.thymeleaf.extras.java8time.util.TemporalFormattingUtils;
import org.thymeleaf.extras.java8time.util.TemporalListUtils;
import org.thymeleaf.extras.java8time.util.TemporalSetUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-extras-java8time-3.0.1.RELEASE.jar:org/thymeleaf/extras/java8time/expression/Temporals.class */
public final class Temporals {
    private final TemporalCreationUtils temporalCreationUtils;
    private final TemporalFormattingUtils temporalFormattingUtils;
    private final TemporalArrayUtils temporalArrayUtils;
    private final TemporalListUtils temporalListUtils;
    private final TemporalSetUtils temporalSetUtils;

    public Temporals(Locale locale) {
        this(locale, ZoneId.systemDefault());
    }

    public Temporals(Locale locale, ZoneId defaultZoneId) {
        Validate.notNull(locale, "Locale cannot be null");
        this.temporalCreationUtils = new TemporalCreationUtils();
        this.temporalFormattingUtils = new TemporalFormattingUtils(locale, defaultZoneId);
        this.temporalArrayUtils = new TemporalArrayUtils(locale, defaultZoneId);
        this.temporalListUtils = new TemporalListUtils(locale, defaultZoneId);
        this.temporalSetUtils = new TemporalSetUtils(locale, defaultZoneId);
    }

    public Temporal create(Object year, Object month, Object day) {
        return this.temporalCreationUtils.create(year, month, day);
    }

    public Temporal create(Object year, Object month, Object day, Object hour, Object minute) {
        return this.temporalCreationUtils.create(year, month, day, hour, minute);
    }

    public Temporal create(Object year, Object month, Object day, Object hour, Object minute, Object second) {
        return this.temporalCreationUtils.create(year, month, day, hour, minute, second);
    }

    public Temporal create(Object year, Object month, Object day, Object hour, Object minute, Object second, Object nanosecond) {
        return this.temporalCreationUtils.create(year, month, day, hour, minute, second, nanosecond);
    }

    public Temporal createDate(String isoDate) {
        return this.temporalCreationUtils.createDate(isoDate);
    }

    public Temporal createDateTime(String isoDate) {
        return this.temporalCreationUtils.createDateTime(isoDate);
    }

    public Temporal createDate(String isoDate, String pattern) {
        return this.temporalCreationUtils.createDate(isoDate, pattern);
    }

    public Temporal createDateTime(String isoDate, String pattern) {
        return this.temporalCreationUtils.createDateTime(isoDate, pattern);
    }

    public Temporal createNow() {
        return this.temporalCreationUtils.createNow();
    }

    public Temporal createNowForTimeZone(Object zoneId) {
        return this.temporalCreationUtils.createNowForTimeZone(zoneId);
    }

    public Temporal createToday() {
        return this.temporalCreationUtils.createToday();
    }

    public Temporal createTodayForTimeZone(Object zoneId) {
        return this.temporalCreationUtils.createTodayForTimeZone(zoneId);
    }

    public String format(Temporal target) {
        return this.temporalFormattingUtils.format(target);
    }

    public String[] arrayFormat(Object[] target) {
        return this.temporalArrayUtils.arrayFormat(target);
    }

    public List<String> listFormat(List<? extends Temporal> target) {
        return this.temporalListUtils.listFormat(target);
    }

    public Set<String> setFormat(Set<? extends Temporal> target) {
        return this.temporalSetUtils.setFormat(target);
    }

    public String format(Temporal target, Locale locale) {
        return this.temporalFormattingUtils.format(target, locale);
    }

    public String[] arrayFormat(Object[] target, Locale locale) {
        return this.temporalArrayUtils.arrayFormat(target, locale);
    }

    public List<String> listFormat(List<? extends Temporal> target, Locale locale) {
        return this.temporalListUtils.listFormat(target, locale);
    }

    public Set<String> setFormat(Set<? extends Temporal> target, Locale locale) {
        return this.temporalSetUtils.setFormat(target, locale);
    }

    public String format(Temporal target, String pattern) {
        return this.temporalFormattingUtils.format(target, pattern);
    }

    public String[] arrayFormat(Object[] target, String pattern) {
        return this.temporalArrayUtils.arrayFormat(target, pattern);
    }

    public List<String> listFormat(List<? extends Temporal> target, String pattern) {
        return this.temporalListUtils.listFormat(target, pattern);
    }

    public Set<String> setFormat(Set<? extends Temporal> target, String pattern) {
        return this.temporalSetUtils.setFormat(target, pattern);
    }

    public String format(Temporal target, String pattern, Locale locale) {
        return this.temporalFormattingUtils.format(target, pattern, locale);
    }

    public String[] arrayFormat(Object[] target, String pattern, Locale locale) {
        return this.temporalArrayUtils.arrayFormat(target, pattern, locale);
    }

    public List<String> listFormat(List<? extends Temporal> target, String pattern, Locale locale) {
        return this.temporalListUtils.listFormat(target, pattern, locale);
    }

    public Set<String> setFormat(Set<? extends Temporal> target, String pattern, Locale locale) {
        return this.temporalSetUtils.setFormat(target, pattern, locale);
    }

    public Integer day(Temporal target) {
        return this.temporalFormattingUtils.day(target);
    }

    public Integer[] arrayDay(Object[] target) {
        return this.temporalArrayUtils.arrayDay(target);
    }

    public List<Integer> listDay(List<? extends Temporal> target) {
        return this.temporalListUtils.listDay(target);
    }

    public Set<Integer> setDay(Set<? extends Temporal> target) {
        return this.temporalSetUtils.setDay(target);
    }

    public Integer month(Temporal target) {
        return this.temporalFormattingUtils.month(target);
    }

    public Integer[] arrayMonth(Object[] target) {
        return this.temporalArrayUtils.arrayMonth(target);
    }

    public List<Integer> listMonth(List<? extends Temporal> target) {
        return this.temporalListUtils.listMonth(target);
    }

    public Set<Integer> setMonth(Set<? extends Temporal> target) {
        return this.temporalSetUtils.setMonth(target);
    }

    public String monthName(Temporal target) {
        return this.temporalFormattingUtils.monthName(target);
    }

    public String[] arrayMonthName(Object[] target) {
        return this.temporalArrayUtils.arrayMonthName(target);
    }

    public List<String> listMonthName(List<? extends Temporal> target) {
        return this.temporalListUtils.listMonthName(target);
    }

    public Set<String> setMonthName(Set<? extends Temporal> target) {
        return this.temporalSetUtils.setMonthName(target);
    }

    public String monthNameShort(Temporal target) {
        return this.temporalFormattingUtils.monthNameShort(target);
    }

    public String[] arrayMonthNameShort(Object[] target) {
        return this.temporalArrayUtils.arrayMonthNameShort(target);
    }

    public List<String> listMonthNameShort(List<? extends Temporal> target) {
        return this.temporalListUtils.listMonthNameShort(target);
    }

    public Set<String> setMonthNameShort(Set<? extends Temporal> target) {
        return this.temporalSetUtils.setMonthNameShort(target);
    }

    public Integer year(Temporal target) {
        return this.temporalFormattingUtils.year(target);
    }

    public Integer[] arrayYear(Object[] target) {
        return this.temporalArrayUtils.arrayYear(target);
    }

    public List<Integer> listYear(List<? extends Temporal> target) {
        return this.temporalListUtils.listYear(target);
    }

    public Set<Integer> setYear(Set<? extends Temporal> target) {
        return this.temporalSetUtils.setYear(target);
    }

    public Integer dayOfWeek(Temporal target) {
        return this.temporalFormattingUtils.dayOfWeek(target);
    }

    public Integer[] arrayDayOfWeek(Object[] target) {
        return this.temporalArrayUtils.arrayDayOfWeek(target);
    }

    public List<Integer> listDayOfWeek(List<? extends Temporal> target) {
        return this.temporalListUtils.listDayOfWeek(target);
    }

    public Set<Integer> setDayOfWeek(Set<? extends Temporal> target) {
        return this.temporalSetUtils.setDayOfWeek(target);
    }

    public String dayOfWeekName(Temporal target) {
        return this.temporalFormattingUtils.dayOfWeekName(target);
    }

    public String[] arrayDayOfWeekName(Object[] target) {
        return this.temporalArrayUtils.arrayDayOfWeekName(target);
    }

    public List<String> listDayOfWeekName(List<? extends Temporal> target) {
        return this.temporalListUtils.listDayOfWeekName(target);
    }

    public Set<String> setDayOfWeekName(Set<? extends Temporal> target) {
        return this.temporalSetUtils.setDayOfWeekName(target);
    }

    public String dayOfWeekNameShort(Temporal target) {
        return this.temporalFormattingUtils.dayOfWeekNameShort(target);
    }

    public String[] arrayDayOfWeekNameShort(Object[] target) {
        return this.temporalArrayUtils.arrayDayOfWeekNameShort(target);
    }

    public List<String> listDayOfWeekNameShort(List<? extends Temporal> target) {
        return this.temporalListUtils.listDayOfWeekNameShort(target);
    }

    public Set<String> setDayOfWeekNameShort(Set<? extends Temporal> target) {
        return this.temporalSetUtils.setDayOfWeekNameShort(target);
    }

    public Integer hour(Temporal target) {
        return this.temporalFormattingUtils.hour(target);
    }

    public Integer[] arrayHour(Object[] target) {
        return this.temporalArrayUtils.arrayHour(target);
    }

    public List<Integer> listHour(List<? extends Temporal> target) {
        return this.temporalListUtils.listHour(target);
    }

    public Set<Integer> setHour(Set<? extends Temporal> target) {
        return this.temporalSetUtils.setHour(target);
    }

    public Integer minute(Temporal target) {
        return this.temporalFormattingUtils.minute(target);
    }

    public Integer[] arrayMinute(Object[] target) {
        return this.temporalArrayUtils.arrayMinute(target);
    }

    public List<Integer> listMinute(List<? extends Temporal> target) {
        return this.temporalListUtils.listMinute(target);
    }

    public Set<Integer> setMinute(Set<? extends Temporal> target) {
        return this.temporalSetUtils.setMinute(target);
    }

    public Integer second(Temporal target) {
        return this.temporalFormattingUtils.second(target);
    }

    public Integer[] arraySecond(Object[] target) {
        return this.temporalArrayUtils.arraySecond(target);
    }

    public List<Integer> listSecond(List<? extends Temporal> target) {
        return this.temporalListUtils.listSecond(target);
    }

    public Set<Integer> setSecond(Set<? extends Temporal> target) {
        return this.temporalSetUtils.setSecond(target);
    }

    public Integer nanosecond(Temporal target) {
        return this.temporalFormattingUtils.nanosecond(target);
    }

    public Integer[] arrayNanosecond(Object[] target) {
        return this.temporalArrayUtils.arrayNanosecond(target);
    }

    public List<Integer> listNanosecond(List<? extends Temporal> target) {
        return this.temporalListUtils.listNanosecond(target);
    }

    public Set<Integer> setNanosecond(Set<? extends Temporal> target) {
        return this.temporalSetUtils.setNanosecond(target);
    }

    public String formatISO(Temporal target) {
        return this.temporalFormattingUtils.formatISO(target);
    }

    public String[] arrayFormatISO(Object[] target) {
        return this.temporalArrayUtils.arrayFormatISO(target);
    }

    public List<String> listFormatISO(List<? extends Temporal> target) {
        return this.temporalListUtils.listFormatISO(target);
    }

    public Set<String> setFormatISO(Set<? extends Temporal> target) {
        return this.temporalSetUtils.setFormatISO(target);
    }
}