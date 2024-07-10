package org.springframework.cache.support;

import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/support/SimpleValueWrapper.class */
public class SimpleValueWrapper implements Cache.ValueWrapper {
    @Nullable
    private final Object value;

    public SimpleValueWrapper(@Nullable Object value) {
        this.value = value;
    }

    @Override // org.springframework.cache.Cache.ValueWrapper
    @Nullable
    public Object get() {
        return this.value;
    }
}