package org.hibernate.validator.internal.util.logging.formatter;

import java.lang.reflect.Type;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/logging/formatter/TypeFormatter.class */
public class TypeFormatter {
    private final String stringRepresentation;

    public TypeFormatter(Type type) {
        this.stringRepresentation = type.getTypeName();
    }

    public String toString() {
        return this.stringRepresentation;
    }
}