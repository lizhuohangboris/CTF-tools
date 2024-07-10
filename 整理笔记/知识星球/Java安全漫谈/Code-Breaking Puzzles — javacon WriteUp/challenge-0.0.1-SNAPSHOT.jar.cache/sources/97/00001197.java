package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.reflect.Method;
import java.security.PrivilegedAction;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/GetMethod.class */
public final class GetMethod implements PrivilegedAction<Method> {
    private final Class<?> clazz;
    private final String methodName;

    public static GetMethod action(Class<?> clazz, String methodName) {
        return new GetMethod(clazz, methodName);
    }

    private GetMethod(Class<?> clazz, String methodName) {
        this.clazz = clazz;
        this.methodName = methodName;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public Method run() {
        try {
            return this.clazz.getMethod(this.methodName, new Class[0]);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}