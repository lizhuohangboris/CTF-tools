package org.hibernate.validator.internal.util.logging.formatter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/logging/formatter/ClassObjectFormatter.class */
public class ClassObjectFormatter {
    private final String stringRepresentation;

    public ClassObjectFormatter(Class<?> clazz) {
        this.stringRepresentation = clazz.getName();
    }

    public String toString() {
        return this.stringRepresentation;
    }
}