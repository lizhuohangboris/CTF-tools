package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.springframework.core.MethodParameter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/SynthesizingMethodParameter.class */
public class SynthesizingMethodParameter extends MethodParameter {
    public SynthesizingMethodParameter(Method method, int parameterIndex) {
        super(method, parameterIndex);
    }

    public SynthesizingMethodParameter(Method method, int parameterIndex, int nestingLevel) {
        super(method, parameterIndex, nestingLevel);
    }

    public SynthesizingMethodParameter(Constructor<?> constructor, int parameterIndex) {
        super(constructor, parameterIndex);
    }

    public SynthesizingMethodParameter(Constructor<?> constructor, int parameterIndex, int nestingLevel) {
        super(constructor, parameterIndex, nestingLevel);
    }

    public SynthesizingMethodParameter(SynthesizingMethodParameter original) {
        super(original);
    }

    @Override // org.springframework.core.MethodParameter
    protected <A extends Annotation> A adaptAnnotation(A annotation) {
        return (A) AnnotationUtils.synthesizeAnnotation((Annotation) annotation, getAnnotatedElement());
    }

    @Override // org.springframework.core.MethodParameter
    protected Annotation[] adaptAnnotationArray(Annotation[] annotations) {
        return AnnotationUtils.synthesizeAnnotationArray(annotations, getAnnotatedElement());
    }

    @Override // org.springframework.core.MethodParameter
    /* renamed from: clone */
    public SynthesizingMethodParameter mo1575clone() {
        return new SynthesizingMethodParameter(this);
    }

    public static SynthesizingMethodParameter forExecutable(Executable executable, int parameterIndex) {
        if (executable instanceof Method) {
            return new SynthesizingMethodParameter((Method) executable, parameterIndex);
        }
        if (executable instanceof Constructor) {
            return new SynthesizingMethodParameter((Constructor) executable, parameterIndex);
        }
        throw new IllegalArgumentException("Not a Method/Constructor: " + executable);
    }

    public static SynthesizingMethodParameter forParameter(Parameter parameter) {
        return forExecutable(parameter.getDeclaringExecutable(), findParameterIndex(parameter));
    }
}