package com.fasterxml.jackson.datatype.jsr310;

import java.math.BigDecimal;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/DecimalUtils.class */
public final class DecimalUtils {
    private static final BigDecimal ONE_BILLION = new BigDecimal(1000000000L);

    private DecimalUtils() {
        throw new RuntimeException("DecimalUtils cannot be instantiated.");
    }

    public static String toDecimal(long seconds, int nanoseconds) {
        StringBuilder sb = new StringBuilder(20).append(seconds).append('.');
        if (nanoseconds == 0) {
            if (seconds == 0) {
                return "0.0";
            }
            sb.append("000000000");
        } else {
            StringBuilder nanoSB = new StringBuilder(9);
            nanoSB.append(nanoseconds);
            int nanosLen = nanoSB.length();
            int prepZeroes = 9 - nanosLen;
            while (prepZeroes > 0) {
                prepZeroes--;
                sb.append('0');
            }
            sb.append((CharSequence) nanoSB);
        }
        return sb.toString();
    }

    public static BigDecimal toBigDecimal(long seconds, int nanoseconds) {
        if (nanoseconds == 0) {
            if (seconds == 0) {
                return BigDecimal.ZERO.setScale(1);
            }
            return BigDecimal.valueOf(seconds).setScale(9);
        }
        return new BigDecimal(toDecimal(seconds, nanoseconds));
    }

    public static int extractNanosecondDecimal(BigDecimal value, long integer) {
        return value.subtract(new BigDecimal(integer)).multiply(ONE_BILLION).intValue();
    }
}