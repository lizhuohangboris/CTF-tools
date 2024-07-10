package org.hibernate.validator.internal.util.privilegedactions;

import java.security.PrivilegedAction;
import org.hibernate.validator.internal.util.Contracts;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/SetContextClassLoader.class */
public final class SetContextClassLoader implements PrivilegedAction<Void> {
    private final ClassLoader classLoader;

    public static SetContextClassLoader action(ClassLoader classLoader) {
        Contracts.assertNotNull(classLoader, "class loader must not be null");
        return new SetContextClassLoader(classLoader);
    }

    private SetContextClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public Void run() {
        Thread.currentThread().setContextClassLoader(this.classLoader);
        return null;
    }
}