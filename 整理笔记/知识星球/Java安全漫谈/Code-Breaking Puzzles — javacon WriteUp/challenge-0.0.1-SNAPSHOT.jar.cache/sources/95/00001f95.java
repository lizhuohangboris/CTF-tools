package org.springframework.format;

import java.lang.annotation.Annotation;
import org.springframework.core.convert.converter.ConverterRegistry;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/FormatterRegistry.class */
public interface FormatterRegistry extends ConverterRegistry {
    void addFormatter(Formatter<?> formatter);

    void addFormatterForFieldType(Class<?> cls, Formatter<?> formatter);

    void addFormatterForFieldType(Class<?> cls, Printer<?> printer, Parser<?> parser);

    void addFormatterForFieldAnnotation(AnnotationFormatterFactory<? extends Annotation> annotationFormatterFactory);
}