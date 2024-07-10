package org.hibernate.validator.internal.constraintvalidators.bv.notempty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotEmpty;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/notempty/NotEmptyValidatorForArraysOfByte.class */
public class NotEmptyValidatorForArraysOfByte implements ConstraintValidator<NotEmpty, byte[]> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(byte[] array, ConstraintValidatorContext constraintValidatorContext) {
        return array != null && array.length > 0;
    }
}