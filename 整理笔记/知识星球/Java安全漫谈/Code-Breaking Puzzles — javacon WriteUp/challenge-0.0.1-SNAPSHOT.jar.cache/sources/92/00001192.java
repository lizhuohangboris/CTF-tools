package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.reflect.Field;
import java.security.PrivilegedAction;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/GetDeclaredFields.class */
public final class GetDeclaredFields implements PrivilegedAction<Field[]> {
    private final Class<?> clazz;

    public static GetDeclaredFields action(Class<?> clazz) {
        return new GetDeclaredFields(clazz);
    }

    private GetDeclaredFields(Class<?> clazz) {
        this.clazz = clazz;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public Field[] run() {
        return this.clazz.getDeclaredFields();
    }
}