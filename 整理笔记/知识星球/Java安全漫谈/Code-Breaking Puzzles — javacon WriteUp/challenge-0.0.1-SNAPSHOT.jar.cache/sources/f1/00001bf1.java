package org.springframework.cglib.core;

import java.lang.ref.WeakReference;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/WeakCacheKey.class */
public class WeakCacheKey<T> extends WeakReference<T> {
    private final int hash;

    public WeakCacheKey(T referent) {
        super(referent);
        this.hash = referent.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WeakCacheKey)) {
            return false;
        }
        Object ours = get();
        Object theirs = ((WeakCacheKey) obj).get();
        return (ours == null || theirs == null || !ours.equals(theirs)) ? false : true;
    }

    public int hashCode() {
        return this.hash;
    }

    public String toString() {
        Object obj = get();
        return obj == null ? "Clean WeakIdentityKey, hash: " + this.hash : obj.toString();
    }
}