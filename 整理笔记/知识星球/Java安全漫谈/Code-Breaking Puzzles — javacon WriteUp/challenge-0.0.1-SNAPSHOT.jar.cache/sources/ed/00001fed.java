package org.springframework.format.datetime.standard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/standard/Jsr310DateTimeFormatAnnotationFormatterFactory.class */
public class Jsr310DateTimeFormatAnnotationFormatterFactory extends EmbeddedValueResolutionSupport implements AnnotationFormatterFactory<DateTimeFormat> {
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
        fieldTypes.add(LocalDate.class);
        fieldTypes.add(LocalTime.class);
        fieldTypes.add(LocalDateTime.class);
        fieldTypes.add(ZonedDateTime.class);
        fieldTypes.add(OffsetDateTime.class);
        fieldTypes.add(OffsetTime.class);
        FIELD_TYPES = Collections.unmodifiableSet(fieldTypes);
    }

    @Override // org.springframework.format.AnnotationFormatterFactory
    public final Set<Class<?>> getFieldTypes() {
        return FIELD_TYPES;
    }

    /* renamed from: getPrinter  reason: avoid collision after fix types in other method */
    public Printer<?> getPrinter2(DateTimeFormat annotation, Class<?> fieldType) {
        DateTimeFormatter formatter = getFormatter(annotation, fieldType);
        if (formatter == DateTimeFormatter.ISO_DATE) {
            if (isLocal(fieldType)) {
                formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            }
        } else if (formatter == DateTimeFormatter.ISO_TIME) {
            if (isLocal(fieldType)) {
                formatter = DateTimeFormatter.ISO_LOCAL_TIME;
            }
        } else if (formatter == DateTimeFormatter.ISO_DATE_TIME && isLocal(fieldType)) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }
        return new TemporalAccessorPrinter(formatter);
    }

    /* renamed from: getParser  reason: avoid collision after fix types in other method */
    public Parser<?> getParser2(DateTimeFormat annotation, Class<?> fieldType) {
        DateTimeFormatter formatter = getFormatter(annotation, fieldType);
        return new TemporalAccessorParser(fieldType, formatter);
    }

    protected DateTimeFormatter getFormatter(DateTimeFormat annotation, Class<?> fieldType) {
        DateTimeFormatterFactory factory = new DateTimeFormatterFactory();
        String style = resolveEmbeddedValue(annotation.style());
        if (StringUtils.hasLength(style)) {
            factory.setStylePattern(style);
        }
        factory.setIso(annotation.iso());
        String pattern = resolveEmbeddedValue(annotation.pattern());
        if (StringUtils.hasLength(pattern)) {
            factory.setPattern(pattern);
        }
        return factory.createDateTimeFormatter();
    }

    private boolean isLocal(Class<?> fieldType) {
        return fieldType.getSimpleName().startsWith("Local");
    }
}