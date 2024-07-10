package org.thymeleaf.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ArrayUtils.class */
public final class ArrayUtils {
    public static Object[] toArray(Object target) {
        return toArray(null, target);
    }

    public static Object[] toStringArray(Object target) {
        return toArray(String.class, target);
    }

    public static Object[] toIntegerArray(Object target) {
        return toArray(Integer.class, target);
    }

    public static Object[] toLongArray(Object target) {
        return toArray(Long.class, target);
    }

    public static Object[] toDoubleArray(Object target) {
        return toArray(Double.class, target);
    }

    public static Object[] toFloatArray(Object target) {
        return toArray(Float.class, target);
    }

    public static Object[] toBooleanArray(Object target) {
        return toArray(Boolean.class, target);
    }

    public static int length(Object[] target) {
        Validate.notNull(target, "Cannot get array length of null");
        return target.length;
    }

    public static boolean isEmpty(Object[] target) {
        return target == null || target.length <= 0;
    }

    public static boolean contains(Object[] target, Object element) {
        Validate.notNull(target, "Cannot execute array contains: target is null");
        if (element == null) {
            for (Object targetElement : target) {
                if (targetElement == null) {
                    return true;
                }
            }
            return false;
        }
        for (Object targetElement2 : target) {
            if (element.equals(targetElement2)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsAll(Object[] target, Object[] elements) {
        Validate.notNull(target, "Cannot execute array containsAll: target is null");
        Validate.notNull(elements, "Cannot execute array containsAll: elements is null");
        return containsAll(target, Arrays.asList(elements));
    }

    public static boolean containsAll(Object[] target, Collection<?> elements) {
        Validate.notNull(target, "Cannot execute array contains: target is null");
        Validate.notNull(elements, "Cannot execute array containsAll: elements is null");
        Set<?> remainingElements = new HashSet<>((Collection<? extends Object>) elements);
        remainingElements.removeAll(Arrays.asList(target));
        return remainingElements.isEmpty();
    }

    private static Object[] toArray(Class<?> componentClass, Object target) {
        Validate.notNull(target, "Cannot convert null to array");
        if (target.getClass().isArray()) {
            if (componentClass == null) {
                return (Object[]) target;
            }
            Class<?> targetComponentClass = target.getClass().getComponentType();
            if (componentClass.isAssignableFrom(targetComponentClass)) {
                return (Object[]) target;
            }
            throw new IllegalArgumentException("Cannot convert object of class \"" + targetComponentClass.getName() + "[]\" to an array of " + componentClass.getClass().getSimpleName());
        } else if (target instanceof Iterable) {
            Class<?> computedComponentClass = null;
            Iterable<?> iterableTarget = (Iterable) target;
            List<Object> elements = new ArrayList<>(5);
            for (Object element : iterableTarget) {
                if (componentClass == null && element != null) {
                    if (computedComponentClass == null) {
                        computedComponentClass = element.getClass();
                    } else if (!computedComponentClass.equals(Object.class) && !computedComponentClass.equals(element.getClass())) {
                        computedComponentClass = Object.class;
                    }
                }
                elements.add(element);
            }
            if (computedComponentClass == null) {
                computedComponentClass = componentClass != null ? componentClass : Object.class;
            }
            Object[] result = (Object[]) Array.newInstance(computedComponentClass, elements.size());
            return elements.toArray(result);
        } else {
            throw new IllegalArgumentException("Cannot convert object of class \"" + target.getClass().getName() + "\" to an array" + (componentClass == null ? "" : " of " + componentClass.getClass().getSimpleName()));
        }
    }

    public static <T, X> X[] copyOf(T[] original, int newLength, Class<? extends X[]> newType) {
        Object[] objArr;
        if (newType == Object[].class) {
            objArr = new Object[newLength];
        } else {
            objArr = (Object[]) Array.newInstance(newType.getComponentType(), newLength);
        }
        X[] newArray = (X[]) objArr;
        System.arraycopy(original, 0, newArray, 0, Math.min(original.length, newLength));
        return newArray;
    }

    public static <T> T[] copyOf(T[] original, int newLength) {
        return (T[]) copyOf(original, newLength, original.getClass());
    }

    public static char[] copyOf(char[] original, int newLength) {
        char[] copy = new char[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static char[] copyOfRange(char[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0) {
            throw new IllegalArgumentException("Cannot copy array range with indexes " + from + " and " + to);
        }
        char[] copy = new char[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    private ArrayUtils() {
    }
}