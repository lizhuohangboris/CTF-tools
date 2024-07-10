package org.springframework.context;

import java.util.Locale;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/NoSuchMessageException.class */
public class NoSuchMessageException extends RuntimeException {
    public NoSuchMessageException(String code, Locale locale) {
        super("No message found under code '" + code + "' for locale '" + locale + "'.");
    }

    public NoSuchMessageException(String code) {
        super("No message found under code '" + code + "' for locale '" + Locale.getDefault() + "'.");
    }
}