package org.thymeleaf.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.thymeleaf.standard.expression.LiteralValue;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/EvaluationUtils.class */
public final class EvaluationUtils {
    public static boolean evaluateAsBoolean(Object condition) {
        boolean result;
        if (condition == null) {
            result = false;
        } else if (condition instanceof Boolean) {
            result = ((Boolean) condition).booleanValue();
        } else if (condition instanceof Number) {
            if (condition instanceof BigDecimal) {
                result = ((BigDecimal) condition).compareTo(BigDecimal.ZERO) != 0;
            } else if (condition instanceof BigInteger) {
                result = !condition.equals(BigInteger.ZERO);
            } else {
                result = ((Number) condition).doubleValue() != 0.0d;
            }
        } else if (condition instanceof Character) {
            result = ((Character) condition).charValue() != 0;
        } else if (condition instanceof String) {
            String condStr = ((String) condition).trim().toLowerCase();
            result = ("false".equals(condStr) || CustomBooleanEditor.VALUE_OFF.equals(condStr) || "no".equals(condStr)) ? false : true;
        } else if (condition instanceof LiteralValue) {
            String condStr2 = ((LiteralValue) condition).getValue().trim().toLowerCase();
            result = ("false".equals(condStr2) || CustomBooleanEditor.VALUE_OFF.equals(condStr2) || "no".equals(condStr2)) ? false : true;
        } else {
            result = true;
        }
        return result;
    }

    public static BigDecimal evaluateAsNumber(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Number) {
            if (object instanceof BigDecimal) {
                return (BigDecimal) object;
            }
            if (object instanceof BigInteger) {
                return new BigDecimal((BigInteger) object);
            }
            if (object instanceof Byte) {
                return new BigDecimal(((Byte) object).intValue());
            }
            if (object instanceof Short) {
                return new BigDecimal(((Short) object).intValue());
            }
            if (object instanceof Integer) {
                return new BigDecimal(((Integer) object).intValue());
            }
            if (object instanceof Long) {
                return new BigDecimal(((Long) object).longValue());
            }
            if (object instanceof Float) {
                return new BigDecimal(((Float) object).doubleValue());
            }
            if (object instanceof Double) {
                return new BigDecimal(((Double) object).doubleValue());
            }
            return null;
        } else if ((object instanceof String) && ((String) object).length() > 0) {
            char c0 = ((String) object).charAt(0);
            if ((c0 >= '0' && c0 <= '9') || c0 == '+' || c0 == '-') {
                try {
                    return new BigDecimal(((String) object).trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    public static List<Object> evaluateAsList(Object value) {
        char[] cArr;
        boolean[] zArr;
        double[] dArr;
        float[] fArr;
        long[] jArr;
        int[] iArr;
        short[] sArr;
        byte[] bArr;
        if (value == null) {
            return Collections.emptyList();
        }
        List<Object> result = new ArrayList<>();
        if (value instanceof Iterable) {
            for (Object obj : (Iterable) value) {
                result.add(obj);
            }
        } else if (value instanceof Map) {
            for (Map.Entry<Object, Object> obj2 : ((Map) value).entrySet()) {
                result.add(new MapEntry(obj2.getKey(), obj2.getValue()));
            }
        } else if (value.getClass().isArray()) {
            if (value instanceof byte[]) {
                for (byte obj3 : (byte[]) value) {
                    result.add(Byte.valueOf(obj3));
                }
            } else if (value instanceof short[]) {
                for (short obj4 : (short[]) value) {
                    result.add(Short.valueOf(obj4));
                }
            } else if (value instanceof int[]) {
                for (int obj5 : (int[]) value) {
                    result.add(Integer.valueOf(obj5));
                }
            } else if (value instanceof long[]) {
                for (long obj6 : (long[]) value) {
                    result.add(Long.valueOf(obj6));
                }
            } else if (value instanceof float[]) {
                for (float obj7 : (float[]) value) {
                    result.add(Float.valueOf(obj7));
                }
            } else if (value instanceof double[]) {
                for (double obj8 : (double[]) value) {
                    result.add(Double.valueOf(obj8));
                }
            } else if (value instanceof boolean[]) {
                for (boolean obj9 : (boolean[]) value) {
                    result.add(Boolean.valueOf(obj9));
                }
            } else if (value instanceof char[]) {
                for (char obj10 : (char[]) value) {
                    result.add(Character.valueOf(obj10));
                }
            } else {
                Object[] objValue = (Object[]) value;
                Collections.addAll(result, objValue);
            }
        } else {
            result.add(value);
        }
        return Collections.unmodifiableList(result);
    }

    public static Object[] evaluateAsArray(Object value) {
        List<Object> result = new ArrayList<>();
        if (value == null) {
            return new Object[]{null};
        }
        if (value instanceof Iterable) {
            for (Object obj : (Iterable) value) {
                result.add(obj);
            }
        } else if (value instanceof Map) {
            for (Object obj2 : ((Map) value).entrySet()) {
                result.add(obj2);
            }
        } else if (value.getClass().isArray()) {
            return (Object[]) value;
        } else {
            result.add(value);
        }
        return result.toArray(new Object[result.size()]);
    }

    private EvaluationUtils() {
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/EvaluationUtils$MapEntry.class */
    static final class MapEntry<K, V> implements Map.Entry<K, V> {
        private final K entryKey;
        private final V entryValue;

        MapEntry(K key, V value) {
            this.entryKey = key;
            this.entryValue = value;
        }

        @Override // java.util.Map.Entry
        public K getKey() {
            return this.entryKey;
        }

        @Override // java.util.Map.Entry
        public V getValue() {
            return this.entryValue;
        }

        @Override // java.util.Map.Entry
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        public String toString() {
            return this.entryKey + "=" + this.entryValue;
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry mapEntry = (Map.Entry) o;
            if (this.entryKey != null) {
                if (!this.entryKey.equals(mapEntry.getKey())) {
                    return false;
                }
            } else if (mapEntry.getKey() != null) {
                return false;
            }
            if (this.entryValue != null) {
                if (!this.entryValue.equals(mapEntry.getValue())) {
                    return false;
                }
                return true;
            } else if (mapEntry.getValue() != null) {
                return false;
            } else {
                return true;
            }
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            int result = this.entryKey != null ? this.entryKey.hashCode() : 0;
            return (31 * result) + (this.entryValue != null ? this.entryValue.hashCode() : 0);
        }
    }
}