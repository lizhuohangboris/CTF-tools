package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.util.Map;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/GetAnnotationAttributes.class */
public final class GetAnnotationAttributes implements PrivilegedAction<Map<String, Object>> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Annotation annotation;

    public static GetAnnotationAttributes action(Annotation annotation) {
        return new GetAnnotationAttributes(annotation);
    }

    private GetAnnotationAttributes(Annotation annotation) {
        this.annotation = annotation;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.security.PrivilegedAction
    public Map<String, Object> run() {
        Method[] declaredMethods = this.annotation.annotationType().getDeclaredMethods();
        Map<String, Object> attributes = CollectionHelper.newHashMap(declaredMethods.length);
        for (Method m : declaredMethods) {
            if (!m.isSynthetic()) {
                m.setAccessible(true);
                String attributeName = m.getName();
                try {
                    attributes.put(m.getName(), m.invoke(this.annotation, new Object[0]));
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw LOG.getUnableToGetAnnotationAttributeException(this.annotation.getClass(), attributeName, e);
                }
            }
        }
        return CollectionHelper.toImmutableMap(attributes);
    }
}