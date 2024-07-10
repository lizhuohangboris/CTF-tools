package org.hibernate.validator.internal.util.privilegedactions;

import java.security.PrivilegedAction;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.logging.Messages;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/GetClassLoader.class */
public final class GetClassLoader implements PrivilegedAction<ClassLoader> {
    private final Class<?> clazz;

    public static GetClassLoader fromContext() {
        return new GetClassLoader(null);
    }

    public static GetClassLoader fromClass(Class<?> clazz) {
        Contracts.assertNotNull(clazz, Messages.MESSAGES.classIsNull());
        return new GetClassLoader(clazz);
    }

    private GetClassLoader(Class<?> clazz) {
        this.clazz = clazz;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public ClassLoader run() {
        if (this.clazz != null) {
            return this.clazz.getClassLoader();
        }
        return Thread.currentThread().getContextClassLoader();
    }
}