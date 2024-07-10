package org.hibernate.validator.cfg.context;

import java.lang.annotation.Annotation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/context/ConstraintDefinitionTarget.class */
public interface ConstraintDefinitionTarget {
    <A extends Annotation> ConstraintDefinitionContext<A> constraintDefinition(Class<A> cls);
}