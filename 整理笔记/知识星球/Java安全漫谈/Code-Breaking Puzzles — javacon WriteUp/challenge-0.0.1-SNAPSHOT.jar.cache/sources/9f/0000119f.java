package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.PrivilegedAction;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/NewProxyInstance.class */
public final class NewProxyInstance<T> implements PrivilegedAction<T> {
    private final ClassLoader classLoader;
    private final Class<?>[] interfaces;
    private final InvocationHandler invocationHandler;

    public static <T> NewProxyInstance<T> action(ClassLoader classLoader, Class<T> interfaze, InvocationHandler invocationHandler) {
        return new NewProxyInstance<>(classLoader, interfaze, invocationHandler);
    }

    public static NewProxyInstance<Object> action(ClassLoader classLoader, Class<?>[] interfaces, InvocationHandler invocationHandler) {
        return new NewProxyInstance<>(classLoader, interfaces, invocationHandler);
    }

    private NewProxyInstance(ClassLoader classLoader, Class<?>[] interfaces, InvocationHandler invocationHandler) {
        this.classLoader = classLoader;
        this.interfaces = interfaces;
        this.invocationHandler = invocationHandler;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private NewProxyInstance(ClassLoader classLoader, Class<T> interfaze, InvocationHandler invocationHandler) {
        this.classLoader = classLoader;
        this.interfaces = new Class[]{interfaze};
        this.invocationHandler = invocationHandler;
    }

    @Override // java.security.PrivilegedAction
    public T run() {
        return (T) Proxy.newProxyInstance(this.classLoader, this.interfaces, this.invocationHandler);
    }
}