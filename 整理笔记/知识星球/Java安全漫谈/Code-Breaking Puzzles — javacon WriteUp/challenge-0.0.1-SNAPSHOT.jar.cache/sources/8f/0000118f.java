package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.reflect.Constructor;
import java.security.PrivilegedAction;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/GetDeclaredConstructor.class */
public final class GetDeclaredConstructor<T> implements PrivilegedAction<Constructor<T>> {
    private final Class<T> clazz;
    private final Class<?>[] params;

    public static <T> GetDeclaredConstructor<T> action(Class<T> clazz, Class<?>... params) {
        return new GetDeclaredConstructor<>(clazz, params);
    }

    private GetDeclaredConstructor(Class<T> clazz, Class<?>... params) {
        this.clazz = clazz;
        this.params = params;
    }

    @Override // java.security.PrivilegedAction
    public Constructor<T> run() {
        try {
            return this.clazz.getDeclaredConstructor(this.params);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}