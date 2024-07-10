package org.thymeleaf.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/AggregateUtils.class */
public final class AggregateUtils {
    public static BigDecimal sum(Iterable<? extends Number> target) {
        Validate.notNull(target, "Cannot aggregate on null");
        Validate.containsNoNulls(target, "Cannot aggregate on iterable containing nulls");
        BigDecimal total = BigDecimal.ZERO;
        int size = 0;
        for (Number element : target) {
            total = total.add(toBigDecimal(element));
            size++;
        }
        if (size == 0) {
            return null;
        }
        return total;
    }

    public static BigDecimal sum(Object[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        Validate.containsNoNulls(target, "Cannot aggregate on array containing nulls");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (Object element : target) {
            total = total.add(toBigDecimal((Number) element));
        }
        return total;
    }

    public static BigDecimal sum(byte[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (byte element : target) {
            total = total.add(toBigDecimal(element));
        }
        return total;
    }

    public static BigDecimal sum(short[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (short element : target) {
            total = total.add(toBigDecimal(element));
        }
        return total;
    }

    public static BigDecimal sum(int[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (int element : target) {
            total = total.add(toBigDecimal(element));
        }
        return total;
    }

    public static BigDecimal sum(long[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (long element : target) {
            total = total.add(toBigDecimal(element));
        }
        return total;
    }

    public static BigDecimal sum(float[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (float element : target) {
            total = total.add(toBigDecimal(element));
        }
        return total;
    }

    public static BigDecimal sum(double[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (double element : target) {
            total = total.add(toBigDecimal(element));
        }
        return total;
    }

    public static BigDecimal avg(Iterable<? extends Number> target) {
        Validate.notNull(target, "Cannot aggregate on null");
        Validate.containsNoNulls(target, "Cannot aggregate on array containing nulls");
        BigDecimal total = BigDecimal.ZERO;
        int size = 0;
        for (Number element : target) {
            total = total.add(toBigDecimal(element));
            size++;
        }
        if (size == 0) {
            return null;
        }
        BigDecimal divisor = BigDecimal.valueOf(size);
        try {
            return total.divide(divisor);
        } catch (ArithmeticException e) {
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }

    public static BigDecimal avg(Object[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        Validate.containsNoNulls(target, "Cannot aggregate on array containing nulls");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (Object element : target) {
            total = total.add(toBigDecimal((Number) element));
        }
        BigDecimal divisor = BigDecimal.valueOf(target.length);
        try {
            return total.divide(divisor);
        } catch (ArithmeticException e) {
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }

    public static BigDecimal avg(byte[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (byte element : target) {
            total = total.add(toBigDecimal(element));
        }
        BigDecimal divisor = BigDecimal.valueOf(target.length);
        try {
            return total.divide(divisor);
        } catch (ArithmeticException e) {
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }

    public static BigDecimal avg(short[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (short element : target) {
            total = total.add(toBigDecimal(element));
        }
        BigDecimal divisor = BigDecimal.valueOf(target.length);
        try {
            return total.divide(divisor);
        } catch (ArithmeticException e) {
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }

    public static BigDecimal avg(int[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (int element : target) {
            total = total.add(toBigDecimal(element));
        }
        BigDecimal divisor = BigDecimal.valueOf(target.length);
        try {
            return total.divide(divisor);
        } catch (ArithmeticException e) {
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }

    public static BigDecimal avg(long[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (long element : target) {
            total = total.add(toBigDecimal(element));
        }
        BigDecimal divisor = BigDecimal.valueOf(target.length);
        try {
            return total.divide(divisor);
        } catch (ArithmeticException e) {
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }

    public static BigDecimal avg(float[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (float element : target) {
            total = total.add(toBigDecimal(element));
        }
        BigDecimal divisor = BigDecimal.valueOf(target.length);
        try {
            return total.divide(divisor);
        } catch (ArithmeticException e) {
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }

    public static BigDecimal avg(double[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (double element : target) {
            total = total.add(toBigDecimal(element));
        }
        BigDecimal divisor = BigDecimal.valueOf(target.length);
        try {
            return total.divide(divisor);
        } catch (ArithmeticException e) {
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }

    private static BigDecimal toBigDecimal(Number number) {
        Validate.notNull(number, "Cannot convert null to BigDecimal");
        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        }
        if (number instanceof BigInteger) {
            return new BigDecimal((BigInteger) number);
        }
        if ((number instanceof Byte) || (number instanceof Short) || (number instanceof Integer) || (number instanceof Long)) {
            return BigDecimal.valueOf(number.longValue());
        }
        return BigDecimal.valueOf(number.doubleValue());
    }

    private static BigDecimal toBigDecimal(byte number) {
        return BigDecimal.valueOf(number);
    }

    private static BigDecimal toBigDecimal(short number) {
        return BigDecimal.valueOf(number);
    }

    private static BigDecimal toBigDecimal(int number) {
        return BigDecimal.valueOf(number);
    }

    private static BigDecimal toBigDecimal(long number) {
        return BigDecimal.valueOf(number);
    }

    private static BigDecimal toBigDecimal(float number) {
        return BigDecimal.valueOf(number);
    }

    private static BigDecimal toBigDecimal(double number) {
        return BigDecimal.valueOf(number);
    }

    private AggregateUtils() {
    }
}