package org.hibernate.validator.internal.util.logging.formatter;

import java.util.Collection;
import org.hibernate.validator.internal.util.StringHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/logging/formatter/CollectionOfObjectsToStringFormatter.class */
public class CollectionOfObjectsToStringFormatter {
    private final String stringRepresentation;

    public CollectionOfObjectsToStringFormatter(Collection<?> objects) {
        this.stringRepresentation = StringHelper.join(objects, ", ");
    }

    public String toString() {
        return this.stringRepresentation;
    }
}