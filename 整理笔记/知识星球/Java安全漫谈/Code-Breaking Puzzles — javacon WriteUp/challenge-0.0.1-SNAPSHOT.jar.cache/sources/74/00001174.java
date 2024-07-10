package org.hibernate.validator.internal.util.annotation;

import java.lang.annotation.Annotation;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.hibernate.validator.internal.util.privilegedactions.NewProxyInstance;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/annotation/AnnotationFactory.class */
public class AnnotationFactory {
    private AnnotationFactory() {
    }

    public static <T extends Annotation> T create(AnnotationDescriptor<T> descriptor) {
        return (T) run(NewProxyInstance.action((ClassLoader) run(GetClassLoader.fromClass(descriptor.getType())), descriptor.getType(), new AnnotationProxy(descriptor)));
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }
}