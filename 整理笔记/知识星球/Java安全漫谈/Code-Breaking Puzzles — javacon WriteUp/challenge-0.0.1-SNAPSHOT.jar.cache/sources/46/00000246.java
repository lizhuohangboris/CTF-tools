package com.fasterxml.classmate.util;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.types.TypePlaceHolder;
import java.io.Serializable;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/util/ResolvedTypeCache.class */
public abstract class ResolvedTypeCache implements Serializable {
    public abstract ResolvedType find(ResolvedTypeKey resolvedTypeKey);

    public abstract int size();

    public abstract void put(ResolvedTypeKey resolvedTypeKey, ResolvedType resolvedType);

    public static ResolvedTypeCache lruCache(int maxEntries) {
        return new LRUTypeCache(maxEntries);
    }

    public static ResolvedTypeCache concurrentCache(int maxEntries) {
        return new ConcurrentTypeCache(maxEntries);
    }

    public ResolvedTypeKey key(Class<?> simpleType) {
        return new ResolvedTypeKey(simpleType);
    }

    public ResolvedTypeKey key(Class<?> simpleType, ResolvedType[] tp) {
        int len = tp == null ? 0 : tp.length;
        if (len == 0) {
            return new ResolvedTypeKey(simpleType);
        }
        for (int i = 0; i < len; i++) {
            if (tp[i] instanceof TypePlaceHolder) {
                return null;
            }
        }
        return new ResolvedTypeKey(simpleType, tp);
    }

    protected void _addForTest(ResolvedType type) {
        List<ResolvedType> tp = type.getTypeParameters();
        ResolvedType[] tpa = (ResolvedType[]) tp.toArray(new ResolvedType[tp.size()]);
        put(key(type.getErasedType(), tpa), type);
    }
}