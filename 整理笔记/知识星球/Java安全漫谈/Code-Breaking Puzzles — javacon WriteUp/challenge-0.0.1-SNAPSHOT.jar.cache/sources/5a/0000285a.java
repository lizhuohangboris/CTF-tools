package org.thymeleaf.extras.java8time.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-extras-java8time-3.0.1.RELEASE.jar:org/thymeleaf/extras/java8time/util/TemporalObjects.class */
public final class TemporalObjects {
    public static DateTimeFormatter formatterFor(Object target, Locale locale) {
        Validate.notNull(target, "Target cannot be null");
        Validate.notNull(locale, "Locale cannot be null");
        if (target instanceof LocalDate) {
            return DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale);
        }
        if (target instanceof LocalDateTime) {
            return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM).withLocale(locale);
        }
        if (target instanceof ZonedDateTime) {
            return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withLocale(locale);
        }
        if (target instanceof Instant) {
            return new DateTimeFormatterBuilder().appendInstant().toFormatter();
        }
        if (target instanceof LocalTime) {
            return DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withLocale(locale);
        }
        if (target instanceof OffsetTime) {
            return new DateTimeFormatterBuilder().appendValue(ChronoField.HOUR_OF_DAY).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR).appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE).appendLocalizedOffset(TextStyle.FULL).toFormatter().withLocale(locale);
        }
        if (target instanceof OffsetDateTime) {
            return new DateTimeFormatterBuilder().appendText(ChronoField.MONTH_OF_YEAR).appendLiteral(' ').appendValue(ChronoField.DAY_OF_MONTH).appendLiteral(", ").appendValue(ChronoField.YEAR).appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR).appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE).appendLocalizedOffset(TextStyle.FULL).toFormatter().withLocale(locale);
        }
        if (target instanceof Year) {
            return new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR).toFormatter();
        }
        if (target instanceof YearMonth) {
            return new DateTimeFormatterBuilder().appendText(ChronoField.MONTH_OF_YEAR).appendLiteral(' ').appendValue(ChronoField.YEAR).toFormatter().withLocale(locale);
        }
        throw new IllegalArgumentException("Cannot format object of class \"" + target.getClass().getName() + "\" as a date");
    }

    public static ChronoZonedDateTime zonedTime(Object target, ZoneId defaultZoneId) {
        Validate.notNull(target, "Target cannot be null");
        Validate.notNull(defaultZoneId, "ZoneId cannot be null");
        if (target instanceof ChronoZonedDateTime) {
            return (ChronoZonedDateTime) target;
        }
        if (target instanceof LocalDateTime) {
            return ZonedDateTime.of((LocalDateTime) target, defaultZoneId);
        }
        if (target instanceof LocalDate) {
            return ZonedDateTime.of((LocalDate) target, LocalTime.MIDNIGHT, defaultZoneId);
        }
        if (target instanceof Instant) {
            return ZonedDateTime.ofInstant((Instant) target, defaultZoneId);
        }
        throw new IllegalArgumentException("Cannot format object of class \"" + target.getClass().getName() + "\" as a date");
    }

    public static TemporalAccessor temporal(Object target) {
        Validate.notNull(target, "Target cannot be null");
        if (target instanceof TemporalAccessor) {
            return (TemporalAccessor) target;
        }
        throw new IllegalArgumentException("Cannot normalize class \"" + target.getClass().getName() + "\" as a date");
    }
}