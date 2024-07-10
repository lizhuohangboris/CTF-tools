package org.springframework.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/NumberUtils.class */
public abstract class NumberUtils {
    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
    public static final Set<Class<?>> STANDARD_NUMBER_TYPES;

    static {
        Set<Class<?>> numberTypes = new HashSet<>(8);
        numberTypes.add(Byte.class);
        numberTypes.add(Short.class);
        numberTypes.add(Integer.class);
        numberTypes.add(Long.class);
        numberTypes.add(BigInteger.class);
        numberTypes.add(Float.class);
        numberTypes.add(Double.class);
        numberTypes.add(BigDecimal.class);
        STANDARD_NUMBER_TYPES = Collections.unmodifiableSet(numberTypes);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T extends Number> T convertNumberToTargetClass(Number number, Class<T> targetClass) throws IllegalArgumentException {
        Assert.notNull(number, "Number must not be null");
        Assert.notNull(targetClass, "Target class must not be null");
        if (targetClass.isInstance(number)) {
            return number;
        }
        if (Byte.class == targetClass) {
            long value = checkedLongValue(number, targetClass);
            if (value < -128 || value > 127) {
                raiseOverflowException(number, targetClass);
            }
            return Byte.valueOf(number.byteValue());
        } else if (Short.class == targetClass) {
            long value2 = checkedLongValue(number, targetClass);
            if (value2 < -32768 || value2 > 32767) {
                raiseOverflowException(number, targetClass);
            }
            return Short.valueOf(number.shortValue());
        } else if (Integer.class == targetClass) {
            long value3 = checkedLongValue(number, targetClass);
            if (value3 < -2147483648L || value3 > 2147483647L) {
                raiseOverflowException(number, targetClass);
            }
            return Integer.valueOf(number.intValue());
        } else if (Long.class == targetClass) {
            return Long.valueOf(checkedLongValue(number, targetClass));
        } else {
            if (BigInteger.class == targetClass) {
                if (number instanceof BigDecimal) {
                    return ((BigDecimal) number).toBigInteger();
                }
                return BigInteger.valueOf(number.longValue());
            } else if (Float.class == targetClass) {
                return Float.valueOf(number.floatValue());
            } else {
                if (Double.class == targetClass) {
                    return Double.valueOf(number.doubleValue());
                }
                if (BigDecimal.class == targetClass) {
                    return new BigDecimal(number.toString());
                }
                throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number.getClass().getName() + "] to unsupported target class [" + targetClass.getName() + "]");
            }
        }
    }

    private static long checkedLongValue(Number number, Class<? extends Number> targetClass) {
        BigInteger bigInt = null;
        if (number instanceof BigInteger) {
            bigInt = (BigInteger) number;
        } else if (number instanceof BigDecimal) {
            bigInt = ((BigDecimal) number).toBigInteger();
        }
        if (bigInt != null && (bigInt.compareTo(LONG_MIN) < 0 || bigInt.compareTo(LONG_MAX) > 0)) {
            raiseOverflowException(number, targetClass);
        }
        return number.longValue();
    }

    private static void raiseOverflowException(Number number, Class<?> targetClass) {
        throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number.getClass().getName() + "] to target class [" + targetClass.getName() + "]: overflow");
    }

    public static <T extends Number> T parseNumber(String text, Class<T> targetClass) {
        Assert.notNull(text, "Text must not be null");
        Assert.notNull(targetClass, "Target class must not be null");
        String trimmed = StringUtils.trimAllWhitespace(text);
        if (Byte.class == targetClass) {
            return isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed);
        } else if (Short.class == targetClass) {
            return isHexNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed);
        } else if (Integer.class == targetClass) {
            return isHexNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed);
        } else if (Long.class == targetClass) {
            return isHexNumber(trimmed) ? Long.decode(trimmed) : Long.valueOf(trimmed);
        } else if (BigInteger.class == targetClass) {
            return isHexNumber(trimmed) ? decodeBigInteger(trimmed) : new BigInteger(trimmed);
        } else if (Float.class == targetClass) {
            return Float.valueOf(trimmed);
        } else {
            if (Double.class == targetClass) {
                return Double.valueOf(trimmed);
            }
            if (BigDecimal.class == targetClass || Number.class == targetClass) {
                return new BigDecimal(trimmed);
            }
            throw new IllegalArgumentException("Cannot convert String [" + text + "] to target class [" + targetClass.getName() + "]");
        }
    }

    public static <T extends Number> T parseNumber(String text, Class<T> targetClass, @Nullable NumberFormat numberFormat) {
        if (numberFormat != null) {
            Assert.notNull(text, "Text must not be null");
            Assert.notNull(targetClass, "Target class must not be null");
            DecimalFormat decimalFormat = null;
            boolean resetBigDecimal = false;
            if (numberFormat instanceof DecimalFormat) {
                decimalFormat = (DecimalFormat) numberFormat;
                if (BigDecimal.class == targetClass && !decimalFormat.isParseBigDecimal()) {
                    decimalFormat.setParseBigDecimal(true);
                    resetBigDecimal = true;
                }
            }
            try {
                try {
                    Number number = numberFormat.parse(StringUtils.trimAllWhitespace(text));
                    T t = (T) convertNumberToTargetClass(number, targetClass);
                    if (resetBigDecimal) {
                        decimalFormat.setParseBigDecimal(false);
                    }
                    return t;
                } catch (ParseException ex) {
                    throw new IllegalArgumentException("Could not parse number: " + ex.getMessage());
                }
            } catch (Throwable th) {
                if (resetBigDecimal) {
                    decimalFormat.setParseBigDecimal(false);
                }
                throw th;
            }
        }
        return (T) parseNumber(text, targetClass);
    }

    private static boolean isHexNumber(String value) {
        int index = value.startsWith("-") ? 1 : 0;
        return value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index);
    }

    private static BigInteger decodeBigInteger(String value) {
        int radix = 10;
        int index = 0;
        boolean negative = false;
        if (value.startsWith("-")) {
            negative = true;
            index = 0 + 1;
        }
        if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        } else if (value.startsWith("#", index)) {
            index++;
            radix = 16;
        } else if (value.startsWith(CustomBooleanEditor.VALUE_0, index) && value.length() > 1 + index) {
            index++;
            radix = 8;
        }
        BigInteger result = new BigInteger(value.substring(index), radix);
        return negative ? result.negate() : result;
    }
}