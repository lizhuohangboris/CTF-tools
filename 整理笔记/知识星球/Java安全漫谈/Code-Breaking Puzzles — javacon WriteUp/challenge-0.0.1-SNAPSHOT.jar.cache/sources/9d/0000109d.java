package org.hibernate.validator.internal.engine.messageinterpolation;

import java.util.Formatter;
import java.util.Locale;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/messageinterpolation/FormatterWrapper.class */
public class FormatterWrapper {
    private final Formatter formatter;

    public FormatterWrapper(Locale locale) {
        this.formatter = new Formatter(locale);
    }

    public String format(String format, Object... args) {
        return this.formatter.format(format, args).toString();
    }

    public String toString() {
        return "FormatterWrapper{}";
    }
}