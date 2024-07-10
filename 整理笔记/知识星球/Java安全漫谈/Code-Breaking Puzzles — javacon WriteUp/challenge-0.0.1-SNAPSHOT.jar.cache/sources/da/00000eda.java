package org.hibernate.validator.cfg.context;

import java.lang.annotation.Annotation;
import javax.validation.ConstraintValidator;
import org.hibernate.validator.Incubating;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/context/ConstraintDefinitionContext.class */
public interface ConstraintDefinitionContext<A extends Annotation> extends ConstraintMappingTarget {

    @Incubating
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/context/ConstraintDefinitionContext$ConstraintValidatorDefinitionContext.class */
    public interface ConstraintValidatorDefinitionContext<A extends Annotation, T> {
        ConstraintDefinitionContext<A> with(ValidationCallable<T> validationCallable);
    }

    @FunctionalInterface
    @Incubating
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/context/ConstraintDefinitionContext$ValidationCallable.class */
    public interface ValidationCallable<T> {
        boolean isValid(T t);
    }

    ConstraintDefinitionContext<A> includeExistingValidators(boolean z);

    ConstraintDefinitionContext<A> validatedBy(Class<? extends ConstraintValidator<A, ?>> cls);

    @Incubating
    <T> ConstraintValidatorDefinitionContext<A, T> validateType(Class<T> cls);
}