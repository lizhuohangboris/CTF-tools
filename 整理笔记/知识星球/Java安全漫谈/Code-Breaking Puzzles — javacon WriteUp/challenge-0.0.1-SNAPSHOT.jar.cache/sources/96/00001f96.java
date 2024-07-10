package org.springframework.format;

import java.text.ParseException;
import java.util.Locale;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/Parser.class */
public interface Parser<T> {
    T parse(String str, Locale locale) throws ParseException;
}