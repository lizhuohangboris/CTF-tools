package org.hibernate.validator.internal.constraintvalidators.bv.number.sign;

import java.math.BigDecimal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.PositiveOrZero;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/sign/PositiveOrZeroValidatorForBigDecimal.class */
public class PositiveOrZeroValidatorForBigDecimal implements ConstraintValidator<PositiveOrZero, BigDecimal> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        return value == null || NumberSignHelper.signum(value) >= 0;
    }
}