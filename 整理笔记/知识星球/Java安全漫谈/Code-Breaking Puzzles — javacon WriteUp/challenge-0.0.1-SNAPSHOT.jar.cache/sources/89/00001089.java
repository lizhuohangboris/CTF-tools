package org.hibernate.validator.internal.engine.constraintvalidation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.EnumSet;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.constraintvalidation.ValidationTarget;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/constraintvalidation/ConstraintValidatorDescriptor.class */
public interface ConstraintValidatorDescriptor<A extends Annotation> {
    Class<? extends ConstraintValidator<A, ?>> getValidatorClass();

    EnumSet<ValidationTarget> getValidationTargets();

    Type getValidatedType();

    ConstraintValidator<A, ?> newInstance(ConstraintValidatorFactory constraintValidatorFactory);

    static <A extends Annotation> ConstraintValidatorDescriptor<A> forClass(Class<? extends ConstraintValidator<A, ?>> validatorClass, Class<? extends Annotation> constraintAnnotationType) {
        return ClassBasedValidatorDescriptor.of(validatorClass, constraintAnnotationType);
    }

    static <A extends Annotation, T> ConstraintValidatorDescriptor<A> forLambda(Class<A> annotationType, Type validatedType, ConstraintDefinitionContext.ValidationCallable<T> lambda) {
        return new LambdaBasedValidatorDescriptor(validatedType, lambda);
    }
}