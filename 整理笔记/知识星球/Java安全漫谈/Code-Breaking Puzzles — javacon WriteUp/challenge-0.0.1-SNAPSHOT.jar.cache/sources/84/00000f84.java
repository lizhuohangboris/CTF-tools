package org.hibernate.validator.internal.constraintvalidators.bv;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/NotNullValidator.class */
public class NotNullValidator implements ConstraintValidator<NotNull, Object> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        return object != null;
    }
}