package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedAction;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/NewInstance.class */
public final class NewInstance<T> implements PrivilegedAction<T> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Class<T> clazz;
    private final String message;

    public static <T> NewInstance<T> action(Class<T> clazz, String message) {
        return new NewInstance<>(clazz, message);
    }

    private NewInstance(Class<T> clazz, String message) {
        this.clazz = clazz;
        this.message = message;
    }

    @Override // java.security.PrivilegedAction
    public T run() {
        try {
            return this.clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (IllegalAccessException e) {
            throw LOG.getUnableToInstantiateException(this.clazz, e);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException e2) {
            throw LOG.getUnableToInstantiateException(this.message, this.clazz, e2);
        } catch (RuntimeException e3) {
            throw LOG.getUnableToInstantiateException(this.clazz, e3);
        }
    }
}