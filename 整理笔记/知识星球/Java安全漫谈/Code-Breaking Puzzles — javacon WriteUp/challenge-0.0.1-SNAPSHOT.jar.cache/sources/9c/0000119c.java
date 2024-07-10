package org.hibernate.validator.internal.util.privilegedactions;

import java.io.IOException;
import java.net.URL;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Enumeration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/GetResources.class */
public final class GetResources implements PrivilegedAction<Enumeration<URL>> {
    private final String resourceName;
    private final ClassLoader classLoader;

    public static GetResources action(ClassLoader classLoader, String resourceName) {
        return new GetResources(classLoader, resourceName);
    }

    private GetResources(ClassLoader classLoader, String resourceName) {
        this.classLoader = classLoader;
        this.resourceName = resourceName;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public Enumeration<URL> run() {
        try {
            return this.classLoader.getResources(this.resourceName);
        } catch (IOException e) {
            return Collections.enumeration(Collections.emptyList());
        }
    }
}