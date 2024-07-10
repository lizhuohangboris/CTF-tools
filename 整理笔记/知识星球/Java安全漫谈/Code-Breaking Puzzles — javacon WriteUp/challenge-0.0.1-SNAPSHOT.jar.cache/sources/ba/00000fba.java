package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.OptionalInt;
import org.hibernate.validator.internal.constraintvalidators.bv.number.InfinityNumberComparatorHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/decimal/DecimalNumberComparatorHelper.class */
final class DecimalNumberComparatorHelper {
    private DecimalNumberComparatorHelper() {
    }

    public static int compare(BigDecimal number, BigDecimal value) {
        return number.compareTo(value);
    }

    public static int compare(BigInteger number, BigDecimal value) {
        return new BigDecimal(number).compareTo(value);
    }

    public static int compare(Long number, BigDecimal value) {
        return BigDecimal.valueOf(number.longValue()).compareTo(value);
    }

    public static int compare(Number number, BigDecimal value) {
        return BigDecimal.valueOf(number.doubleValue()).compareTo(value);
    }

    public static int compare(Double number, BigDecimal value, OptionalInt treatNanAs) {
        OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return BigDecimal.valueOf(number.doubleValue()).compareTo(value);
    }

    public static int compare(Float number, BigDecimal value, OptionalInt treatNanAs) {
        OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return BigDecimal.valueOf(number.floatValue()).compareTo(value);
    }
}