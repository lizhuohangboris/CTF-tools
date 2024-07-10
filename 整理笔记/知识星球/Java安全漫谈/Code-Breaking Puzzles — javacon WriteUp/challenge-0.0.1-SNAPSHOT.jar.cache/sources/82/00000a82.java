package org.apache.el.lang;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.el.ELContext;
import javax.el.ELException;
import org.apache.el.util.MessageFactory;
import org.springframework.asm.TypeReference;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/lang/ELSupport.class */
public class ELSupport {
    private static final Long ZERO = 0L;
    protected static final boolean COERCE_TO_ZERO;

    static {
        String coerceToZeroStr;
        if (System.getSecurityManager() != null) {
            coerceToZeroStr = (String) AccessController.doPrivileged(new PrivilegedAction<String>() { // from class: org.apache.el.lang.ELSupport.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedAction
                public String run() {
                    return System.getProperty("org.apache.el.parser.COERCE_TO_ZERO", "false");
                }
            });
        } else {
            coerceToZeroStr = System.getProperty("org.apache.el.parser.COERCE_TO_ZERO", "false");
        }
        COERCE_TO_ZERO = Boolean.parseBoolean(coerceToZeroStr);
    }

    public static final int compare(ELContext ctx, Object obj0, Object obj1) throws ELException {
        if (obj0 == obj1 || equals(ctx, obj0, obj1)) {
            return 0;
        }
        if (isBigDecimalOp(obj0, obj1)) {
            BigDecimal bd0 = (BigDecimal) coerceToNumber(ctx, obj0, BigDecimal.class);
            BigDecimal bd1 = (BigDecimal) coerceToNumber(ctx, obj1, BigDecimal.class);
            return bd0.compareTo(bd1);
        } else if (isDoubleOp(obj0, obj1)) {
            Double d0 = (Double) coerceToNumber(ctx, obj0, Double.class);
            Double d1 = (Double) coerceToNumber(ctx, obj1, Double.class);
            return d0.compareTo(d1);
        } else if (isBigIntegerOp(obj0, obj1)) {
            BigInteger bi0 = (BigInteger) coerceToNumber(ctx, obj0, BigInteger.class);
            BigInteger bi1 = (BigInteger) coerceToNumber(ctx, obj1, BigInteger.class);
            return bi0.compareTo(bi1);
        } else if (isLongOp(obj0, obj1)) {
            Long l0 = (Long) coerceToNumber(ctx, obj0, Long.class);
            Long l1 = (Long) coerceToNumber(ctx, obj1, Long.class);
            return l0.compareTo(l1);
        } else if ((obj0 instanceof String) || (obj1 instanceof String)) {
            return coerceToString(ctx, obj0).compareTo(coerceToString(ctx, obj1));
        } else {
            if (obj0 instanceof Comparable) {
                Comparable<Object> comparable = (Comparable) obj0;
                if (obj1 != null) {
                    return comparable.compareTo(obj1);
                }
                return 1;
            } else if (obj1 instanceof Comparable) {
                Comparable<Object> comparable2 = (Comparable) obj1;
                if (obj0 != null) {
                    return -comparable2.compareTo(obj0);
                }
                return -1;
            } else {
                throw new ELException(MessageFactory.get("error.compare", obj0, obj1));
            }
        }
    }

    public static final boolean equals(ELContext ctx, Object obj0, Object obj1) throws ELException {
        if (obj0 == obj1) {
            return true;
        }
        if (obj0 == null || obj1 == null) {
            return false;
        }
        if (isBigDecimalOp(obj0, obj1)) {
            BigDecimal bd0 = (BigDecimal) coerceToNumber(ctx, obj0, BigDecimal.class);
            BigDecimal bd1 = (BigDecimal) coerceToNumber(ctx, obj1, BigDecimal.class);
            return bd0.equals(bd1);
        } else if (isDoubleOp(obj0, obj1)) {
            Double d0 = (Double) coerceToNumber(ctx, obj0, Double.class);
            Double d1 = (Double) coerceToNumber(ctx, obj1, Double.class);
            return d0.equals(d1);
        } else if (isBigIntegerOp(obj0, obj1)) {
            BigInteger bi0 = (BigInteger) coerceToNumber(ctx, obj0, BigInteger.class);
            BigInteger bi1 = (BigInteger) coerceToNumber(ctx, obj1, BigInteger.class);
            return bi0.equals(bi1);
        } else if (isLongOp(obj0, obj1)) {
            Long l0 = (Long) coerceToNumber(ctx, obj0, Long.class);
            Long l1 = (Long) coerceToNumber(ctx, obj1, Long.class);
            return l0.equals(l1);
        } else if ((obj0 instanceof Boolean) || (obj1 instanceof Boolean)) {
            return coerceToBoolean(ctx, obj0, false).equals(coerceToBoolean(ctx, obj1, false));
        } else {
            if (obj0.getClass().isEnum()) {
                return obj0.equals(coerceToEnum(ctx, obj1, obj0.getClass()));
            }
            if (obj1.getClass().isEnum()) {
                return obj1.equals(coerceToEnum(ctx, obj0, obj1.getClass()));
            }
            if ((obj0 instanceof String) || (obj1 instanceof String)) {
                int lexCompare = coerceToString(ctx, obj0).compareTo(coerceToString(ctx, obj1));
                return lexCompare == 0;
            }
            return obj0.equals(obj1);
        }
    }

    public static final Enum<?> coerceToEnum(ELContext ctx, Object obj, Class type) {
        if (ctx != null) {
            boolean originalIsPropertyResolved = ctx.isPropertyResolved();
            try {
                Object result = ctx.getELResolver().convertToType(ctx, obj, type);
                if (ctx.isPropertyResolved()) {
                    Enum<?> r0 = (Enum) result;
                    ctx.setPropertyResolved(originalIsPropertyResolved);
                    return r0;
                }
                ctx.setPropertyResolved(originalIsPropertyResolved);
            } catch (Throwable th) {
                ctx.setPropertyResolved(originalIsPropertyResolved);
                throw th;
            }
        }
        if (obj == null || "".equals(obj)) {
            return null;
        }
        if (type.isAssignableFrom(obj.getClass())) {
            return (Enum) obj;
        }
        if (!(obj instanceof String)) {
            throw new ELException(MessageFactory.get("error.convert", obj, obj.getClass(), type));
        }
        try {
            Enum<?> result2 = Enum.valueOf(type, (String) obj);
            return result2;
        } catch (IllegalArgumentException e) {
            throw new ELException(MessageFactory.get("error.convert", obj, obj.getClass(), type));
        }
    }

    public static final Boolean coerceToBoolean(ELContext ctx, Object obj, boolean primitive) throws ELException {
        if (ctx != null) {
            boolean originalIsPropertyResolved = ctx.isPropertyResolved();
            try {
                Object result = ctx.getELResolver().convertToType(ctx, obj, Boolean.class);
                if (ctx.isPropertyResolved()) {
                    Boolean bool = (Boolean) result;
                    ctx.setPropertyResolved(originalIsPropertyResolved);
                    return bool;
                }
                ctx.setPropertyResolved(originalIsPropertyResolved);
            } catch (Throwable th) {
                ctx.setPropertyResolved(originalIsPropertyResolved);
                throw th;
            }
        }
        if (!COERCE_TO_ZERO && !primitive && obj == null) {
            return null;
        }
        if (obj == null || "".equals(obj)) {
            return Boolean.FALSE;
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        if (obj instanceof String) {
            return Boolean.valueOf((String) obj);
        }
        throw new ELException(MessageFactory.get("error.convert", obj, obj.getClass(), Boolean.class));
    }

    private static final Character coerceToCharacter(ELContext ctx, Object obj) throws ELException {
        if (ctx != null) {
            boolean originalIsPropertyResolved = ctx.isPropertyResolved();
            try {
                Object result = ctx.getELResolver().convertToType(ctx, obj, Character.class);
                if (ctx.isPropertyResolved()) {
                    Character ch2 = (Character) result;
                    ctx.setPropertyResolved(originalIsPropertyResolved);
                    return ch2;
                }
                ctx.setPropertyResolved(originalIsPropertyResolved);
            } catch (Throwable th) {
                ctx.setPropertyResolved(originalIsPropertyResolved);
                throw th;
            }
        }
        if (obj == null || "".equals(obj)) {
            return (char) 0;
        }
        if (obj instanceof String) {
            return Character.valueOf(((String) obj).charAt(0));
        }
        if (ELArithmetic.isNumber(obj)) {
            return Character.valueOf((char) ((Number) obj).shortValue());
        }
        Class<?> objType = obj.getClass();
        if (obj instanceof Character) {
            return (Character) obj;
        }
        throw new ELException(MessageFactory.get("error.convert", obj, objType, Character.class));
    }

    protected static final Number coerceToNumber(Number number, Class<?> type) throws ELException {
        if (Long.TYPE == type || Long.class.equals(type)) {
            return Long.valueOf(number.longValue());
        }
        if (Double.TYPE == type || Double.class.equals(type)) {
            return Double.valueOf(number.doubleValue());
        }
        if (Integer.TYPE == type || Integer.class.equals(type)) {
            return Integer.valueOf(number.intValue());
        }
        if (BigInteger.class.equals(type)) {
            if (number instanceof BigDecimal) {
                return ((BigDecimal) number).toBigInteger();
            }
            if (number instanceof BigInteger) {
                return number;
            }
            return BigInteger.valueOf(number.longValue());
        } else if (BigDecimal.class.equals(type)) {
            if (number instanceof BigDecimal) {
                return number;
            }
            if (number instanceof BigInteger) {
                return new BigDecimal((BigInteger) number);
            }
            return new BigDecimal(number.doubleValue());
        } else if (Byte.TYPE == type || Byte.class.equals(type)) {
            return Byte.valueOf(number.byteValue());
        } else {
            if (Short.TYPE == type || Short.class.equals(type)) {
                return Short.valueOf(number.shortValue());
            }
            if (Float.TYPE == type || Float.class.equals(type)) {
                return Float.valueOf(number.floatValue());
            }
            if (Number.class.equals(type)) {
                return number;
            }
            throw new ELException(MessageFactory.get("error.convert", number, number.getClass(), type));
        }
    }

    public static final Number coerceToNumber(ELContext ctx, Object obj, Class<?> type) throws ELException {
        if (ctx != null) {
            boolean originalIsPropertyResolved = ctx.isPropertyResolved();
            try {
                Object result = ctx.getELResolver().convertToType(ctx, obj, type);
                if (ctx.isPropertyResolved()) {
                    Number number = (Number) result;
                    ctx.setPropertyResolved(originalIsPropertyResolved);
                    return number;
                }
                ctx.setPropertyResolved(originalIsPropertyResolved);
            } catch (Throwable th) {
                ctx.setPropertyResolved(originalIsPropertyResolved);
                throw th;
            }
        }
        if (!COERCE_TO_ZERO && obj == null && !type.isPrimitive()) {
            return null;
        }
        if (obj == null || "".equals(obj)) {
            return coerceToNumber(ZERO, type);
        }
        if (obj instanceof String) {
            return coerceToNumber((String) obj, type);
        }
        if (ELArithmetic.isNumber(obj)) {
            return coerceToNumber((Number) obj, type);
        }
        if (obj instanceof Character) {
            return coerceToNumber(Short.valueOf((short) ((Character) obj).charValue()), type);
        }
        throw new ELException(MessageFactory.get("error.convert", obj, obj.getClass(), type));
    }

    protected static final Number coerceToNumber(String val, Class<?> type) throws ELException {
        if (Long.TYPE == type || Long.class.equals(type)) {
            try {
                return Long.valueOf(val);
            } catch (NumberFormatException e) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        } else if (Integer.TYPE == type || Integer.class.equals(type)) {
            try {
                return Integer.valueOf(val);
            } catch (NumberFormatException e2) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        } else if (Double.TYPE == type || Double.class.equals(type)) {
            try {
                return Double.valueOf(val);
            } catch (NumberFormatException e3) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        } else if (BigInteger.class.equals(type)) {
            try {
                return new BigInteger(val);
            } catch (NumberFormatException e4) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        } else if (BigDecimal.class.equals(type)) {
            try {
                return new BigDecimal(val);
            } catch (NumberFormatException e5) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        } else if (Byte.TYPE == type || Byte.class.equals(type)) {
            try {
                return Byte.valueOf(val);
            } catch (NumberFormatException e6) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        } else if (Short.TYPE == type || Short.class.equals(type)) {
            try {
                return Short.valueOf(val);
            } catch (NumberFormatException e7) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        } else if (Float.TYPE == type || Float.class.equals(type)) {
            try {
                return Float.valueOf(val);
            } catch (NumberFormatException e8) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        } else {
            throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
        }
    }

    public static final String coerceToString(ELContext ctx, Object obj) {
        if (ctx != null) {
            boolean originalIsPropertyResolved = ctx.isPropertyResolved();
            try {
                Object result = ctx.getELResolver().convertToType(ctx, obj, String.class);
                if (ctx.isPropertyResolved()) {
                    String str = (String) result;
                    ctx.setPropertyResolved(originalIsPropertyResolved);
                    return str;
                }
                ctx.setPropertyResolved(originalIsPropertyResolved);
            } catch (Throwable th) {
                ctx.setPropertyResolved(originalIsPropertyResolved);
                throw th;
            }
        }
        if (obj == null) {
            return "";
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Enum) {
            return ((Enum) obj).name();
        }
        return obj.toString();
    }

    public static final Object coerceToType(ELContext ctx, Object obj, Class<?> type) throws ELException {
        if (ctx != null) {
            boolean originalIsPropertyResolved = ctx.isPropertyResolved();
            try {
                Object result = ctx.getELResolver().convertToType(ctx, obj, type);
                if (ctx.isPropertyResolved()) {
                    return result;
                }
                ctx.setPropertyResolved(originalIsPropertyResolved);
            } finally {
                ctx.setPropertyResolved(originalIsPropertyResolved);
            }
        }
        if (type == null || Object.class.equals(type) || (obj != null && type.isAssignableFrom(obj.getClass()))) {
            return obj;
        }
        if (!COERCE_TO_ZERO && obj == null && !type.isPrimitive() && !String.class.isAssignableFrom(type)) {
            return null;
        }
        if (String.class.equals(type)) {
            return coerceToString(ctx, obj);
        }
        if (ELArithmetic.isNumberType(type)) {
            return coerceToNumber(ctx, obj, type);
        }
        if (Character.class.equals(type) || Character.TYPE == type) {
            return coerceToCharacter(ctx, obj);
        }
        if (Boolean.class.equals(type) || Boolean.TYPE == type) {
            return coerceToBoolean(ctx, obj, Boolean.TYPE == type);
        } else if (type.isEnum()) {
            return coerceToEnum(ctx, obj, type);
        } else {
            if (obj == null) {
                return null;
            }
            if (obj instanceof String) {
                PropertyEditor editor = PropertyEditorManager.findEditor(type);
                if (editor == null) {
                    if ("".equals(obj)) {
                        return null;
                    }
                    throw new ELException(MessageFactory.get("error.convert", obj, obj.getClass(), type));
                }
                try {
                    editor.setAsText((String) obj);
                    return editor.getValue();
                } catch (RuntimeException e) {
                    if ("".equals(obj)) {
                        return null;
                    }
                    throw new ELException(MessageFactory.get("error.convert", obj, obj.getClass(), type), e);
                }
            } else if ((obj instanceof Set) && type == Map.class && ((Set) obj).isEmpty()) {
                return Collections.EMPTY_MAP;
            } else {
                if (type.isArray() && obj.getClass().isArray()) {
                    return coerceToArray(ctx, obj, type);
                }
                throw new ELException(MessageFactory.get("error.convert", obj, obj.getClass(), type));
            }
        }
    }

    private static Object coerceToArray(ELContext ctx, Object obj, Class<?> type) {
        int size = Array.getLength(obj);
        Class<?> componentType = type.getComponentType();
        Object result = Array.newInstance(componentType, size);
        for (int i = 0; i < size; i++) {
            Array.set(result, i, coerceToType(ctx, Array.get(obj, i), componentType));
        }
        return result;
    }

    public static final boolean isBigDecimalOp(Object obj0, Object obj1) {
        return (obj0 instanceof BigDecimal) || (obj1 instanceof BigDecimal);
    }

    public static final boolean isBigIntegerOp(Object obj0, Object obj1) {
        return (obj0 instanceof BigInteger) || (obj1 instanceof BigInteger);
    }

    public static final boolean isDoubleOp(Object obj0, Object obj1) {
        return (obj0 instanceof Double) || (obj1 instanceof Double) || (obj0 instanceof Float) || (obj1 instanceof Float);
    }

    public static final boolean isLongOp(Object obj0, Object obj1) {
        return (obj0 instanceof Long) || (obj1 instanceof Long) || (obj0 instanceof Integer) || (obj1 instanceof Integer) || (obj0 instanceof Character) || (obj1 instanceof Character) || (obj0 instanceof Short) || (obj1 instanceof Short) || (obj0 instanceof Byte) || (obj1 instanceof Byte);
    }

    public static final boolean isStringFloat(String str) {
        int len = str.length();
        if (len > 1) {
            for (int i = 0; i < len; i++) {
                switch (str.charAt(i)) {
                    case '.':
                        return true;
                    case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
                        return true;
                    case 'e':
                        return true;
                    default:
                }
            }
            return false;
        }
        return false;
    }
}