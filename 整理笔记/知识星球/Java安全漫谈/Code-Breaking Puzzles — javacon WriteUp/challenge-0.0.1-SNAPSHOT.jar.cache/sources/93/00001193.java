package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.reflect.Method;
import java.security.PrivilegedAction;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/GetDeclaredMethod.class */
public final class GetDeclaredMethod implements PrivilegedAction<Method> {
    private final Class<?> clazz;
    private final String methodName;
    private final Class<?>[] parameterTypes;
    private final boolean makeAccessible;

    public static GetDeclaredMethod action(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        return new GetDeclaredMethod(clazz, methodName, false, parameterTypes);
    }

    public static GetDeclaredMethod andMakeAccessible(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        return new GetDeclaredMethod(clazz, methodName, true, parameterTypes);
    }

    private GetDeclaredMethod(Class<?> clazz, String methodName, boolean makeAccessible, Class<?>... parameterTypes) {
        this.clazz = clazz;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.makeAccessible = makeAccessible;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public Method run() {
        try {
            Method method = this.clazz.getDeclaredMethod(this.methodName, this.parameterTypes);
            if (this.makeAccessible) {
                method.setAccessible(true);
            }
            return method;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}