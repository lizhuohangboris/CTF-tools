package org.hibernate.validator.internal.util.logging.formatter;

import java.util.Collection;
import java.util.stream.Collectors;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/logging/formatter/CollectionOfClassesObjectFormatter.class */
public class CollectionOfClassesObjectFormatter {
    private final String stringRepresentation;

    public CollectionOfClassesObjectFormatter(Collection<? extends Class<?>> classes) {
        this.stringRepresentation = (String) classes.stream().map(c -> {
            return c.getName();
        }).collect(Collectors.joining(", "));
    }

    public String toString() {
        return this.stringRepresentation;
    }
}