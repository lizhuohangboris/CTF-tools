package org.thymeleaf.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ListUtils.class */
public final class ListUtils {
    public static List<?> toList(Object target) {
        Validate.notNull(target, "Cannot convert null to list");
        if (target instanceof List) {
            return (List) target;
        }
        if (target.getClass().isArray()) {
            return new ArrayList(Arrays.asList((Object[]) target));
        }
        if (target instanceof Iterable) {
            List<Object> elements = new ArrayList<>(10);
            for (Object element : (Iterable) target) {
                elements.add(element);
            }
            return elements;
        }
        throw new IllegalArgumentException("Cannot convert object of class \"" + target.getClass().getName() + "\" to a list");
    }

    public static int size(List<?> target) {
        Validate.notNull(target, "Cannot get list size of null");
        return target.size();
    }

    public static boolean isEmpty(List<?> target) {
        return target == null || target.isEmpty();
    }

    public static boolean contains(List<?> target, Object element) {
        Validate.notNull(target, "Cannot execute list contains: target is null");
        return target.contains(element);
    }

    public static boolean containsAll(List<?> target, Object[] elements) {
        Validate.notNull(target, "Cannot execute list containsAll: target is null");
        Validate.notNull(elements, "Cannot execute list containsAll: elements is null");
        return containsAll(target, Arrays.asList(elements));
    }

    public static boolean containsAll(List<?> target, Collection<?> elements) {
        Validate.notNull(target, "Cannot execute list contains: target is null");
        Validate.notNull(elements, "Cannot execute list containsAll: elements is null");
        return target.containsAll(elements);
    }

    public static <T extends Comparable<? super T>> List<T> sort(List<T> list) {
        Validate.notNull(list, "Cannot execute list sort: list is null");
        Object[] a = list.toArray();
        Arrays.sort(a);
        return fillNewList(a, list.getClass());
    }

    public static <T> List<T> sort(List<T> list, Comparator<? super T> c) {
        Validate.notNull(list, "Cannot execute list sort: list is null");
        Object[] a = list.toArray();
        Arrays.sort(a, c);
        return fillNewList(a, list.getClass());
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v14, types: [java.util.List] */
    private static <T> List<T> fillNewList(Object[] a, Class<? extends List> listType) {
        ArrayList arrayList;
        try {
            arrayList = listType.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            arrayList = new ArrayList(a.length + 2);
        }
        for (Object object : a) {
            arrayList.add(object);
        }
        return arrayList;
    }

    private ListUtils() {
    }
}