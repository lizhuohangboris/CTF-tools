package org.springframework.format;

import java.util.Locale;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/Printer.class */
public interface Printer<T> {
    String print(T t, Locale locale);
}