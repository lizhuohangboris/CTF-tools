package org.springframework.core;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/MethodClassKey.class */
public final class MethodClassKey implements Comparable<MethodClassKey> {
    private final Method method;
    @Nullable
    private final Class<?> targetClass;

    public MethodClassKey(Method method, @Nullable Class<?> targetClass) {
        this.method = method;
        this.targetClass = targetClass;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MethodClassKey)) {
            return false;
        }
        MethodClassKey otherKey = (MethodClassKey) other;
        return this.method.equals(otherKey.method) && ObjectUtils.nullSafeEquals(this.targetClass, otherKey.targetClass);
    }

    public int hashCode() {
        return this.method.hashCode() + (this.targetClass != null ? this.targetClass.hashCode() * 29 : 0);
    }

    public String toString() {
        return this.method + (this.targetClass != null ? " on " + this.targetClass : "");
    }

    @Override // java.lang.Comparable
    public int compareTo(MethodClassKey other) {
        int result = this.method.getName().compareTo(other.method.getName());
        if (result == 0) {
            result = this.method.toString().compareTo(other.method.toString());
            if (result == 0 && this.targetClass != null && other.targetClass != null) {
                result = this.targetClass.getName().compareTo(other.targetClass.getName());
            }
        }
        return result;
    }
}