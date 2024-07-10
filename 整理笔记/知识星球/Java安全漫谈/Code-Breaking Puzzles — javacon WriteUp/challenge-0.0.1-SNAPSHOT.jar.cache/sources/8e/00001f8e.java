package org.springframework.expression.spel.support;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.expression.TypeComparator;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.NumberUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/support/StandardTypeComparator.class */
public class StandardTypeComparator implements TypeComparator {
    @Override // org.springframework.expression.TypeComparator
    public boolean canCompare(@Nullable Object left, @Nullable Object right) {
        if (left == null || right == null) {
            return true;
        }
        if (((left instanceof Number) && (right instanceof Number)) || (left instanceof Comparable)) {
            return true;
        }
        return false;
    }

    @Override // org.springframework.expression.TypeComparator
    public int compare(@Nullable Object left, @Nullable Object right) throws SpelEvaluationException {
        if (left == null) {
            return right == null ? 0 : -1;
        } else if (right == null) {
            return 1;
        } else {
            if ((left instanceof Number) && (right instanceof Number)) {
                Number leftNumber = (Number) left;
                Number rightNumber = (Number) right;
                if ((leftNumber instanceof BigDecimal) || (rightNumber instanceof BigDecimal)) {
                    BigDecimal leftBigDecimal = (BigDecimal) NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
                    BigDecimal rightBigDecimal = (BigDecimal) NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
                    return leftBigDecimal.compareTo(rightBigDecimal);
                } else if ((leftNumber instanceof Double) || (rightNumber instanceof Double)) {
                    return Double.compare(leftNumber.doubleValue(), rightNumber.doubleValue());
                } else {
                    if ((leftNumber instanceof Float) || (rightNumber instanceof Float)) {
                        return Float.compare(leftNumber.floatValue(), rightNumber.floatValue());
                    }
                    if ((leftNumber instanceof BigInteger) || (rightNumber instanceof BigInteger)) {
                        BigInteger leftBigInteger = (BigInteger) NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
                        BigInteger rightBigInteger = (BigInteger) NumberUtils.convertNumberToTargetClass(rightNumber, BigInteger.class);
                        return leftBigInteger.compareTo(rightBigInteger);
                    } else if ((leftNumber instanceof Long) || (rightNumber instanceof Long)) {
                        return compare(leftNumber.longValue(), rightNumber.longValue());
                    } else {
                        if ((leftNumber instanceof Integer) || (rightNumber instanceof Integer)) {
                            return compare(leftNumber.intValue(), rightNumber.intValue());
                        }
                        if ((leftNumber instanceof Short) || (rightNumber instanceof Short)) {
                            return compare(leftNumber.shortValue(), rightNumber.shortValue());
                        }
                        if ((leftNumber instanceof Byte) || (rightNumber instanceof Byte)) {
                            return compare(leftNumber.byteValue(), rightNumber.byteValue());
                        }
                        return Double.compare(leftNumber.doubleValue(), rightNumber.doubleValue());
                    }
                }
            }
            try {
                if (left instanceof Comparable) {
                    return ((Comparable) left).compareTo(right);
                }
                throw new SpelEvaluationException(SpelMessage.NOT_COMPARABLE, left.getClass(), right.getClass());
            } catch (ClassCastException ex) {
                throw new SpelEvaluationException(ex, SpelMessage.NOT_COMPARABLE, left.getClass(), right.getClass());
            }
        }
    }

    private static int compare(long x, long y) {
        if (x < y) {
            return -1;
        }
        return x > y ? 1 : 0;
    }

    private static int compare(int x, int y) {
        if (x < y) {
            return -1;
        }
        return x > y ? 1 : 0;
    }

    private static int compare(short x, short y) {
        return x - y;
    }

    private static int compare(byte x, byte y) {
        return x - y;
    }
}