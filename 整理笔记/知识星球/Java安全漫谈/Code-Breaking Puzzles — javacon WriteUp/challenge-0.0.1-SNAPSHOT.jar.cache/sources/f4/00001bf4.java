package org.springframework.cglib.core.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/internal/LoadingCache.class */
public class LoadingCache<K, KK, V> {
    protected final ConcurrentMap<KK, Object> map = new ConcurrentHashMap();
    protected final Function<K, V> loader;
    protected final Function<K, KK> keyMapper;
    public static final Function IDENTITY = new Function() { // from class: org.springframework.cglib.core.internal.LoadingCache.1
        @Override // org.springframework.cglib.core.internal.Function
        public Object apply(Object key) {
            return key;
        }
    };

    public LoadingCache(Function<K, KK> keyMapper, Function<K, V> loader) {
        this.keyMapper = keyMapper;
        this.loader = loader;
    }

    public static <K> Function<K, K> identity() {
        return IDENTITY;
    }

    public V get(K key) {
        KK cacheKey = this.keyMapper.apply(key);
        V v = (V) this.map.get(cacheKey);
        if (v != null && !(v instanceof FutureTask)) {
            return v;
        }
        return createEntry(key, cacheKey, v);
    }

    protected V createEntry(final K key, KK cacheKey, Object v) {
        FutureTask<V> task;
        boolean creator = false;
        if (v != null) {
            task = (FutureTask) v;
        } else {
            task = new FutureTask<>(new Callable<V>() { // from class: org.springframework.cglib.core.internal.LoadingCache.2
                /* JADX WARN: Multi-variable type inference failed */
                @Override // java.util.concurrent.Callable
                public V call() throws Exception {
                    return (V) LoadingCache.this.loader.apply(key);
                }
            });
            V v2 = (V) this.map.putIfAbsent(cacheKey, task);
            if (v2 == null) {
                creator = true;
                task.run();
            } else if (v2 instanceof FutureTask) {
                task = (FutureTask) v2;
            } else {
                return v2;
            }
        }
        try {
            V result = task.get();
            if (creator) {
                this.map.put(cacheKey, result);
            }
            return result;
        } catch (InterruptedException e) {
            throw new IllegalStateException("Interrupted while loading cache item", e);
        } catch (ExecutionException e2) {
            Throwable cause = e2.getCause();
            if (cause instanceof RuntimeException) {
                throw ((RuntimeException) cause);
            }
            throw new IllegalStateException("Unable to load cache item", cause);
        }
    }
}