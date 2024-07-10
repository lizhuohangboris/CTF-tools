package org.hibernate.validator.internal.constraintvalidators.bv.number;

import java.util.OptionalInt;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/InfinityNumberComparatorHelper.class */
public final class InfinityNumberComparatorHelper {
    public static final OptionalInt LESS_THAN = OptionalInt.of(-1);
    public static final OptionalInt FINITE_VALUE = OptionalInt.empty();
    public static final OptionalInt GREATER_THAN = OptionalInt.of(1);

    private InfinityNumberComparatorHelper() {
    }

    public static OptionalInt infinityCheck(Double number, OptionalInt treatNanAs) {
        OptionalInt result = FINITE_VALUE;
        if (number.doubleValue() == Double.NEGATIVE_INFINITY) {
            result = LESS_THAN;
        } else if (number.isNaN()) {
            result = treatNanAs;
        } else if (number.doubleValue() == Double.POSITIVE_INFINITY) {
            result = GREATER_THAN;
        }
        return result;
    }

    public static OptionalInt infinityCheck(Float number, OptionalInt treatNanAs) {
        OptionalInt result = FINITE_VALUE;
        if (number.floatValue() == Float.NEGATIVE_INFINITY) {
            result = LESS_THAN;
        } else if (number.isNaN()) {
            result = treatNanAs;
        } else if (number.floatValue() == Float.POSITIVE_INFINITY) {
            result = GREATER_THAN;
        }
        return result;
    }
}