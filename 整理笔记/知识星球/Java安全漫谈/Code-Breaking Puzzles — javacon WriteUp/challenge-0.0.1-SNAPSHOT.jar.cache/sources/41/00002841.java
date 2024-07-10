package org.thymeleaf.expression;

import java.util.Collection;
import org.thymeleaf.util.ArrayUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/Arrays.class */
public final class Arrays {
    public Object[] toArray(Object target) {
        return ArrayUtils.toArray(target);
    }

    public Object[] toStringArray(Object target) {
        return ArrayUtils.toStringArray(target);
    }

    public Object[] toIntegerArray(Object target) {
        return ArrayUtils.toIntegerArray(target);
    }

    public Object[] toLongArray(Object target) {
        return ArrayUtils.toLongArray(target);
    }

    public Object[] toDoubleArray(Object target) {
        return ArrayUtils.toDoubleArray(target);
    }

    public Object[] toFloatArray(Object target) {
        return ArrayUtils.toFloatArray(target);
    }

    public Object[] toBooleanArray(Object target) {
        return ArrayUtils.toBooleanArray(target);
    }

    public int length(Object[] target) {
        return ArrayUtils.length(target);
    }

    public boolean isEmpty(Object[] target) {
        return ArrayUtils.isEmpty(target);
    }

    public boolean contains(Object[] target, Object element) {
        return ArrayUtils.contains(target, element);
    }

    public boolean containsAll(Object[] target, Object[] elements) {
        return ArrayUtils.containsAll(target, elements);
    }

    public boolean containsAll(Object[] target, Collection<?> elements) {
        return ArrayUtils.containsAll(target, elements);
    }
}