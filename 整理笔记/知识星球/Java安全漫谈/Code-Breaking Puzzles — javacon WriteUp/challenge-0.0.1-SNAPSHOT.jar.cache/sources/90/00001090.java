package org.hibernate.validator.internal.engine.constraintvalidation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.EnumSet;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.constraintvalidation.ValidationTarget;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/constraintvalidation/LambdaBasedValidatorDescriptor.class */
public class LambdaBasedValidatorDescriptor<A extends Annotation> implements ConstraintValidatorDescriptor<A> {
    private static final long serialVersionUID = 5129757824081595723L;
    private final Type validatedType;
    private final ConstraintDefinitionContext.ValidationCallable<?> lambda;

    public LambdaBasedValidatorDescriptor(Type validatedType, ConstraintDefinitionContext.ValidationCallable<?> lambda) {
        this.validatedType = validatedType;
        this.lambda = lambda;
    }

    @Override // org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor
    public Class<? extends ConstraintValidator<A, ?>> getValidatorClass() {
        return LambdaExecutor.class;
    }

    @Override // org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor
    public EnumSet<ValidationTarget> getValidationTargets() {
        return EnumSet.of(ValidationTarget.ANNOTATED_ELEMENT);
    }

    @Override // org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor
    public Type getValidatedType() {
        return this.validatedType;
    }

    @Override // org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor
    public ConstraintValidator<A, ?> newInstance(ConstraintValidatorFactory constraintValidatorFactory) {
        return new LambdaExecutor(this.lambda);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/constraintvalidation/LambdaBasedValidatorDescriptor$LambdaExecutor.class */
    private static class LambdaExecutor<A extends Annotation, T> implements ConstraintValidator<A, T> {
        private final ConstraintDefinitionContext.ValidationCallable<T> lambda;

        public LambdaExecutor(ConstraintDefinitionContext.ValidationCallable<T> lambda) {
            this.lambda = lambda;
        }

        @Override // javax.validation.ConstraintValidator
        public boolean isValid(T value, ConstraintValidatorContext context) {
            return this.lambda.isValid(value);
        }
    }
}