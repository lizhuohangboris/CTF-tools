package org.springframework.format;

import java.lang.annotation.Annotation;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/AnnotationFormatterFactory.class */
public interface AnnotationFormatterFactory<A extends Annotation> {
    Set<Class<?>> getFieldTypes();

    Printer<?> getPrinter(A a, Class<?> cls);

    Parser<?> getParser(A a, Class<?> cls);
}