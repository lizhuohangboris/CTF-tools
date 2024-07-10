package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedAction;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/ConstructorInstance.class */
public final class ConstructorInstance<T> implements PrivilegedAction<T> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Constructor<T> constructor;
    private final Object[] initArgs;

    public static <T> ConstructorInstance<T> action(Constructor<T> constructor, Object... initArgs) {
        return new ConstructorInstance<>(constructor, initArgs);
    }

    private ConstructorInstance(Constructor<T> constructor, Object... initArgs) {
        this.constructor = constructor;
        this.initArgs = initArgs;
    }

    @Override // java.security.PrivilegedAction
    public T run() {
        try {
            return this.constructor.newInstance(this.initArgs);
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
            throw LOG.getUnableToInstantiateException(this.constructor.getDeclaringClass(), e);
        }
    }
}