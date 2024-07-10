package org.hibernate.validator.internal.engine.valueextraction;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/AnnotatedObject.class */
public class AnnotatedObject implements TypeVariable<Class<?>> {
    public static final AnnotatedObject INSTANCE = new AnnotatedObject();

    private AnnotatedObject() {
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

    public String toString() {
        return "AnnotatedObject.INSTANCE";
    }
}