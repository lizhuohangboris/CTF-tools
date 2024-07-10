package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Max;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/AbstractMaxValidator.class */
public abstract class AbstractMaxValidator<T> implements ConstraintValidator<Max, T> {
    protected long maxValue;

    protected abstract int compare(T t);

    @Override // javax.validation.ConstraintValidator
    public void initialize(Max maxValue) {
        this.maxValue = maxValue.value();
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(T value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || compare(value) <= 0;
    }
}