package org.hibernate.validator.internal.util.privilegedactions;

import java.net.URL;
import java.security.PrivilegedAction;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/GetResource.class */
public final class GetResource implements PrivilegedAction<URL> {
    private final String resourceName;
    private final ClassLoader classLoader;

    public static GetResource action(ClassLoader classLoader, String resourceName) {
        return new GetResource(classLoader, resourceName);
    }

    private GetResource(ClassLoader classLoader, String resourceName) {
        this.classLoader = classLoader;
        this.resourceName = resourceName;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public URL run() {
        return this.classLoader.getResource(this.resourceName);
    }
}