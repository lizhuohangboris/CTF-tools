package org.hibernate.validator.internal.constraintvalidators.bv.money;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import javax.money.MonetaryAmount;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.DecimalMin;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/money/DecimalMinValidatorForMonetaryAmount.class */
public class DecimalMinValidatorForMonetaryAmount implements ConstraintValidator<DecimalMin, MonetaryAmount> {
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
    public boolean isValid(MonetaryAmount value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int comparisonResult = ((BigDecimal) value.getNumber().numberValueExact(BigDecimal.class)).compareTo(this.minValue);
        return this.inclusive ? comparisonResult >= 0 : comparisonResult > 0;
    }
}