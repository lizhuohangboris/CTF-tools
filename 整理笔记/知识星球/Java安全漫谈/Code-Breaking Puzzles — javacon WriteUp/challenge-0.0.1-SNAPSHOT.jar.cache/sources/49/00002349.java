package org.springframework.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/LinkedCaseInsensitiveMap.class */
public class LinkedCaseInsensitiveMap<V> implements Map<String, V>, Serializable, Cloneable {
    private final LinkedHashMap<String, V> targetMap;
    private final HashMap<String, String> caseInsensitiveKeys;
    private final Locale locale;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    @Nullable
    public /* bridge */ /* synthetic */ Object putIfAbsent(String str, @Nullable Object obj) {
        return putIfAbsent2(str, (String) obj);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    @Nullable
    public /* bridge */ /* synthetic */ Object put(String str, @Nullable Object obj) {
        return put2(str, (String) obj);
    }

    public LinkedCaseInsensitiveMap() {
        this((Locale) null);
    }

    public LinkedCaseInsensitiveMap(@Nullable Locale locale) {
        this(16, locale);
    }

    public LinkedCaseInsensitiveMap(int initialCapacity) {
        this(initialCapacity, null);
    }

    public LinkedCaseInsensitiveMap(int initialCapacity, @Nullable Locale locale) {
        this.targetMap = new LinkedHashMap<String, V>(initialCapacity) { // from class: org.springframework.util.LinkedCaseInsensitiveMap.1
            @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
            public boolean containsKey(Object key) {
                return LinkedCaseInsensitiveMap.this.containsKey(key);
            }

            @Override // java.util.LinkedHashMap
            protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
                boolean doRemove = LinkedCaseInsensitiveMap.this.removeEldestEntry(eldest);
                if (doRemove) {
                    LinkedCaseInsensitiveMap.this.caseInsensitiveKeys.remove(LinkedCaseInsensitiveMap.this.convertKey(eldest.getKey()));
                }
                return doRemove;
            }
        };
        this.caseInsensitiveKeys = new HashMap<>(initialCapacity);
        this.locale = locale != null ? locale : Locale.getDefault();
    }

    private LinkedCaseInsensitiveMap(LinkedCaseInsensitiveMap<V> other) {
        this.targetMap = (LinkedHashMap) other.targetMap.clone();
        this.caseInsensitiveKeys = (HashMap) other.caseInsensitiveKeys.clone();
        this.locale = other.locale;
    }

    @Override // java.util.Map
    public int size() {
        return this.targetMap.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.targetMap.isEmpty();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return (key instanceof String) && this.caseInsensitiveKeys.containsKey(convertKey((String) key));
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return this.targetMap.containsValue(value);
    }

    @Override // java.util.Map
    @Nullable
    public V get(Object key) {
        String caseInsensitiveKey;
        if ((key instanceof String) && (caseInsensitiveKey = this.caseInsensitiveKeys.get(convertKey((String) key))) != null) {
            return this.targetMap.get(caseInsensitiveKey);
        }
        return null;
    }

    @Override // java.util.Map
    @Nullable
    public V getOrDefault(Object key, V defaultValue) {
        String caseInsensitiveKey;
        if ((key instanceof String) && (caseInsensitiveKey = this.caseInsensitiveKeys.get(convertKey((String) key))) != null) {
            return this.targetMap.get(caseInsensitiveKey);
        }
        return defaultValue;
    }

    @Nullable
    /* renamed from: put  reason: avoid collision after fix types in other method */
    public V put2(String key, @Nullable V value) {
        String oldKey = this.caseInsensitiveKeys.put(convertKey(key), key);
        V oldKeyValue = null;
        if (oldKey != null && !oldKey.equals(key)) {
            oldKeyValue = this.targetMap.remove(oldKey);
        }
        V oldValue = this.targetMap.put(key, value);
        return oldKeyValue != null ? oldKeyValue : oldValue;
    }

    @Override // java.util.Map
    public void putAll(Map<? extends String, ? extends V> map) {
        if (map.isEmpty()) {
            return;
        }
        map.forEach(this::put2);
    }

    @Nullable
    /* renamed from: putIfAbsent  reason: avoid collision after fix types in other method */
    public V putIfAbsent2(String key, @Nullable V value) {
        String oldKey = this.caseInsensitiveKeys.putIfAbsent(convertKey(key), key);
        if (oldKey != null) {
            return this.targetMap.get(oldKey);
        }
        return this.targetMap.putIfAbsent(key, value);
    }

    @Override // java.util.Map
    @Nullable
    public V computeIfAbsent(String key, Function<? super String, ? extends V> mappingFunction) {
        String oldKey = this.caseInsensitiveKeys.putIfAbsent(convertKey(key), key);
        if (oldKey != null) {
            return this.targetMap.get(oldKey);
        }
        return this.targetMap.computeIfAbsent(key, mappingFunction);
    }

    @Override // java.util.Map
    @Nullable
    public V remove(Object key) {
        String caseInsensitiveKey;
        if ((key instanceof String) && (caseInsensitiveKey = this.caseInsensitiveKeys.remove(convertKey((String) key))) != null) {
            return this.targetMap.remove(caseInsensitiveKey);
        }
        return null;
    }

    @Override // java.util.Map
    public void clear() {
        this.caseInsensitiveKeys.clear();
        this.targetMap.clear();
    }

    @Override // java.util.Map
    public Set<String> keySet() {
        return this.targetMap.keySet();
    }

    @Override // java.util.Map
    public Collection<V> values() {
        return this.targetMap.values();
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, V>> entrySet() {
        return this.targetMap.entrySet();
    }

    /* renamed from: clone */
    public LinkedCaseInsensitiveMap<V> m1751clone() {
        return new LinkedCaseInsensitiveMap<>(this);
    }

    @Override // java.util.Map
    public boolean equals(Object obj) {
        return this.targetMap.equals(obj);
    }

    @Override // java.util.Map
    public int hashCode() {
        return this.targetMap.hashCode();
    }

    public String toString() {
        return this.targetMap.toString();
    }

    public Locale getLocale() {
        return this.locale;
    }

    protected String convertKey(String key) {
        return key.toLowerCase(getLocale());
    }

    protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
        return false;
    }
}