package org.thymeleaf.expression;

import java.util.Collection;
import java.util.Map;
import org.thymeleaf.util.MapUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/Maps.class */
public final class Maps {
    public int size(Map<?, ?> target) {
        return MapUtils.size(target);
    }

    public boolean isEmpty(Map<?, ?> target) {
        return MapUtils.isEmpty(target);
    }

    public <X> boolean containsKey(Map<? super X, ?> target, X key) {
        return MapUtils.containsKey(target, key);
    }

    public <X> boolean containsValue(Map<?, ? super X> target, X value) {
        return MapUtils.containsValue(target, value);
    }

    public <X> boolean containsAllKeys(Map<? super X, ?> target, X[] keys) {
        return MapUtils.containsAllKeys(target, keys);
    }

    public <X> boolean containsAllKeys(Map<? super X, ?> target, Collection<X> keys) {
        return MapUtils.containsAllKeys(target, keys);
    }

    public <X> boolean containsAllValues(Map<?, ? super X> target, X[] values) {
        return MapUtils.containsAllValues(target, values);
    }

    public <X> boolean containsAllValues(Map<?, ? super X> target, Collection<X> values) {
        return MapUtils.containsAllValues(target, values);
    }
}