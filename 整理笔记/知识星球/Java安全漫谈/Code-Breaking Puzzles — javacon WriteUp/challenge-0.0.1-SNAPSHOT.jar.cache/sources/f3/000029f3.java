package org.thymeleaf.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/DateUtils.class */
public final class DateUtils {
    private static final Map<DateFormatKey, DateFormat> dateFormats = new ConcurrentHashMap(4, 0.9f, 2);
    private static final SimpleDateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");

    public static Calendar create(Object year, Object month, Object day) {
        return create(year, month, day, null, null, null, null, null, null);
    }

    public static Calendar create(Object year, Object month, Object day, Object hour, Object minute) {
        return create(year, month, day, hour, minute, null, null, null, null);
    }

    public static Calendar create(Object year, Object month, Object day, Object hour, Object minute, Object second) {
        return create(year, month, day, hour, minute, second, null, null, null);
    }

    public static Calendar create(Object year, Object month, Object day, Object hour, Object minute, Object second, Object millisecond) {
        return create(year, month, day, hour, minute, second, millisecond, null, null);
    }

    public static Calendar create(Object year, Object month, Object day, Object hour, Object minute, Object second, Object millisecond, Object timeZone) {
        return create(year, month, day, hour, minute, second, millisecond, timeZone, null);
    }

    public static Calendar create(Object year, Object month, Object day, Object hour, Object minute, Object second, Object millisecond, Object timeZone, Locale locale) {
        TimeZone timeZone2;
        Calendar cal;
        BigDecimal nYear = year == null ? null : EvaluationUtils.evaluateAsNumber(year);
        BigDecimal nMonth = month == null ? null : EvaluationUtils.evaluateAsNumber(month);
        BigDecimal nDay = day == null ? null : EvaluationUtils.evaluateAsNumber(day);
        BigDecimal nHour = hour == null ? null : EvaluationUtils.evaluateAsNumber(hour);
        BigDecimal nMinute = minute == null ? null : EvaluationUtils.evaluateAsNumber(minute);
        BigDecimal nSecond = second == null ? null : EvaluationUtils.evaluateAsNumber(second);
        BigDecimal nMillisecond = millisecond == null ? null : EvaluationUtils.evaluateAsNumber(millisecond);
        if (timeZone != null) {
            timeZone2 = timeZone instanceof TimeZone ? (TimeZone) timeZone : TimeZone.getTimeZone(timeZone.toString());
        } else {
            timeZone2 = null;
        }
        TimeZone tzTimeZone = timeZone2;
        if (tzTimeZone != null && locale != null) {
            cal = Calendar.getInstance(tzTimeZone, locale);
        } else if (tzTimeZone != null) {
            cal = Calendar.getInstance(tzTimeZone);
        } else if (locale != null) {
            cal = Calendar.getInstance(locale);
        } else {
            cal = Calendar.getInstance();
        }
        if (nYear == null || nMonth == null || nDay == null) {
            throw new IllegalArgumentException("Cannot create Calendar/Date object with null year (" + nYear + "), month (" + nMonth + ") or day (" + nDay + ")");
        }
        cal.set(1, nYear.intValue());
        cal.set(2, nMonth.intValue() - 1);
        cal.set(5, nDay.intValue());
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        if (nHour != null && nMinute != null) {
            cal.set(11, nHour.intValue());
            cal.set(12, nMinute.intValue());
            if (nSecond != null) {
                cal.set(13, nSecond.intValue());
                if (nMillisecond != null) {
                    cal.set(14, nMillisecond.intValue());
                }
            } else if (nMillisecond != null) {
                throw new IllegalArgumentException("Calendar/Date object cannot be correctly created from a null second but non-null millisecond.");
            }
        } else if (nHour != null || nMinute != null) {
            throw new IllegalArgumentException("Calendar/Date object can only be correctly created if hour (" + nHour + ") and minute (" + nMinute + ") are either both null or non-null.");
        } else {
            if (nSecond != null || nMillisecond != null) {
                throw new IllegalArgumentException("Calendar/Date object cannot be correctly created from a null hour and minute but non-null second and/or millisecond.");
            }
        }
        return cal;
    }

    public static Calendar createNow() {
        return createNow(null, null);
    }

    public static Calendar createNow(Object timeZone) {
        return createNow(timeZone, null);
    }

    public static Calendar createNow(Object timeZone, Locale locale) {
        TimeZone timeZone2;
        if (timeZone != null) {
            timeZone2 = timeZone instanceof TimeZone ? (TimeZone) timeZone : TimeZone.getTimeZone(timeZone.toString());
        } else {
            timeZone2 = null;
        }
        TimeZone tzTimeZone = timeZone2;
        if (tzTimeZone != null && locale != null) {
            return Calendar.getInstance(tzTimeZone, locale);
        }
        if (tzTimeZone != null) {
            return Calendar.getInstance(tzTimeZone);
        }
        if (locale != null) {
            return Calendar.getInstance(locale);
        }
        return Calendar.getInstance();
    }

    public static Calendar createToday() {
        return createToday(null, null);
    }

    public static Calendar createToday(Object timeZone) {
        return createToday(timeZone, null);
    }

    public static Calendar createToday(Object timeZone, Locale locale) {
        Calendar cal = createNow(timeZone, locale);
        cal.set(14, 0);
        cal.set(13, 0);
        cal.set(12, 0);
        cal.set(11, 0);
        return cal;
    }

    public static String format(Object target, Locale locale) {
        if (target == null) {
            return null;
        }
        return formatDate(target, locale);
    }

    public static String format(Object target, String pattern, Locale locale) {
        Validate.notEmpty(pattern, "Pattern cannot be null or empty");
        if (target == null) {
            return null;
        }
        return formatDate(target, pattern, locale);
    }

    public static Integer day(Object target) {
        if (target == null) {
            return null;
        }
        Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(5));
    }

    public static Integer month(Object target) {
        if (target == null) {
            return null;
        }
        Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(2) + 1);
    }

    public static String monthName(Object target, Locale locale) {
        if (target == null) {
            return null;
        }
        return format(target, "MMMM", locale);
    }

    public static String monthNameShort(Object target, Locale locale) {
        if (target == null) {
            return null;
        }
        return format(target, "MMM", locale);
    }

    public static Integer year(Object target) {
        if (target == null) {
            return null;
        }
        Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(1));
    }

    public static Integer dayOfWeek(Object target) {
        if (target == null) {
            return null;
        }
        Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(7));
    }

    public static String dayOfWeekName(Object target, Locale locale) {
        if (target == null) {
            return null;
        }
        return format(target, "EEEE", locale);
    }

    public static String dayOfWeekNameShort(Object target, Locale locale) {
        if (target == null) {
            return null;
        }
        return format(target, "EEE", locale);
    }

    public static Integer hour(Object target) {
        if (target == null) {
            return null;
        }
        Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(11));
    }

    public static Integer minute(Object target) {
        if (target == null) {
            return null;
        }
        Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(12));
    }

    public static Integer second(Object target) {
        if (target == null) {
            return null;
        }
        Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(13));
    }

    public static Integer millisecond(Object target) {
        if (target == null) {
            return null;
        }
        Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(14));
    }

    private static Calendar normalizeDate(Object target) {
        if (target == null) {
            return null;
        }
        if (target instanceof Calendar) {
            return (Calendar) target;
        }
        if (target instanceof Date) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(((Date) target).getTime());
            return cal;
        }
        throw new IllegalArgumentException("Cannot normalize class \"" + target.getClass().getName() + "\" as a date");
    }

    private static String formatDate(Object target, Locale locale) {
        if (target == null) {
            return null;
        }
        return formatDate(target, null, locale);
    }

    private static String formatDate(Object target, String pattern, Locale locale) {
        String format;
        String format2;
        Validate.notNull(locale, "Locale cannot be null");
        if (target == null) {
            return null;
        }
        DateFormatKey key = new DateFormatKey(target, pattern, locale);
        DateFormat dateFormat = dateFormats.get(key);
        if (dateFormat == null) {
            if (StringUtils.isEmptyOrWhitespace(pattern)) {
                dateFormat = DateFormat.getDateTimeInstance(1, 1, locale);
            } else {
                dateFormat = new SimpleDateFormat(pattern, locale);
            }
            if (key.timeZone != null) {
                dateFormat.setTimeZone(key.timeZone);
            }
            dateFormats.put(key, dateFormat);
        }
        if (target instanceof Calendar) {
            synchronized (dateFormat) {
                format2 = dateFormat.format(((Calendar) target).getTime());
            }
            return format2;
        } else if (target instanceof Date) {
            synchronized (dateFormat) {
                format = dateFormat.format((Date) target);
            }
            return format;
        } else {
            throw new IllegalArgumentException("Cannot format object of class \"" + target.getClass().getName() + "\" as a date");
        }
    }

    public static String formatISO(Object target) {
        Date targetDate;
        String formatted;
        if (target == null) {
            return null;
        }
        if (target instanceof Calendar) {
            targetDate = ((Calendar) target).getTime();
        } else if (target instanceof Date) {
            targetDate = (Date) target;
        } else {
            throw new IllegalArgumentException("Cannot format object of class \"" + target.getClass().getName() + "\" as a date");
        }
        synchronized (ISO8601_DATE_FORMAT) {
            formatted = ISO8601_DATE_FORMAT.format(targetDate);
        }
        StringBuilder strBuilder = new StringBuilder(formatted.length() + 1);
        strBuilder.append(formatted);
        strBuilder.insert(26, ':');
        return strBuilder.toString();
    }

    private DateUtils() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/DateUtils$DateFormatKey.class */
    public static final class DateFormatKey {
        final String format;
        final TimeZone timeZone;
        final Locale locale;

        DateFormatKey(Object target, String format, Locale locale) {
            Validate.notNull(locale, "Locale cannot be null");
            this.format = format;
            this.locale = locale;
            if (target != null && (target instanceof Calendar)) {
                this.timeZone = ((Calendar) target).getTimeZone();
            } else {
                this.timeZone = null;
            }
        }

        public int hashCode() {
            int result = (31 * 1) + (this.format == null ? 0 : this.format.hashCode());
            return (31 * ((31 * result) + this.locale.hashCode())) + (this.timeZone == null ? 0 : this.timeZone.hashCode());
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            DateFormatKey other = (DateFormatKey) obj;
            if (this.format == null) {
                if (other.format != null) {
                    return false;
                }
            } else if (!this.format.equals(other.format)) {
                return false;
            }
            if (this.timeZone == null) {
                if (other.timeZone != null) {
                    return false;
                }
            } else if (!this.timeZone.equals(other.timeZone)) {
                return false;
            }
            return this.locale.equals(other.locale);
        }
    }
}