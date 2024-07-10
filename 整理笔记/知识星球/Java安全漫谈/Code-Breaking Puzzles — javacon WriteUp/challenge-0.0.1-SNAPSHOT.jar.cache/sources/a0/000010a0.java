package org.hibernate.validator.internal.engine.messageinterpolation;

import java.util.Locale;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/messageinterpolation/LocalizedMessage.class */
public class LocalizedMessage {
    private final String message;
    private final Locale locale;
    private final int hashCode = buildHashCode();

    public LocalizedMessage(String message, Locale locale) {
        this.message = message;
        this.locale = locale;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocalizedMessage that = (LocalizedMessage) o;
        if (!this.message.equals(that.message) || !this.locale.equals(that.locale)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.hashCode;
    }

    private int buildHashCode() {
        int result = this.message.hashCode();
        return (31 * result) + this.locale.hashCode();
    }
}