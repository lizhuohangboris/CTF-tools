package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/GetAnnotationAttribute.class */
public final class GetAnnotationAttribute<T> implements PrivilegedAction<T> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Annotation annotation;
    private final String attributeName;
    private final Class<T> type;

    public static <T> GetAnnotationAttribute<T> action(Annotation annotation, String attributeName, Class<T> type) {
        return new GetAnnotationAttribute<>(annotation, attributeName, type);
    }

    private GetAnnotationAttribute(Annotation annotation, String attributeName, Class<T> type) {
        this.annotation = annotation;
        this.attributeName = attributeName;
        this.type = type;
    }

    @Override // java.security.PrivilegedAction
    public T run() {
        try {
            try {
                Method m = this.annotation.getClass().getMethod(this.attributeName, new Class[0]);
                m.setAccessible(true);
                T t = (T) m.invoke(this.annotation, new Object[0]);
                if (this.type.isAssignableFrom(t.getClass())) {
                    return t;
                }
                throw LOG.getWrongAnnotationAttributeTypeException(this.annotation.annotationType(), this.attributeName, this.type, t.getClass());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw LOG.getUnableToGetAnnotationAttributeException(this.annotation.annotationType(), this.attributeName, e);
            }
        } catch (NoSuchMethodException e2) {
            throw LOG.getUnableToFindAnnotationAttributeException(this.annotation.annotationType(), this.attributeName, e2);
        }
    }
}