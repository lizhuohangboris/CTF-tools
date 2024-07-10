package org.hibernate.validator.cfg;

import java.lang.annotation.Annotation;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.cfg.context.TypeConstraintMappingContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/ConstraintMapping.class */
public interface ConstraintMapping {
    <C> TypeConstraintMappingContext<C> type(Class<C> cls);

    <A extends Annotation> ConstraintDefinitionContext<A> constraintDefinition(Class<A> cls);
}