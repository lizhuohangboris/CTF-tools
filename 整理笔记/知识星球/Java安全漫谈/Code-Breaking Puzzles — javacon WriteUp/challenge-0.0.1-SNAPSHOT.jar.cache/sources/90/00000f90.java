package org.hibernate.validator.internal.constraintvalidators.bv.notempty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotEmpty;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/notempty/NotEmptyValidatorForArray.class */
public class NotEmptyValidatorForArray implements ConstraintValidator<NotEmpty, Object[]> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Object[] array, ConstraintValidatorContext constraintValidatorContext) {
        return array != null && array.length > 0;
    }
}