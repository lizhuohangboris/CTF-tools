package org.springframework.format.datetime.joda;

import java.util.Calendar;
import java.util.Date;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableInstant;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.format.datetime.DateFormatterRegistrar;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeConverters.class */
final class JodaTimeConverters {
    private JodaTimeConverters() {
    }

    public static void registerConverters(ConverterRegistry registry) {
        DateFormatterRegistrar.addDateConverters(registry);
        registry.addConverter(new DateTimeToLocalDateConverter());
        registry.addConverter(new DateTimeToLocalTimeConverter());
        registry.addConverter(new DateTimeToLocalDateTimeConverter());
        registry.addConverter(new DateTimeToDateMidnightConverter());
        registry.addConverter(new DateTimeToMutableDateTimeConverter());
        registry.addConverter(new DateTimeToInstantConverter());
        registry.addConverter(new DateTimeToDateConverter());
        registry.addConverter(new DateTimeToCalendarConverter());
        registry.addConverter(new DateTimeToLongConverter());
        registry.addConverter(new DateToReadableInstantConverter());
        registry.addConverter(new CalendarToReadableInstantConverter());
        registry.addConverter(new LongToReadableInstantConverter());
        registry.addConverter(new LocalDateTimeToLocalDateConverter());
        registry.addConverter(new LocalDateTimeToLocalTimeConverter());
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeConverters$DateTimeToLocalDateConverter.class */
    private static class DateTimeToLocalDateConverter implements Converter<DateTime, LocalDate> {
        private DateTimeToLocalDateConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public LocalDate convert(DateTime source) {
            return source.toLocalDate();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeConverters$DateTimeToLocalTimeConverter.class */
    private static class DateTimeToLocalTimeConverter implements Converter<DateTime, LocalTime> {
        private DateTimeToLocalTimeConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public LocalTime convert(DateTime source) {
            return source.toLocalTime();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeConverters$DateTimeToLocalDateTimeConverter.class */
    private static class DateTimeToLocalDateTimeConverter implements Converter<DateTime, LocalDateTime> {
        private DateTimeToLocalDateTimeConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public LocalDateTime convert(DateTime source) {
            return source.toLocalDateTime();
        }
    }

    @Deprecated
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeConverters$DateTimeToDateMidnightConverter.class */
    private static class DateTimeToDateMidnightConverter implements Converter<DateTime, DateMidnight> {
        private DateTimeToDateMidnightConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public DateMidnight convert(DateTime source) {
            return source.toDateMidnight();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeConverters$DateTimeToMutableDateTimeConverter.class */
    private static class DateTimeToMutableDateTimeConverter implements Converter<DateTime, MutableDateTime> {
        private DateTimeToMutableDateTimeConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public MutableDateTime convert(DateTime source) {
            return source.toMutableDateTime();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeConverters$DateTimeToInstantConverter.class */
    private static class DateTimeToInstantConverter implements Converter<DateTime, Instant> {
        private DateTimeToInstantConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public Instant convert(DateTime source) {
            return source.toInstant();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeConverters$DateTimeToDateConverter.class */
    private static class DateTimeToDateConverter implements Converter<DateTime, Date> {
        private DateTimeToDateConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public Date convert(DateTime source) {
            return source.toDate();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeConverters$DateTimeToCalendarConverter.class */
    private static class DateTimeToCalendarConverter implements Converter<DateTime, Calendar> {
        private DateTimeToCalendarConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public Calendar convert(DateTime source) {
            return source.toGregorianCalendar();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeConverters$DateTimeToLongConverter.class */
    private static class DateTimeToLongConverter implements Converter<DateTime, Long> {
        private DateTimeToLongConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public Long convert(DateTime source) {
            return Long.valueOf(source.getMillis());
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeConverters$DateToReadableInstantConverter.class */
    private static class DateToReadableInstantConverter implements Converter<Date, ReadableInstant> {
        private DateToReadableInstantConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public ReadableInstant convert(Date source) {
            return new DateTime(source);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeConverters$CalendarToReadableInstantConverter.class */
    private static class CalendarToReadableInstantConverter implements Converter<Calendar, ReadableInstant> {
        private CalendarToReadableInstantConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public ReadableInstant convert(Calendar source) {
            return new DateTime(source);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeConverters$LongToReadableInstantConverter.class */
    private static class LongToReadableInstantConverter implements Converter<Long, ReadableInstant> {
        private LongToReadableInstantConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public ReadableInstant convert(Long source) {
            return new DateTime(source.longValue());
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeConverters$LocalDateTimeToLocalDateConverter.class */
    private static class LocalDateTimeToLocalDateConverter implements Converter<LocalDateTime, LocalDate> {
        private LocalDateTimeToLocalDateConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public LocalDate convert(LocalDateTime source) {
            return source.toLocalDate();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeConverters$LocalDateTimeToLocalTimeConverter.class */
    private static class LocalDateTimeToLocalTimeConverter implements Converter<LocalDateTime, LocalTime> {
        private LocalDateTimeToLocalTimeConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public LocalTime convert(LocalDateTime source) {
            return source.toLocalTime();
        }
    }
}