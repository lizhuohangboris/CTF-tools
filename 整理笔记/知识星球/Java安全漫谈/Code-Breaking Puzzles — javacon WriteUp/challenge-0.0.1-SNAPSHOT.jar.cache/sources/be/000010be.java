package org.hibernate.validator.internal.engine.valueextraction;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.hibernate.validator.internal.util.ReflectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/ArrayElement.class */
public class ArrayElement implements TypeVariable<Class<?>> {
    private final Class<?> containerClass;

    public ArrayElement(AnnotatedArrayType annotatedArrayType) {
        Type arrayElementType = annotatedArrayType.getAnnotatedGenericComponentType().getType();
        if (arrayElementType == Boolean.TYPE) {
            this.containerClass = boolean[].class;
        } else if (arrayElementType == Integer.TYPE) {
            this.containerClass = int[].class;
        } else if (arrayElementType == Long.TYPE) {
            this.containerClass = long[].class;
        } else if (arrayElementType == Double.TYPE) {
            this.containerClass = double[].class;
        } else if (arrayElementType == Float.TYPE) {
            this.containerClass = float[].class;
        } else if (arrayElementType == Byte.TYPE) {
            this.containerClass = byte[].class;
        } else if (arrayElementType == Short.TYPE) {
            this.containerClass = short[].class;
        } else if (arrayElementType == Character.TYPE) {
            this.containerClass = char[].class;
        } else {
            this.containerClass = Object[].class;
        }
    }

    public ArrayElement(Type arrayType) {
        Class<?> arrayClass = ReflectionHelper.getClassFromType(arrayType);
        if (arrayClass.getComponentType().isPrimitive()) {
            this.containerClass = arrayClass;
        } else {
            this.containerClass = Object[].class;
        }
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        throw new UnsupportedOperationException();
    }

    public Annotation[] getAnnotations() {
        throw new UnsupportedOperationException();
    }

    public Annotation[] getDeclaredAnnotations() {
        throw new UnsupportedOperationException();
    }

    @Override // java.lang.reflect.TypeVariable
    public Type[] getBounds() {
        throw new UnsupportedOperationException();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.lang.reflect.TypeVariable
    public Class<?> getGenericDeclaration() {
        throw new UnsupportedOperationException();
    }

    @Override // java.lang.reflect.TypeVariable
    public String getName() {
        throw new UnsupportedOperationException();
    }

    public AnnotatedType[] getAnnotatedBounds() {
        throw new UnsupportedOperationException();
    }

    public Class<?> getContainerClass() {
        return this.containerClass;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ArrayElement other = (ArrayElement) obj;
        return this.containerClass.equals(other.containerClass);
    }

    public int hashCode() {
        int result = (31 * 1) + this.containerClass.hashCode();
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append("<").append(this.containerClass).append(">");
        return sb.toString();
    }
}