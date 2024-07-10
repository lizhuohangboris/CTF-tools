package org.hibernate.validator.cfg.context;

import java.lang.annotation.ElementType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/context/PropertyTarget.class */
public interface PropertyTarget {
    PropertyConstraintMappingContext property(String str, ElementType elementType);
}