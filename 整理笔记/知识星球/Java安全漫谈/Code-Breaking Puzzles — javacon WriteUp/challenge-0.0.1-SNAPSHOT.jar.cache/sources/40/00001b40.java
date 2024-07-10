package org.springframework.cache.concurrent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.core.serializer.support.SerializationDelegate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/concurrent/ConcurrentMapCache.class */
public class ConcurrentMapCache extends AbstractValueAdaptingCache {
    private final String name;
    private final ConcurrentMap<Object, Object> store;
    @Nullable
    private final SerializationDelegate serialization;

    public ConcurrentMapCache(String name) {
        this(name, new ConcurrentHashMap(256), true);
    }

    public ConcurrentMapCache(String name, boolean allowNullValues) {
        this(name, new ConcurrentHashMap(256), allowNullValues);
    }

    public ConcurrentMapCache(String name, ConcurrentMap<Object, Object> store, boolean allowNullValues) {
        this(name, store, allowNullValues, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ConcurrentMapCache(String name, ConcurrentMap<Object, Object> store, boolean allowNullValues, @Nullable SerializationDelegate serialization) {
        super(allowNullValues);
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(store, "Store must not be null");
        this.name = name;
        this.store = store;
        this.serialization = serialization;
    }

    public final boolean isStoreByValue() {
        return this.serialization != null;
    }

    @Override // org.springframework.cache.Cache
    public final String getName() {
        return this.name;
    }

    @Override // org.springframework.cache.Cache
    public final ConcurrentMap<Object, Object> getNativeCache() {
        return this.store;
    }

    @Override // org.springframework.cache.support.AbstractValueAdaptingCache
    @Nullable
    protected Object lookup(Object key) {
        return this.store.get(key);
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T) fromStoreValue(this.store.computeIfAbsent(key, r -> {
            try {
                return toStoreValue(valueLoader.call());
            } catch (Throwable ex) {
                throw new Cache.ValueRetrievalException(key, valueLoader, ex);
            }
        }));
    }

    @Override // org.springframework.cache.Cache
    public void put(Object key, @Nullable Object value) {
        this.store.put(key, toStoreValue(value));
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public Cache.ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        Object existing = this.store.putIfAbsent(key, toStoreValue(value));
        return toValueWrapper(existing);
    }

    @Override // org.springframework.cache.Cache
    public void evict(Object key) {
        this.store.remove(key);
    }

    @Override // org.springframework.cache.Cache
    public void clear() {
        this.store.clear();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.cache.support.AbstractValueAdaptingCache
    public Object toStoreValue(@Nullable Object userValue) {
        Object storeValue = super.toStoreValue(userValue);
        if (this.serialization != null) {
            try {
                return serializeValue(this.serialization, storeValue);
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Failed to serialize cache value '" + userValue + "'. Does it implement Serializable?", ex);
            }
        }
        return storeValue;
    }

    private Object serializeValue(SerializationDelegate serialization, Object storeValue) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            serialization.serialize(storeValue, out);
            byte[] byteArray = out.toByteArray();
            out.close();
            return byteArray;
        } catch (Throwable th) {
            out.close();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.cache.support.AbstractValueAdaptingCache
    public Object fromStoreValue(@Nullable Object storeValue) {
        if (storeValue != null && this.serialization != null) {
            try {
                return super.fromStoreValue(deserializeValue(this.serialization, storeValue));
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Failed to deserialize cache value '" + storeValue + "'", ex);
            }
        }
        return super.fromStoreValue(storeValue);
    }

    private Object deserializeValue(SerializationDelegate serialization, Object storeValue) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream((byte[]) storeValue);
        try {
            Object deserialize = serialization.deserialize(in);
            in.close();
            return deserialize;
        } catch (Throwable th) {
            in.close();
            throw th;
        }
    }
}