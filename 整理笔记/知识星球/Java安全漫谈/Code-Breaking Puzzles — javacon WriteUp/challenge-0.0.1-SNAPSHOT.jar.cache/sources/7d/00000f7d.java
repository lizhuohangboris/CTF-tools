package org.hibernate.validator.internal.constraintvalidators.bv;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.DecimalMin;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/DecimalMinValidatorForCharSequence.class */
public class DecimalMinValidatorForCharSequence implements ConstraintValidator<DecimalMin, CharSequence> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private BigDecimal minValue;
    private boolean inclusive;

    @Override // javax.validation.ConstraintValidator
    public void initialize(DecimalMin minValue) {
        try {
            this.minValue = new BigDecimal(minValue.value());
            this.inclusive = minValue.inclusive();
        } catch (NumberFormatException nfe) {
            throw LOG.getInvalidBigDecimalFormatException(minValue.value(), nfe);
        }
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        try {
            int comparisonResult = new BigDecimal(value.toString()).compareTo(this.minValue);
            return this.inclusive ? comparisonResult >= 0 : comparisonResult > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}