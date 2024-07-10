package org.thymeleaf.expression;

import java.util.Collection;
import java.util.Set;
import org.thymeleaf.util.SetUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/Sets.class */
public final class Sets {
    public Set<?> toSet(Object target) {
        return SetUtils.toSet(target);
    }

    public int size(Set<?> target) {
        return SetUtils.size(target);
    }

    public boolean isEmpty(Set<?> target) {
        return SetUtils.isEmpty(target);
    }

    public boolean contains(Set<?> target, Object element) {
        return SetUtils.contains(target, element);
    }

    public boolean containsAll(Set<?> target, Object[] elements) {
        return SetUtils.containsAll(target, elements);
    }

    public boolean containsAll(Set<?> target, Collection<?> elements) {
        return SetUtils.containsAll(target, elements);
    }
}