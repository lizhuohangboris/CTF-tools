package org.thymeleaf.util;

import java.io.Serializable;
import java.util.IdentityHashMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/IdentityCounter.class */
public final class IdentityCounter<T> implements Serializable {
    private static final long serialVersionUID = -6965348731301112911L;
    private final IdentityHashMap<T, Object> counted;

    public IdentityCounter(int expectedMaxSize) {
        this.counted = new IdentityHashMap<>(expectedMaxSize);
    }

    public void count(T object) {
        this.counted.put(object, null);
    }

    public boolean isAlreadyCounted(T object) {
        return this.counted.containsKey(object);
    }
}