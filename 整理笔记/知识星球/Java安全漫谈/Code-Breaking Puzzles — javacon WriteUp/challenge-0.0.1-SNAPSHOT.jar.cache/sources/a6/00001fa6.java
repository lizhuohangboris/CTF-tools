package org.springframework.format.datetime;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/DateTimeFormatAnnotationFormatterFactory.class */
public class DateTimeFormatAnnotationFormatterFactory extends EmbeddedValueResolutionSupport implements AnnotationFormatterFactory<DateTimeFormat> {
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
        Set<Class<?>> fieldTypes = new HashSet<>(4);
        fieldTypes.add(Date.class);
        fieldTypes.add(Calendar.class);
        fieldTypes.add(Long.class);
        FIELD_TYPES = Collections.unmodifiableSet(fieldTypes);
    }

    @Override // org.springframework.format.AnnotationFormatterFactory
    public Set<Class<?>> getFieldTypes() {
        return FIELD_TYPES;
    }

    /* renamed from: getPrinter */
    public Printer<?> getPrinter2(DateTimeFormat annotation, Class<?> fieldType) {
        return getFormatter(annotation, fieldType);
    }

    /* renamed from: getParser */
    public Parser<?> getParser2(DateTimeFormat annotation, Class<?> fieldType) {
        return getFormatter(annotation, fieldType);
    }

    protected Formatter<Date> getFormatter(DateTimeFormat annotation, Class<?> fieldType) {
        DateFormatter formatter = new DateFormatter();
        String style = resolveEmbeddedValue(annotation.style());
        if (StringUtils.hasLength(style)) {
            formatter.setStylePattern(style);
        }
        formatter.setIso(annotation.iso());
        String pattern = resolveEmbeddedValue(annotation.pattern());
        if (StringUtils.hasLength(pattern)) {
            formatter.setPattern(pattern);
        }
        return formatter;
    }
}