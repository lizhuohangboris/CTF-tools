package com.fasterxml.classmate.util;

import com.fasterxml.classmate.ResolvedType;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/util/LRUTypeCache.class */
public class LRUTypeCache extends ResolvedTypeCache {
    private static final long serialVersionUID = 1;
    protected final int _maxEntries;
    protected final transient CacheMap _map;

    public LRUTypeCache(int maxEntries) {
        this._map = new CacheMap(maxEntries);
        this._maxEntries = maxEntries;
    }

    Object readResolve() {
        return new LRUTypeCache(this._maxEntries);
    }

    @Override // com.fasterxml.classmate.util.ResolvedTypeCache
    public synchronized ResolvedType find(ResolvedTypeKey key) {
        if (key == null) {
            throw new IllegalArgumentException("Null key not allowed");
        }
        return this._map.get(key);
    }

    @Override // com.fasterxml.classmate.util.ResolvedTypeCache
    public synchronized int size() {
        return this._map.size();
    }

    @Override // com.fasterxml.classmate.util.ResolvedTypeCache
    public synchronized void put(ResolvedTypeKey key, ResolvedType type) {
        if (key == null) {
            throw new IllegalArgumentException("Null key not allowed");
        }
        this._map.put(key, type);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/util/LRUTypeCache$CacheMap.class */
    private static final class CacheMap extends LinkedHashMap<ResolvedTypeKey, ResolvedType> {
        protected final int _maxEntries;

        public CacheMap(int maxEntries) {
            this._maxEntries = maxEntries;
        }

        @Override // java.util.LinkedHashMap
        protected boolean removeEldestEntry(Map.Entry<ResolvedTypeKey, ResolvedType> eldest) {
            return size() > this._maxEntries;
        }
    }
}