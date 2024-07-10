package org.hibernate.validator.internal.metadata.location;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/location/ConstraintLocation.class */
public interface ConstraintLocation {
    Class<?> getDeclaringClass();

    Member getMember();

    Type getTypeForValidatorResolution();

    void appendTo(ExecutableParameterNameProvider executableParameterNameProvider, PathImpl pathImpl);

    Object getValue(Object obj);

    static ConstraintLocation forClass(Class<?> declaringClass) {
        return new BeanConstraintLocation(declaringClass);
    }

    static ConstraintLocation forField(Field field) {
        return new FieldConstraintLocation(field);
    }

    static ConstraintLocation forGetter(Method getter) {
        return new GetterConstraintLocation(getter.getDeclaringClass(), getter);
    }

    static ConstraintLocation forGetter(Class<?> declaringClass, Method getter) {
        return new GetterConstraintLocation(declaringClass, getter);
    }

    static ConstraintLocation forTypeArgument(ConstraintLocation delegate, TypeVariable<?> typeParameter, Type typeOfAnnotatedElement) {
        return new TypeArgumentConstraintLocation(delegate, typeParameter, typeOfAnnotatedElement);
    }

    static ConstraintLocation forReturnValue(Executable executable) {
        return new ReturnValueConstraintLocation(executable);
    }

    static ConstraintLocation forCrossParameter(Executable executable) {
        return new CrossParameterConstraintLocation(executable);
    }

    static ConstraintLocation forParameter(Executable executable, int index) {
        return new ParameterConstraintLocation(executable, index);
    }
}