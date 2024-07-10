package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.reflect.Constructor;
import java.security.PrivilegedAction;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/GetDeclaredConstructors.class */
public final class GetDeclaredConstructors implements PrivilegedAction<Constructor<?>[]> {
    private final Class<?> clazz;

    public static GetDeclaredConstructors action(Class<?> clazz) {
        return new GetDeclaredConstructors(clazz);
    }

    private GetDeclaredConstructors(Class<?> clazz) {
        this.clazz = clazz;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public Constructor<?>[] run() {
        return this.clazz.getDeclaredConstructors();
    }
}