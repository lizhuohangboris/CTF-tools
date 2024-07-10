package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/GetDeclaredMethodHandle.class */
public final class GetDeclaredMethodHandle implements PrivilegedAction<MethodHandle> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final MethodHandles.Lookup lookup;
    private final Class<?> clazz;
    private final String methodName;
    private final Class<?>[] parameterTypes;
    private final boolean makeAccessible;

    public static GetDeclaredMethodHandle action(MethodHandles.Lookup lookup, Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        return new GetDeclaredMethodHandle(lookup, clazz, methodName, false, parameterTypes);
    }

    public static GetDeclaredMethodHandle andMakeAccessible(MethodHandles.Lookup lookup, Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        return new GetDeclaredMethodHandle(lookup, clazz, methodName, true, parameterTypes);
    }

    private GetDeclaredMethodHandle(MethodHandles.Lookup lookup, Class<?> clazz, String methodName, boolean makeAccessible, Class<?>... parameterTypes) {
        this.lookup = lookup;
        this.clazz = clazz;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.makeAccessible = makeAccessible;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public MethodHandle run() {
        try {
            Method method = this.clazz.getDeclaredMethod(this.methodName, this.parameterTypes);
            if (this.makeAccessible) {
                method.setAccessible(true);
            }
            return this.lookup.unreflect(method);
        } catch (IllegalAccessException e) {
            throw LOG.getUnableToAccessMethodException(this.lookup, this.clazz, this.methodName, this.parameterTypes, e);
        } catch (NoSuchMethodException e2) {
            return null;
        }
    }
}