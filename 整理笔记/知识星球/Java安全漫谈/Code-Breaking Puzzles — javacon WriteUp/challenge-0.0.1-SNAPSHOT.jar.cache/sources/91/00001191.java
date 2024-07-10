package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.reflect.Field;
import java.security.PrivilegedAction;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/GetDeclaredField.class */
public final class GetDeclaredField implements PrivilegedAction<Field> {
    private final Class<?> clazz;
    private final String fieldName;
    private final boolean makeAccessible;

    public static GetDeclaredField action(Class<?> clazz, String fieldName) {
        return new GetDeclaredField(clazz, fieldName, false);
    }

    public static GetDeclaredField andMakeAccessible(Class<?> clazz, String fieldName) {
        return new GetDeclaredField(clazz, fieldName, true);
    }

    private GetDeclaredField(Class<?> clazz, String fieldName, boolean makeAccessible) {
        this.clazz = clazz;
        this.fieldName = fieldName;
        this.makeAccessible = makeAccessible;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public Field run() {
        try {
            Field field = this.clazz.getDeclaredField(this.fieldName);
            if (this.makeAccessible) {
                field.setAccessible(true);
            }
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}