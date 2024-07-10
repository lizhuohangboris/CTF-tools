package org.hibernate.validator.internal.util.logging.formatter;

import java.util.Arrays;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/logging/formatter/ObjectArrayFormatter.class */
public class ObjectArrayFormatter {
    private final String stringRepresentation;

    public ObjectArrayFormatter(Object[] array) {
        this.stringRepresentation = Arrays.toString(array);
    }

    public String toString() {
        return this.stringRepresentation;
    }
}