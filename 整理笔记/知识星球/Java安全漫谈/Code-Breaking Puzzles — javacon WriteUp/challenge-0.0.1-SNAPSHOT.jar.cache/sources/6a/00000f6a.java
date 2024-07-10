package org.hibernate.validator.internal.cfg.context;

import java.lang.annotation.Annotation;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.cfg.context.TypeConstraintMappingContext;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/ConstraintContextImplBase.class */
public abstract class ConstraintContextImplBase {
    protected final DefaultConstraintMapping mapping;

    public ConstraintContextImplBase(DefaultConstraintMapping mapping) {
        this.mapping = mapping;
    }

    public <C> TypeConstraintMappingContext<C> type(Class<C> type) {
        return this.mapping.type(type);
    }

    public <A extends Annotation> ConstraintDefinitionContext<A> constraintDefinition(Class<A> annotationClass) {
        return this.mapping.constraintDefinition(annotationClass);
    }
}