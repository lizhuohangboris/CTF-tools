package org.thymeleaf.extras.java8time.util;

import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-extras-java8time-3.0.1.RELEASE.jar:org/thymeleaf/extras/java8time/util/TemporalFormattingUtils.class */
public final class TemporalFormattingUtils {
    private static final DateTimeFormatter ISO8601_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");
    private final Locale locale;
    private final ZoneId defaultZoneId;

    public TemporalFormattingUtils(Locale locale, ZoneId defaultZoneId) {
        Validate.notNull(locale, "Locale cannot be null");
        Validate.notNull(defaultZoneId, "ZoneId cannot be null");
        this.locale = locale;
        this.defaultZoneId = defaultZoneId;
    }

    public String format(Object target) {
        return formatDate(target);
    }

    public String format(Object target, Locale locale) {
        Validate.notNull(locale, "Locale cannot be null");
        return formatDate(target, null, locale);
    }

    public String format(Object target, String pattern) {
        return format(target, pattern, null);
    }

    public String format(Object target, String pattern, Locale locale) {
        Validate.notEmpty(pattern, "Pattern cannot be null or empty");
        return formatDate(target, pattern, locale);
    }

    public Integer day(Object target) {
        if (target == null) {
            return null;
        }
        TemporalAccessor time = TemporalObjects.temporal(target);
        return Integer.valueOf(time.get(ChronoField.DAY_OF_MONTH));
    }

    public Integer month(Object target) {
        if (target == null) {
            return null;
        }
        TemporalAccessor time = TemporalObjects.temporal(target);
        return Integer.valueOf(time.get(ChronoField.MONTH_OF_YEAR));
    }

    public String monthName(Object target) {
        return format(target, "MMMM");
    }

    public String monthNameShort(Object target) {
        return format(target, "MMM");
    }

    public Integer year(Object target) {
        if (target == null) {
            return null;
        }
        TemporalAccessor time = TemporalObjects.temporal(target);
        return Integer.valueOf(time.get(ChronoField.YEAR));
    }

    public Integer dayOfWeek(Object target) {
        if (target == null) {
            return null;
        }
        TemporalAccessor time = TemporalObjects.temporal(target);
        return Integer.valueOf(time.get(ChronoField.DAY_OF_WEEK));
    }

    public String dayOfWeekName(Object target) {
        return format(target, "EEEE");
    }

    public String dayOfWeekNameShort(Object target) {
        return format(target, "EEE");
    }

    public Integer hour(Object target) {
        if (target == null) {
            return null;
        }
        TemporalAccessor time = TemporalObjects.temporal(target);
        return Integer.valueOf(time.get(ChronoField.HOUR_OF_DAY));
    }

    public Integer minute(Object target) {
        if (target == null) {
            return null;
        }
        TemporalAccessor time = TemporalObjects.temporal(target);
        return Integer.valueOf(time.get(ChronoField.MINUTE_OF_HOUR));
    }

    public Integer second(Object target) {
        if (target == null) {
            return null;
        }
        TemporalAccessor time = TemporalObjects.temporal(target);
        return Integer.valueOf(time.get(ChronoField.SECOND_OF_MINUTE));
    }

    public Integer nanosecond(Object target) {
        if (target == null) {
            return null;
        }
        TemporalAccessor time = TemporalObjects.temporal(target);
        return Integer.valueOf(time.get(ChronoField.NANO_OF_SECOND));
    }

    public String formatISO(Object target) {
        if (target == null) {
            return null;
        }
        if (target instanceof TemporalAccessor) {
            ChronoZonedDateTime time = TemporalObjects.zonedTime(target, this.defaultZoneId);
            return ISO8601_DATE_TIME_FORMATTER.withLocale(this.locale).format(time);
        }
        throw new IllegalArgumentException("Cannot format object of class \"" + target.getClass().getName() + "\" as a date");
    }

    private String formatDate(Object target) {
        return formatDate(target, null, null);
    }

    private String formatDate(Object target, String pattern, Locale localeOverride) {
        if (target == null) {
            return null;
        }
        Locale formattingLocale = localeOverride != null ? localeOverride : this.locale;
        try {
            if (StringUtils.isEmptyOrWhitespace(pattern)) {
                DateTimeFormatter formatter = TemporalObjects.formatterFor(target, formattingLocale);
                return formatter.format(TemporalObjects.temporal(target));
            }
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(pattern, formattingLocale);
            return formatter2.format(TemporalObjects.temporal(target));
        } catch (Exception e) {
            throw new TemplateProcessingException("Error formatting date for locale " + formattingLocale, e);
        }
    }
}