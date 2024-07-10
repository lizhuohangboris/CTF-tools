package org.thymeleaf.expression;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.DateUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/Calendars.class */
public final class Calendars {
    private final Locale locale;

    public Calendars(Locale locale) {
        Validate.notNull(locale, "Locale cannot be null");
        this.locale = locale;
    }

    public Calendar create(Object year, Object month, Object day) {
        return DateUtils.create(year, month, day, null, null, null, null, null, this.locale);
    }

    public Calendar create(Object year, Object month, Object day, Object hour, Object minute) {
        return DateUtils.create(year, month, day, hour, minute, null, null, null, this.locale);
    }

    public Calendar create(Object year, Object month, Object day, Object hour, Object minute, Object second) {
        return DateUtils.create(year, month, day, hour, minute, second, null, null, this.locale);
    }

    public Calendar create(Object year, Object month, Object day, Object hour, Object minute, Object second, Object millisecond) {
        return DateUtils.create(year, month, day, hour, minute, second, millisecond, null, this.locale);
    }

    public Calendar createForTimeZone(Object year, Object month, Object day, Object timeZone) {
        return DateUtils.create(year, month, day, null, null, null, null, timeZone, this.locale);
    }

    public Calendar createForTimeZone(Object year, Object month, Object day, Object hour, Object minute, Object timeZone) {
        return DateUtils.create(year, month, day, hour, minute, null, null, timeZone, this.locale);
    }

    public Calendar createForTimeZone(Object year, Object month, Object day, Object hour, Object minute, Object second, Object timeZone) {
        return DateUtils.create(year, month, day, hour, minute, second, null, timeZone, this.locale);
    }

    public Calendar createForTimeZone(Object year, Object month, Object day, Object hour, Object minute, Object second, Object millisecond, Object timeZone) {
        return DateUtils.create(year, month, day, hour, minute, second, millisecond, timeZone, this.locale);
    }

    public Calendar createNow() {
        return DateUtils.createNow(null, this.locale);
    }

    public Calendar createNowForTimeZone(Object timeZone) {
        return DateUtils.createNow(timeZone, this.locale);
    }

    public Calendar createToday() {
        return DateUtils.createToday(null, this.locale);
    }

    public Calendar createTodayForTimeZone(Object timeZone) {
        return DateUtils.createToday(timeZone, this.locale);
    }

    public String format(Calendar target) {
        if (target == null) {
            return null;
        }
        try {
            return DateUtils.format(target, this.locale);
        } catch (Exception e) {
            throw new TemplateProcessingException("Error formatting calendar with standard format for locale " + this.locale, e);
        }
    }

    public String[] arrayFormat(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = format((Calendar) target[i]);
        }
        return result;
    }

    public List<String> listFormat(List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(format(element));
        }
        return result;
    }

    public Set<String> setFormat(Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(format(element));
        }
        return result;
    }

    public String format(Calendar target, String pattern) {
        if (target == null) {
            return null;
        }
        try {
            return DateUtils.format(target, pattern, this.locale);
        } catch (Exception e) {
            throw new TemplateProcessingException("Error formatting calendar with format pattern \"" + pattern + "\"", e);
        }
    }

    public String[] arrayFormat(Object[] target, String pattern) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = format((Calendar) target[i], pattern);
        }
        return result;
    }

    public List<String> listFormat(List<? extends Calendar> target, String pattern) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(format(element, pattern));
        }
        return result;
    }

    public Set<String> setFormat(Set<? extends Calendar> target, String pattern) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(format(element, pattern));
        }
        return result;
    }

    public Integer day(Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.day(target);
    }

    public Integer[] arrayDay(Object[] target) {
        if (target == null) {
            return null;
        }
        Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = day((Calendar) target[i]);
        }
        return result;
    }

    public List<Integer> listDay(List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        List<Integer> result = new ArrayList<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(day(element));
        }
        return result;
    }

    public Set<Integer> setDay(Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        Set<Integer> result = new LinkedHashSet<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(day(element));
        }
        return result;
    }

    public Integer month(Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.month(target);
    }

    public Integer[] arrayMonth(Object[] target) {
        if (target == null) {
            return null;
        }
        Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = month((Calendar) target[i]);
        }
        return result;
    }

    public List<Integer> listMonth(List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        List<Integer> result = new ArrayList<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(month(element));
        }
        return result;
    }

    public Set<Integer> setMonth(Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        Set<Integer> result = new LinkedHashSet<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(month(element));
        }
        return result;
    }

    public String monthName(Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.monthName(target, this.locale);
    }

    public String[] arrayMonthName(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = monthName((Calendar) target[i]);
        }
        return result;
    }

    public List<String> listMonthName(List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(monthName(element));
        }
        return result;
    }

    public Set<String> setMonthName(Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(monthName(element));
        }
        return result;
    }

    public String monthNameShort(Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.monthNameShort(target, this.locale);
    }

    public String[] arrayMonthNameShort(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = monthNameShort((Calendar) target[i]);
        }
        return result;
    }

    public List<String> listMonthNameShort(List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(monthNameShort(element));
        }
        return result;
    }

    public Set<String> setMonthNameShort(Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(monthNameShort(element));
        }
        return result;
    }

    public Integer year(Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.year(target);
    }

    public Integer[] arrayYear(Object[] target) {
        if (target == null) {
            return null;
        }
        Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = year((Calendar) target[i]);
        }
        return result;
    }

    public List<Integer> listYear(List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        List<Integer> result = new ArrayList<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(year(element));
        }
        return result;
    }

    public Set<Integer> setYear(Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        Set<Integer> result = new LinkedHashSet<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(year(element));
        }
        return result;
    }

    public Integer dayOfWeek(Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.dayOfWeek(target);
    }

    public Integer[] arrayDayOfWeek(Object[] target) {
        if (target == null) {
            return null;
        }
        Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = dayOfWeek((Calendar) target[i]);
        }
        return result;
    }

    public List<Integer> listDayOfWeek(List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        List<Integer> result = new ArrayList<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(dayOfWeek(element));
        }
        return result;
    }

    public Set<Integer> setDayOfWeek(Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        Set<Integer> result = new LinkedHashSet<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(dayOfWeek(element));
        }
        return result;
    }

    public String dayOfWeekName(Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.dayOfWeekName(target, this.locale);
    }

    public String[] arrayDayOfWeekName(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = dayOfWeekName((Calendar) target[i]);
        }
        return result;
    }

    public List<String> listDayOfWeekName(List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(dayOfWeekName(element));
        }
        return result;
    }

    public Set<String> setDayOfWeekName(Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(dayOfWeekName(element));
        }
        return result;
    }

    public String dayOfWeekNameShort(Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.dayOfWeekNameShort(target, this.locale);
    }

    public String[] arrayDayOfWeekNameShort(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = dayOfWeekNameShort((Calendar) target[i]);
        }
        return result;
    }

    public List<String> listDayOfWeekNameShort(List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(dayOfWeekNameShort(element));
        }
        return result;
    }

    public Set<String> setDayOfWeekNameShort(Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(dayOfWeekNameShort(element));
        }
        return result;
    }

    public Integer hour(Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.hour(target);
    }

    public Integer[] arrayHour(Object[] target) {
        if (target == null) {
            return null;
        }
        Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = hour((Calendar) target[i]);
        }
        return result;
    }

    public List<Integer> listHour(List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        List<Integer> result = new ArrayList<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(hour(element));
        }
        return result;
    }

    public Set<Integer> setHour(Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        Set<Integer> result = new LinkedHashSet<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(hour(element));
        }
        return result;
    }

    public Integer minute(Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.minute(target);
    }

    public Integer[] arrayMinute(Object[] target) {
        if (target == null) {
            return null;
        }
        Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = minute((Calendar) target[i]);
        }
        return result;
    }

    public List<Integer> listMinute(List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        List<Integer> result = new ArrayList<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(minute(element));
        }
        return result;
    }

    public Set<Integer> setMinute(Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        Set<Integer> result = new LinkedHashSet<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(minute(element));
        }
        return result;
    }

    public Integer second(Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.second(target);
    }

    public Integer[] arraySecond(Object[] target) {
        if (target == null) {
            return null;
        }
        Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = second((Calendar) target[i]);
        }
        return result;
    }

    public List<Integer> listSecond(List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        List<Integer> result = new ArrayList<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(second(element));
        }
        return result;
    }

    public Set<Integer> setSecond(Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        Set<Integer> result = new LinkedHashSet<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(second(element));
        }
        return result;
    }

    public Integer millisecond(Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.millisecond(target);
    }

    public Integer[] arrayMillisecond(Object[] target) {
        if (target == null) {
            return null;
        }
        Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = millisecond((Calendar) target[i]);
        }
        return result;
    }

    public List<Integer> listMillisecond(List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        List<Integer> result = new ArrayList<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(millisecond(element));
        }
        return result;
    }

    public Set<Integer> setMillisecond(Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        Set<Integer> result = new LinkedHashSet<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(millisecond(element));
        }
        return result;
    }

    public String formatISO(Calendar target) {
        if (target == null) {
            return null;
        }
        try {
            return DateUtils.formatISO(target);
        } catch (Exception e) {
            throw new TemplateProcessingException("Error formatting calendar as ISO8601", e);
        }
    }

    public String[] arrayFormatISO(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = formatISO((Calendar) target[i]);
        }
        return result;
    }

    public List<String> listFormatISO(List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(formatISO(element));
        }
        return result;
    }

    public Set<String> setFormatISO(Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Calendar element : target) {
            result.add(formatISO(element));
        }
        return result;
    }
}