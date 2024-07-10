package org.hibernate.validator.internal.util.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Map;
import org.hibernate.validator.internal.util.privilegedactions.GetAnnotationAttributes;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/annotation/AnnotationProxy.class */
public class AnnotationProxy implements Annotation, InvocationHandler, Serializable {
    private static final long serialVersionUID = 6907601010599429454L;
    private final AnnotationDescriptor<? extends Annotation> descriptor;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnnotationProxy(AnnotationDescriptor<? extends Annotation> descriptor) {
        this.descriptor = descriptor;
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object value = this.descriptor.getAttribute(method.getName());
        if (value != null) {
            return value;
        }
        return method.invoke(this, args);
    }

    @Override // java.lang.annotation.Annotation
    public Class<? extends Annotation> annotationType() {
        return this.descriptor.getType();
    }

    @Override // java.lang.annotation.Annotation
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !this.descriptor.getType().isInstance(obj)) {
            return false;
        }
        Annotation other = this.descriptor.getType().cast(obj);
        Map<String, Object> otherAttributes = getAnnotationAttributes(other);
        if (this.descriptor.getAttributes().size() != otherAttributes.size()) {
            return false;
        }
        for (Map.Entry<String, Object> member : this.descriptor.getAttributes().entrySet()) {
            Object value = member.getValue();
            Object otherValue = otherAttributes.get(member.getKey());
            if (!areEqual(value, otherValue)) {
                return false;
            }
        }
        return true;
    }

    @Override // java.lang.annotation.Annotation
    public int hashCode() {
        return this.descriptor.hashCode();
    }

    @Override // java.lang.annotation.Annotation
    public String toString() {
        return this.descriptor.toString();
    }

    private boolean areEqual(Object o1, Object o2) {
        return !o1.getClass().isArray() ? o1.equals(o2) : o1.getClass() == boolean[].class ? Arrays.equals((boolean[]) o1, (boolean[]) o2) : o1.getClass() == byte[].class ? Arrays.equals((byte[]) o1, (byte[]) o2) : o1.getClass() == char[].class ? Arrays.equals((char[]) o1, (char[]) o2) : o1.getClass() == double[].class ? Arrays.equals((double[]) o1, (double[]) o2) : o1.getClass() == float[].class ? Arrays.equals((float[]) o1, (float[]) o2) : o1.getClass() == int[].class ? Arrays.equals((int[]) o1, (int[]) o2) : o1.getClass() == long[].class ? Arrays.equals((long[]) o1, (long[]) o2) : o1.getClass() == short[].class ? Arrays.equals((short[]) o1, (short[]) o2) : Arrays.equals((Object[]) o1, (Object[]) o2);
    }

    private Map<String, Object> getAnnotationAttributes(Annotation annotation) {
        if (Proxy.isProxyClass(annotation.getClass()) && System.getSecurityManager() == null) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
            if (invocationHandler instanceof AnnotationProxy) {
                return ((AnnotationProxy) invocationHandler).descriptor.getAttributes();
            }
        }
        return (Map) run(GetAnnotationAttributes.action(annotation));
    }

    private <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }
}