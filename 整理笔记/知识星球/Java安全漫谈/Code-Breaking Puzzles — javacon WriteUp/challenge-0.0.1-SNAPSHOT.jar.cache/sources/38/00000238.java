package com.fasterxml.classmate.members;

import com.fasterxml.classmate.Annotations;
import com.fasterxml.classmate.ResolvedType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/members/ResolvedMethod.class */
public final class ResolvedMethod extends ResolvedParameterizedMember<Method> implements Comparable<ResolvedMethod> {
    public ResolvedMethod(ResolvedType context, Annotations ann, Method method, ResolvedType returnType, ResolvedType[] argumentTypes) {
        super(context, ann, method, returnType, argumentTypes);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }

    public boolean isStrict() {
        return Modifier.isStrict(getModifiers());
    }

    public boolean isNative() {
        return Modifier.isNative(getModifiers());
    }

    public boolean isSynchronized() {
        return Modifier.isSynchronized(getModifiers());
    }

    public ResolvedType getReturnType() {
        return getType();
    }

    @Override // java.lang.Comparable
    public int compareTo(ResolvedMethod other) {
        int diff = getName().compareTo(other.getName());
        if (diff == 0) {
            diff = getArgumentCount() - other.getArgumentCount();
        }
        return diff;
    }
}