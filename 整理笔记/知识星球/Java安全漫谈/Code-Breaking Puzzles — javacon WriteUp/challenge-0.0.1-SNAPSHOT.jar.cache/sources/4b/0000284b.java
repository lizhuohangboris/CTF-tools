package org.thymeleaf.expression;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.thymeleaf.util.ListUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/Lists.class */
public final class Lists {
    public List<?> toList(Object target) {
        return ListUtils.toList(target);
    }

    public int size(List<?> target) {
        return ListUtils.size(target);
    }

    public boolean isEmpty(List<?> target) {
        return ListUtils.isEmpty(target);
    }

    public boolean contains(List<?> target, Object element) {
        return ListUtils.contains(target, element);
    }

    public boolean containsAll(List<?> target, Object[] elements) {
        return ListUtils.containsAll(target, elements);
    }

    public boolean containsAll(List<?> target, Collection<?> elements) {
        return ListUtils.containsAll(target, elements);
    }

    public <T extends Comparable<? super T>> List<T> sort(List<T> list) {
        return ListUtils.sort(list);
    }

    public <T> List<T> sort(List<T> list, Comparator<? super T> c) {
        return ListUtils.sort(list, c);
    }
}