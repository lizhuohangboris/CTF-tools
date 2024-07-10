package org.hibernate.validator.internal.metadata.raw;

import java.util.Set;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/raw/ConstrainedElement.class */
public interface ConstrainedElement extends Iterable<MetaConstraint<?>> {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/raw/ConstrainedElement$ConstrainedElementKind.class */
    public enum ConstrainedElementKind {
        TYPE,
        FIELD,
        CONSTRUCTOR,
        METHOD,
        PARAMETER
    }

    ConstrainedElementKind getKind();

    Set<MetaConstraint<?>> getConstraints();

    Set<MetaConstraint<?>> getTypeArgumentConstraints();

    CascadingMetaDataBuilder getCascadingMetaDataBuilder();

    boolean isConstrained();

    ConfigurationSource getSource();
}