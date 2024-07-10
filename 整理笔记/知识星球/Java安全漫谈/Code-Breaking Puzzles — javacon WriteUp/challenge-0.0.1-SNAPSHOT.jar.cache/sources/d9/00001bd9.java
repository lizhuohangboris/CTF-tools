package org.springframework.cglib.core;

import org.springframework.asm.Type;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/MethodInfo.class */
public abstract class MethodInfo {
    public abstract ClassInfo getClassInfo();

    public abstract int getModifiers();

    public abstract Signature getSignature();

    public abstract Type[] getExceptionTypes();

    public boolean equals(Object o) {
        if (o == null || !(o instanceof MethodInfo)) {
            return false;
        }
        return getSignature().equals(((MethodInfo) o).getSignature());
    }

    public int hashCode() {
        return getSignature().hashCode();
    }

    public String toString() {
        return getSignature().toString();
    }
}