package org.springframework.format.datetime.joda;

import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.MonthDay;
import org.joda.time.Period;
import org.joda.time.ReadableInstant;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeFormatterRegistrar.class */
public class JodaTimeFormatterRegistrar implements FormatterRegistrar {
    private final Map<Type, DateTimeFormatter> formatters = new EnumMap(Type.class);
    private final Map<Type, DateTimeFormatterFactory> factories = new EnumMap(Type.class);

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeFormatterRegistrar$Type.class */
    public enum Type {
        DATE,
        TIME,
        DATE_TIME
    }

    public JodaTimeFormatterRegistrar() {
        Type[] values;
        for (Type type : Type.values()) {
            this.factories.put(type, new DateTimeFormatterFactory());
        }
    }

    public void setUseIsoFormat(boolean useIsoFormat) {
        this.factories.get(Type.DATE).setIso(useIsoFormat ? DateTimeFormat.ISO.DATE : DateTimeFormat.ISO.NONE);
        this.factories.get(Type.TIME).setIso(useIsoFormat ? DateTimeFormat.ISO.TIME : DateTimeFormat.ISO.NONE);
        this.factories.get(Type.DATE_TIME).setIso(useIsoFormat ? DateTimeFormat.ISO.DATE_TIME : DateTimeFormat.ISO.NONE);
    }

    public void setDateStyle(String dateStyle) {
        this.factories.get(Type.DATE).setStyle(dateStyle + "-");
    }

    public void setTimeStyle(String timeStyle) {
        this.factories.get(Type.TIME).setStyle("-" + timeStyle);
    }

    public void setDateTimeStyle(String dateTimeStyle) {
        this.factories.get(Type.DATE_TIME).setStyle(dateTimeStyle);
    }

    public void setDateFormatter(DateTimeFormatter formatter) {
        this.formatters.put(Type.DATE, formatter);
    }

    public void setTimeFormatter(DateTimeFormatter formatter) {
        this.formatters.put(Type.TIME, formatter);
    }

    public void setDateTimeFormatter(DateTimeFormatter formatter) {
        this.formatters.put(Type.DATE_TIME, formatter);
    }

    @Override // org.springframework.format.FormatterRegistrar
    public void registerFormatters(FormatterRegistry registry) {
        JodaTimeConverters.registerConverters(registry);
        DateTimeFormatter dateFormatter = getFormatter(Type.DATE);
        DateTimeFormatter timeFormatter = getFormatter(Type.TIME);
        DateTimeFormatter dateTimeFormatter = getFormatter(Type.DATE_TIME);
        addFormatterForFields(registry, new ReadablePartialPrinter(dateFormatter), new LocalDateParser(dateFormatter), LocalDate.class);
        addFormatterForFields(registry, new ReadablePartialPrinter(timeFormatter), new LocalTimeParser(timeFormatter), LocalTime.class);
        addFormatterForFields(registry, new ReadablePartialPrinter(dateTimeFormatter), new LocalDateTimeParser(dateTimeFormatter), LocalDateTime.class);
        addFormatterForFields(registry, new ReadableInstantPrinter(dateTimeFormatter), new DateTimeParser(dateTimeFormatter), ReadableInstant.class);
        if (this.formatters.containsKey(Type.DATE_TIME)) {
            addFormatterForFields(registry, new ReadableInstantPrinter(dateTimeFormatter), new DateTimeParser(dateTimeFormatter), Date.class, Calendar.class);
        }
        registry.addFormatterForFieldType(Period.class, new PeriodFormatter());
        registry.addFormatterForFieldType(Duration.class, new DurationFormatter());
        registry.addFormatterForFieldType(YearMonth.class, new YearMonthFormatter());
        registry.addFormatterForFieldType(MonthDay.class, new MonthDayFormatter());
        registry.addFormatterForFieldAnnotation(new JodaDateTimeFormatAnnotationFormatterFactory());
    }

    private DateTimeFormatter getFormatter(Type type) {
        DateTimeFormatter formatter = this.formatters.get(type);
        if (formatter != null) {
            return formatter;
        }
        DateTimeFormatter fallbackFormatter = getFallbackFormatter(type);
        return this.factories.get(type).createDateTimeFormatter(fallbackFormatter);
    }

    private DateTimeFormatter getFallbackFormatter(Type type) {
        switch (type) {
            case DATE:
                return org.joda.time.format.DateTimeFormat.shortDate();
            case TIME:
                return org.joda.time.format.DateTimeFormat.shortTime();
            default:
                return org.joda.time.format.DateTimeFormat.shortDateTime();
        }
    }

    private void addFormatterForFields(FormatterRegistry registry, Printer<?> printer, Parser<?> parser, Class<?>... fieldTypes) {
        for (Class<?> fieldType : fieldTypes) {
            registry.addFormatterForFieldType(fieldType, printer, parser);
        }
    }
}