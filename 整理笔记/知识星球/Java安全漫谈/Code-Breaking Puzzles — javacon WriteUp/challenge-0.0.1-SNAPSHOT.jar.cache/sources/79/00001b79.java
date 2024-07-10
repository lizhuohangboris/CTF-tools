package org.springframework.cache.support;

import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/support/AbstractValueAdaptingCache.class */
public abstract class AbstractValueAdaptingCache implements Cache {
    private final boolean allowNullValues;

    @Nullable
    protected abstract Object lookup(Object obj);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractValueAdaptingCache(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public final boolean isAllowNullValues() {
        return this.allowNullValues;
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public Cache.ValueWrapper get(Object key) {
        Object value = lookup(key);
        return toValueWrapper(value);
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public <T> T get(Object key, @Nullable Class<T> type) {
        T t = (T) fromStoreValue(lookup(key));
        if (t != null && type != null && !type.isInstance(t)) {
            throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + t);
        }
        return t;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Object fromStoreValue(@Nullable Object storeValue) {
        if (this.allowNullValues && storeValue == NullValue.INSTANCE) {
            return null;
        }
        return storeValue;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object toStoreValue(@Nullable Object userValue) {
        if (userValue == null) {
            if (this.allowNullValues) {
                return NullValue.INSTANCE;
            }
            throw new IllegalArgumentException("Cache '" + getName() + "' is configured to not allow null values but null was provided");
        }
        return userValue;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Cache.ValueWrapper toValueWrapper(@Nullable Object storeValue) {
        if (storeValue != null) {
            return new SimpleValueWrapper(fromStoreValue(storeValue));
        }
        return null;
    }
}