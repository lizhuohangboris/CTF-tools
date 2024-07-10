package org.hibernate.validator.internal.constraintvalidators.bv.number.sign;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.OptionalInt;
import org.hibernate.validator.internal.constraintvalidators.bv.number.InfinityNumberComparatorHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/sign/NumberSignHelper.class */
final class NumberSignHelper {
    private static final short SHORT_ZERO = 0;
    private static final byte BYTE_ZERO = 0;

    private NumberSignHelper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int signum(Long number) {
        return Long.signum(number.longValue());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int signum(Integer number) {
        return Integer.signum(number.intValue());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int signum(Short number) {
        return number.compareTo((Short) 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int signum(Byte number) {
        return number.compareTo((Byte) (byte) 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int signum(BigInteger number) {
        return number.signum();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int signum(BigDecimal number) {
        return number.signum();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int signum(Number value) {
        return Double.compare(value.doubleValue(), 0.0d);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int signum(Float number, OptionalInt treatNanAs) {
        OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return number.compareTo(Float.valueOf(0.0f));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int signum(Double number, OptionalInt treatNanAs) {
        OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return number.compareTo(Double.valueOf(0.0d));
    }
}