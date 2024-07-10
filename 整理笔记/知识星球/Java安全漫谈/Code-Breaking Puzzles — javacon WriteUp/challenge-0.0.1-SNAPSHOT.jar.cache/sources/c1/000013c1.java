package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/InjectionPoint.class */
public class InjectionPoint {
    @Nullable
    protected MethodParameter methodParameter;
    @Nullable
    protected Field field;
    @Nullable
    private volatile Annotation[] fieldAnnotations;

    public InjectionPoint(MethodParameter methodParameter) {
        Assert.notNull(methodParameter, "MethodParameter must not be null");
        this.methodParameter = methodParameter;
    }

    public InjectionPoint(Field field) {
        Assert.notNull(field, "Field must not be null");
        this.field = field;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public InjectionPoint(InjectionPoint original) {
        this.methodParameter = original.methodParameter != null ? new MethodParameter(original.methodParameter) : null;
        this.field = original.field;
        this.fieldAnnotations = original.fieldAnnotations;
    }

    protected InjectionPoint() {
    }

    @Nullable
    public MethodParameter getMethodParameter() {
        return this.methodParameter;
    }

    @Nullable
    public Field getField() {
        return this.field;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final MethodParameter obtainMethodParameter() {
        Assert.state(this.methodParameter != null, "Neither Field nor MethodParameter");
        return this.methodParameter;
    }

    public Annotation[] getAnnotations() {
        if (this.field != null) {
            Annotation[] fieldAnnotations = this.fieldAnnotations;
            if (fieldAnnotations == null) {
                fieldAnnotations = this.field.getAnnotations();
                this.fieldAnnotations = fieldAnnotations;
            }
            return fieldAnnotations;
        }
        return obtainMethodParameter().getParameterAnnotations();
    }

    @Nullable
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return this.field != null ? (A) this.field.getAnnotation(annotationType) : (A) obtainMethodParameter().getParameterAnnotation(annotationType);
    }

    public Class<?> getDeclaredType() {
        return this.field != null ? this.field.getType() : obtainMethodParameter().getParameterType();
    }

    public Member getMember() {
        return this.field != null ? this.field : obtainMethodParameter().getMember();
    }

    public AnnotatedElement getAnnotatedElement() {
        return this.field != null ? this.field : obtainMethodParameter().getAnnotatedElement();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (getClass() != other.getClass()) {
            return false;
        }
        InjectionPoint otherPoint = (InjectionPoint) other;
        return ObjectUtils.nullSafeEquals(this.field, otherPoint.field) && ObjectUtils.nullSafeEquals(this.methodParameter, otherPoint.methodParameter);
    }

    public int hashCode() {
        return this.field != null ? this.field.hashCode() : ObjectUtils.nullSafeHashCode(this.methodParameter);
    }

    public String toString() {
        return this.field != null ? "field '" + this.field.getName() + "'" : String.valueOf(this.methodParameter);
    }
}