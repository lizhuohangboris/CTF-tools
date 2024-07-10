package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.OptionalInt;
import org.hibernate.validator.internal.constraintvalidators.bv.number.InfinityNumberComparatorHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/NumberComparatorHelper.class */
final class NumberComparatorHelper {
    private NumberComparatorHelper() {
    }

    public static int compare(BigDecimal number, long value) {
        return number.compareTo(BigDecimal.valueOf(value));
    }

    public static int compare(BigInteger number, long value) {
        return number.compareTo(BigInteger.valueOf(value));
    }

    public static int compare(Long number, long value) {
        return number.compareTo(Long.valueOf(value));
    }

    public static int compare(Number number, long value) {
        return Long.compare(number.longValue(), value);
    }

    public static int compare(Double number, long value, OptionalInt treatNanAs) {
        OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return Long.compare(number.longValue(), value);
    }

    public static int compare(Float number, long value, OptionalInt treatNanAs) {
        OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return Long.compare(number.longValue(), value);
    }
}