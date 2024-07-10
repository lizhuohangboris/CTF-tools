package org.springframework.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/LinkedMultiValueMap.class */
public class LinkedMultiValueMap<K, V> implements MultiValueMap<K, V>, Serializable, Cloneable {
    private static final long serialVersionUID = 3801124242820219131L;
    private final Map<K, List<V>> targetMap;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    @Nullable
    public /* bridge */ /* synthetic */ Object put(Object obj, Object obj2) {
        return put((LinkedMultiValueMap<K, V>) obj, (List) ((List) obj2));
    }

    public LinkedMultiValueMap() {
        this.targetMap = new LinkedHashMap();
    }

    public LinkedMultiValueMap(int initialCapacity) {
        this.targetMap = new LinkedHashMap(initialCapacity);
    }

    public LinkedMultiValueMap(Map<K, List<V>> otherMap) {
        this.targetMap = new LinkedHashMap(otherMap);
    }

    @Override // org.springframework.util.MultiValueMap
    @Nullable
    public V getFirst(K key) {
        List<V> values = this.targetMap.get(key);
        if (values != null) {
            return values.get(0);
        }
        return null;
    }

    @Override // org.springframework.util.MultiValueMap
    public void add(K key, @Nullable V value) {
        List<V> values = this.targetMap.computeIfAbsent(key, k -> {
            return new LinkedList();
        });
        values.add(value);
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(K key, List<? extends V> values) {
        List<V> currentValues = this.targetMap.computeIfAbsent(key, k -> {
            return new LinkedList();
        });
        currentValues.addAll(values);
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(MultiValueMap<K, V> values) {
        for (Map.Entry<K, V> entry : values.entrySet()) {
            addAll(entry.getKey(), (List) entry.getValue());
        }
    }

    @Override // org.springframework.util.MultiValueMap
    public void set(K key, @Nullable V value) {
        List<V> values = new LinkedList<>();
        values.add(value);
        this.targetMap.put(key, values);
    }

    @Override // org.springframework.util.MultiValueMap
    public void setAll(Map<K, V> values) {
        values.forEach(this::set);
    }

    @Override // org.springframework.util.MultiValueMap
    public Map<K, V> toSingleValueMap() {
        LinkedHashMap<K, V> singleValueMap = new LinkedHashMap<>(this.targetMap.size());
        this.targetMap.forEach(key, value -> {
            singleValueMap.put(key, value.get(0));
        });
        return singleValueMap;
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
        return this.targetMap.containsKey(key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return this.targetMap.containsValue(value);
    }

    @Override // java.util.Map
    @Nullable
    public List<V> get(Object key) {
        return this.targetMap.get(key);
    }

    @Nullable
    public List<V> put(K key, List<V> value) {
        return this.targetMap.put(key, value);
    }

    @Override // java.util.Map
    @Nullable
    public List<V> remove(Object key) {
        return this.targetMap.remove(key);
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends List<V>> map) {
        this.targetMap.putAll(map);
    }

    @Override // java.util.Map
    public void clear() {
        this.targetMap.clear();
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        return this.targetMap.keySet();
    }

    @Override // java.util.Map
    public Collection<List<V>> values() {
        return this.targetMap.values();
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, List<V>>> entrySet() {
        return this.targetMap.entrySet();
    }

    public LinkedMultiValueMap<K, V> deepCopy() {
        LinkedMultiValueMap<K, V> copy = new LinkedMultiValueMap<>(this.targetMap.size());
        this.targetMap.forEach(key, value -> {
            copy.put((LinkedMultiValueMap) key, (List) new LinkedList(value));
        });
        return copy;
    }

    /* renamed from: clone */
    public LinkedMultiValueMap<K, V> m1752clone() {
        return new LinkedMultiValueMap<>(this);
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
}