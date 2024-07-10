package org.hibernate.validator.internal.constraintvalidators.bv.money;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import javax.money.MonetaryAmount;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.DecimalMax;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/money/DecimalMaxValidatorForMonetaryAmount.class */
public class DecimalMaxValidatorForMonetaryAmount implements ConstraintValidator<DecimalMax, MonetaryAmount> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private BigDecimal maxValue;
    private boolean inclusive;

    @Override // javax.validation.ConstraintValidator
    public void initialize(DecimalMax maxValue) {
        try {
            this.maxValue = new BigDecimal(maxValue.value());
            this.inclusive = maxValue.inclusive();
        } catch (NumberFormatException nfe) {
            throw LOG.getInvalidBigDecimalFormatException(maxValue.value(), nfe);
        }
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(MonetaryAmount value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int comparisonResult = ((BigDecimal) value.getNumber().numberValueExact(BigDecimal.class)).compareTo(this.maxValue);
        return this.inclusive ? comparisonResult <= 0 : comparisonResult < 0;
    }
}