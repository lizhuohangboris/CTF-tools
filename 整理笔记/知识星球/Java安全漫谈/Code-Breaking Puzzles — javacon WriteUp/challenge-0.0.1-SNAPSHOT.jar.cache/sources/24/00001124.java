package org.hibernate.validator.internal.metadata.facets;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/facets/Validatable.class */
public interface Validatable {
    Iterable<Cascadable> getCascadables();

    boolean hasCascadables();
}