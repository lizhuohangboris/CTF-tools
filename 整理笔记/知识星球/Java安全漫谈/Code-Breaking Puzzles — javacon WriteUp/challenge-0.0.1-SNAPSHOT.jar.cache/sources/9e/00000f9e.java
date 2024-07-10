package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Min;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/AbstractMinValidator.class */
public abstract class AbstractMinValidator<T> implements ConstraintValidator<Min, T> {
    protected long minValue;

    protected abstract int compare(T t);

    @Override // javax.validation.ConstraintValidator
    public void initialize(Min maxValue) {
        this.minValue = maxValue.value();
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(T value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || compare(value) >= 0;
    }
}