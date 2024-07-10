package com.fasterxml.classmate.util;

import com.fasterxml.classmate.ResolvedType;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/util/ConcurrentTypeCache.class */
public class ConcurrentTypeCache extends ResolvedTypeCache {
    private static final long serialVersionUID = 1;
    protected final int _maxEntries;
    protected final transient ConcurrentHashMap<ResolvedTypeKey, ResolvedType> _map;

    public ConcurrentTypeCache(int maxEntries) {
        this._map = new ConcurrentHashMap<>(maxEntries, 0.8f, 4);
        this._maxEntries = maxEntries;
    }

    Object readResolve() {
        return new ConcurrentTypeCache(this._maxEntries);
    }

    @Override // com.fasterxml.classmate.util.ResolvedTypeCache
    public ResolvedType find(ResolvedTypeKey key) {
        if (key == null) {
            throw new IllegalArgumentException("Null key not allowed");
        }
        return this._map.get(key);
    }

    @Override // com.fasterxml.classmate.util.ResolvedTypeCache
    public int size() {
        return this._map.size();
    }

    @Override // com.fasterxml.classmate.util.ResolvedTypeCache
    public void put(ResolvedTypeKey key, ResolvedType type) {
        if (key == null) {
            throw new IllegalArgumentException("Null key not allowed");
        }
        if (this._map.size() >= this._maxEntries) {
            synchronized (this) {
                if (this._map.size() >= this._maxEntries) {
                    this._map.clear();
                }
            }
        }
        this._map.put(key, type);
    }
}