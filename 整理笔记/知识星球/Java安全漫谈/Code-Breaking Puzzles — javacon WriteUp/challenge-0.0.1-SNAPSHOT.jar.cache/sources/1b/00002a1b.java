package org.thymeleaf.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/SetUtils.class */
public final class SetUtils {
    public static Set<?> toSet(Object target) {
        Validate.notNull(target, "Cannot convert null to set");
        if (target instanceof Set) {
            return (Set) target;
        }
        if (target.getClass().isArray()) {
            return new LinkedHashSet(Arrays.asList((Object[]) target));
        }
        if (target instanceof Iterable) {
            Set<Object> elements = new LinkedHashSet<>();
            for (Object element : (Iterable) target) {
                elements.add(element);
            }
            return elements;
        }
        throw new IllegalArgumentException("Cannot convert object of class \"" + target.getClass().getName() + "\" to a set");
    }

    public static int size(Set<?> target) {
        Validate.notNull(target, "Cannot get set size of null");
        return target.size();
    }

    public static boolean isEmpty(Set<?> target) {
        return target == null || target.isEmpty();
    }

    public static boolean contains(Set<?> target, Object element) {
        Validate.notNull(target, "Cannot execute set contains: target is null");
        return target.contains(element);
    }

    public static boolean containsAll(Set<?> target, Object[] elements) {
        Validate.notNull(target, "Cannot execute set containsAll: target is null");
        Validate.notNull(elements, "Cannot execute set containsAll: elements is null");
        return containsAll(target, Arrays.asList(elements));
    }

    public static boolean containsAll(Set<?> target, Collection<?> elements) {
        Validate.notNull(target, "Cannot execute set contains: target is null");
        Validate.notNull(elements, "Cannot execute set containsAll: elements is null");
        return target.containsAll(elements);
    }

    public static <X> Set<X> singletonSet(X element) {
        Set<X> set = new HashSet<>(2, 1.0f);
        set.add(element);
        return Collections.unmodifiableSet(set);
    }

    private SetUtils() {
    }
}