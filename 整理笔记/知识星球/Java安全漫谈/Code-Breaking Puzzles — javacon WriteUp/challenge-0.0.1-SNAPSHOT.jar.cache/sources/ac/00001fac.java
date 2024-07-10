package org.springframework.format.datetime.joda;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaDateTimeFormatAnnotationFormatterFactory.class */
public class JodaDateTimeFormatAnnotationFormatterFactory extends EmbeddedValueResolutionSupport implements AnnotationFormatterFactory<DateTimeFormat> {
    private static final Set<Class<?>> FIELD_TYPES;

    @Override // org.springframework.format.AnnotationFormatterFactory
    public /* bridge */ /* synthetic */ Parser getParser(DateTimeFormat dateTimeFormat, Class cls) {
        return getParser2(dateTimeFormat, (Class<?>) cls);
    }

    @Override // org.springframework.format.AnnotationFormatterFactory
    public /* bridge */ /* synthetic */ Printer getPrinter(DateTimeFormat dateTimeFormat, Class cls) {
        return getPrinter2(dateTimeFormat, (Class<?>) cls);
    }

    static {
        Set<Class<?>> fieldTypes = new HashSet<>(8);
        fieldTypes.add(ReadableInstant.class);
        fieldTypes.add(LocalDate.class);
        fieldTypes.add(LocalTime.class);
        fieldTypes.add(LocalDateTime.class);
        fieldTypes.add(Date.class);
        fieldTypes.add(Calendar.class);
        fieldTypes.add(Long.class);
        FIELD_TYPES = Collections.unmodifiableSet(fieldTypes);
    }

    @Override // org.springframework.format.AnnotationFormatterFactory
    public final Set<Class<?>> getFieldTypes() {
        return FIELD_TYPES;
    }

    /* renamed from: getPrinter  reason: avoid collision after fix types in other method */
    public Printer<?> getPrinter2(DateTimeFormat annotation, Class<?> fieldType) {
        DateTimeFormatter formatter = getFormatter(annotation, fieldType);
        if (ReadablePartial.class.isAssignableFrom(fieldType)) {
            return new ReadablePartialPrinter(formatter);
        }
        if (ReadableInstant.class.isAssignableFrom(fieldType) || Calendar.class.isAssignableFrom(fieldType)) {
            return new ReadableInstantPrinter(formatter);
        }
        return new MillisecondInstantPrinter(formatter);
    }

    /* renamed from: getParser  reason: avoid collision after fix types in other method */
    public Parser<?> getParser2(DateTimeFormat annotation, Class<?> fieldType) {
        if (LocalDate.class == fieldType) {
            return new LocalDateParser(getFormatter(annotation, fieldType));
        }
        if (LocalTime.class == fieldType) {
            return new LocalTimeParser(getFormatter(annotation, fieldType));
        }
        if (LocalDateTime.class == fieldType) {
            return new LocalDateTimeParser(getFormatter(annotation, fieldType));
        }
        return new DateTimeParser(getFormatter(annotation, fieldType));
    }

    protected DateTimeFormatter getFormatter(DateTimeFormat annotation, Class<?> fieldType) {
        DateTimeFormatterFactory factory = new DateTimeFormatterFactory();
        String style = resolveEmbeddedValue(annotation.style());
        if (StringUtils.hasLength(style)) {
            factory.setStyle(style);
        }
        factory.setIso(annotation.iso());
        String pattern = resolveEmbeddedValue(annotation.pattern());
        if (StringUtils.hasLength(pattern)) {
            factory.setPattern(pattern);
        }
        return factory.createDateTimeFormatter();
    }
}