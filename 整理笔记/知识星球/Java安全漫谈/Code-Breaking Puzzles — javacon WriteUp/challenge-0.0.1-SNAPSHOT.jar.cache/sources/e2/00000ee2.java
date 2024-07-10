package org.hibernate.validator.cfg.context;

import org.hibernate.validator.Incubating;

@Incubating
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/context/ContainerElementTarget.class */
public interface ContainerElementTarget {
    ContainerElementConstraintMappingContext containerElementType();

    ContainerElementConstraintMappingContext containerElementType(int i, int... iArr);
}