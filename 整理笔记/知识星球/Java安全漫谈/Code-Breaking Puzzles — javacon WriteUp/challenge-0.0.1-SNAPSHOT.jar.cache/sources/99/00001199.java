package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.reflect.Method;
import java.security.PrivilegedAction;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/GetMethods.class */
public final class GetMethods implements PrivilegedAction<Method[]> {
    private final Class<?> clazz;

    public static GetMethods action(Class<?> clazz) {
        return new GetMethods(clazz);
    }

    private GetMethods(Class<?> clazz) {
        this.clazz = clazz;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public Method[] run() {
        return this.clazz.getMethods();
    }
}