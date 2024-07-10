package org.springframework.core.env;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/ReadOnlySystemAttributesMap.class */
abstract class ReadOnlySystemAttributesMap implements Map<String, String> {
    @Nullable
    protected abstract String getSystemAttribute(String str);

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Map
    @Nullable
    public String get(Object key) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("Type of key [" + key.getClass().getName() + "] must be java.lang.String");
        }
        return getSystemAttribute((String) key);
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return false;
    }

    @Override // java.util.Map
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public String put(String key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Map
    public String remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public Set<String> keySet() {
        return Collections.emptySet();
    }

    @Override // java.util.Map
    public void putAll(Map<? extends String, ? extends String> map) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public Collection<String> values() {
        return Collections.emptySet();
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, String>> entrySet() {
        return Collections.emptySet();
    }
}