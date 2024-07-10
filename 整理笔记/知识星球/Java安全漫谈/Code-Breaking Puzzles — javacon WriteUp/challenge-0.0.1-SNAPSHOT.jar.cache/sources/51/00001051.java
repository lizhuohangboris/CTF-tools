package org.hibernate.validator.internal.constraintvalidators.hv;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.NotBlank;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/NotBlankValidator.class */
public class NotBlankValidator implements ConstraintValidator<NotBlank, CharSequence> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(CharSequence charSequence, ConstraintValidatorContext constraintValidatorContext) {
        return charSequence == null || charSequence.toString().trim().length() > 0;
    }
}