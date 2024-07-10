package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/introspect/Annotated.class */
public abstract class Annotated {
    public abstract <A extends Annotation> A getAnnotation(Class<A> cls);

    public abstract boolean hasAnnotation(Class<?> cls);

    public abstract boolean hasOneOf(Class<? extends Annotation>[] clsArr);

    public abstract AnnotatedElement getAnnotated();

    public abstract int getModifiers();

    public abstract String getName();

    public abstract JavaType getType();

    public abstract Class<?> getRawType();

    @Deprecated
    public abstract Iterable<Annotation> annotations();

    public abstract boolean equals(Object obj);

    public abstract int hashCode();

    public abstract String toString();

    public boolean isPublic() {
        return Modifier.isPublic(getModifiers());
    }

    @Deprecated
    public final JavaType getType(TypeBindings bogus) {
        return getType();
    }

    @Deprecated
    public Type getGenericType() {
        return getRawType();
    }
}