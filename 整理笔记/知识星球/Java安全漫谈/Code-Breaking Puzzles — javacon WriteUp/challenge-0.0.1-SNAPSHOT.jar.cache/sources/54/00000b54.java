package org.apache.logging.log4j.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/GarbageFreeSortedArrayThreadContextMap.class */
public class GarbageFreeSortedArrayThreadContextMap implements ReadOnlyThreadContextMap, ObjectThreadContextMap {
    public static final String INHERITABLE_MAP = "isThreadContextMapInheritable";
    protected static final int DEFAULT_INITIAL_CAPACITY = 16;
    protected static final String PROPERTY_NAME_INITIAL_CAPACITY = "log4j2.ThreadContext.initial.capacity";
    protected final ThreadLocal<StringMap> localMap = createThreadLocalMap();
    private static volatile int initialCapacity;
    private static volatile boolean inheritableMap;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void init() {
        PropertiesUtil properties = PropertiesUtil.getProperties();
        initialCapacity = properties.getIntegerProperty(PROPERTY_NAME_INITIAL_CAPACITY, 16);
        inheritableMap = properties.getBooleanProperty("isThreadContextMapInheritable");
    }

    static {
        init();
    }

    private ThreadLocal<StringMap> createThreadLocalMap() {
        if (inheritableMap) {
            return new InheritableThreadLocal<StringMap>() { // from class: org.apache.logging.log4j.spi.GarbageFreeSortedArrayThreadContextMap.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // java.lang.InheritableThreadLocal
                public StringMap childValue(StringMap parentValue) {
                    if (parentValue != null) {
                        return GarbageFreeSortedArrayThreadContextMap.this.createStringMap(parentValue);
                    }
                    return null;
                }
            };
        }
        return new ThreadLocal<>();
    }

    protected StringMap createStringMap() {
        return new SortedArrayStringMap(initialCapacity);
    }

    protected StringMap createStringMap(ReadOnlyStringMap original) {
        return new SortedArrayStringMap(original);
    }

    private StringMap getThreadLocalMap() {
        StringMap map = this.localMap.get();
        if (map == null) {
            map = createStringMap();
            this.localMap.set(map);
        }
        return map;
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void put(String key, String value) {
        getThreadLocalMap().putValue(key, value);
    }

    @Override // org.apache.logging.log4j.spi.ObjectThreadContextMap
    public void putValue(String key, Object value) {
        getThreadLocalMap().putValue(key, value);
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap2
    public void putAll(Map<String, String> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        StringMap map = getThreadLocalMap();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            map.putValue(entry.getKey(), entry.getValue());
        }
    }

    @Override // org.apache.logging.log4j.spi.ObjectThreadContextMap
    public <V> void putAllValues(Map<String, V> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        StringMap map = getThreadLocalMap();
        for (Map.Entry<String, V> entry : values.entrySet()) {
            map.putValue(entry.getKey(), entry.getValue());
        }
    }

    @Override // org.apache.logging.log4j.spi.ReadOnlyThreadContextMap, org.apache.logging.log4j.spi.ThreadContextMap
    public String get(String key) {
        return (String) getValue(key);
    }

    @Override // org.apache.logging.log4j.spi.ObjectThreadContextMap
    public <V> V getValue(String key) {
        StringMap map = this.localMap.get();
        if (map == null) {
            return null;
        }
        return (V) map.getValue(key);
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void remove(String key) {
        StringMap map = this.localMap.get();
        if (map != null) {
            map.remove(key);
        }
    }

    @Override // org.apache.logging.log4j.spi.CleanableThreadContextMap
    public void removeAll(Iterable<String> keys) {
        StringMap map = this.localMap.get();
        if (map != null) {
            for (String key : keys) {
                map.remove(key);
            }
        }
    }

    @Override // org.apache.logging.log4j.spi.ReadOnlyThreadContextMap, org.apache.logging.log4j.spi.ThreadContextMap
    public void clear() {
        StringMap map = this.localMap.get();
        if (map != null) {
            map.clear();
        }
    }

    @Override // org.apache.logging.log4j.spi.ReadOnlyThreadContextMap, org.apache.logging.log4j.spi.ThreadContextMap
    public boolean containsKey(String key) {
        StringMap map = this.localMap.get();
        return map != null && map.containsKey(key);
    }

    @Override // org.apache.logging.log4j.spi.ReadOnlyThreadContextMap, org.apache.logging.log4j.spi.ThreadContextMap
    public Map<String, String> getCopy() {
        StringMap map = this.localMap.get();
        return map == null ? new HashMap() : map.toMap();
    }

    @Override // org.apache.logging.log4j.spi.ReadOnlyThreadContextMap, org.apache.logging.log4j.spi.ThreadContextMap2
    public StringMap getReadOnlyContextData() {
        StringMap map = this.localMap.get();
        if (map == null) {
            map = createStringMap();
            this.localMap.set(map);
        }
        return map;
    }

    @Override // org.apache.logging.log4j.spi.ReadOnlyThreadContextMap, org.apache.logging.log4j.spi.ThreadContextMap
    public Map<String, String> getImmutableMapOrNull() {
        StringMap map = this.localMap.get();
        if (map == null) {
            return null;
        }
        return Collections.unmodifiableMap(map.toMap());
    }

    @Override // org.apache.logging.log4j.spi.ReadOnlyThreadContextMap, org.apache.logging.log4j.spi.ThreadContextMap
    public boolean isEmpty() {
        StringMap map = this.localMap.get();
        return map == null || map.size() == 0;
    }

    public String toString() {
        StringMap map = this.localMap.get();
        return map == null ? "{}" : map.toString();
    }

    public int hashCode() {
        StringMap map = this.localMap.get();
        int result = (31 * 1) + (map == null ? 0 : map.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ThreadContextMap)) {
            return false;
        }
        ThreadContextMap other = (ThreadContextMap) obj;
        Map<String, String> map = getImmutableMapOrNull();
        Map<String, String> otherMap = other.getImmutableMapOrNull();
        if (map == null) {
            if (otherMap != null) {
                return false;
            }
            return true;
        } else if (!map.equals(otherMap)) {
            return false;
        } else {
            return true;
        }
    }
}