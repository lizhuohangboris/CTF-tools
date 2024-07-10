package org.springframework.cache;

import java.util.concurrent.Callable;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/Cache.class */
public interface Cache {

    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/Cache$ValueWrapper.class */
    public interface ValueWrapper {
        @Nullable
        Object get();
    }

    String getName();

    Object getNativeCache();

    @Nullable
    ValueWrapper get(Object obj);

    @Nullable
    <T> T get(Object obj, @Nullable Class<T> cls);

    @Nullable
    <T> T get(Object obj, Callable<T> callable);

    void put(Object obj, @Nullable Object obj2);

    @Nullable
    ValueWrapper putIfAbsent(Object obj, @Nullable Object obj2);

    void evict(Object obj);

    void clear();

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/Cache$ValueRetrievalException.class */
    public static class ValueRetrievalException extends RuntimeException {
        @Nullable
        private final Object key;

        public ValueRetrievalException(@Nullable Object key, Callable<?> loader, Throwable ex) {
            super(String.format("Value for key '%s' could not be loaded using '%s'", key, loader), ex);
            this.key = key;
        }

        @Nullable
        public Object getKey() {
            return this.key;
        }
    }
}