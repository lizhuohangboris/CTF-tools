package com.fasterxml.classmate.members;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.util.MethodKey;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/members/RawMethod.class */
public final class RawMethod extends RawMember {
    protected final Method _method;
    protected final int _hashCode;

    public RawMethod(ResolvedType context, Method method) {
        super(context);
        this._method = method;
        this._hashCode = this._method == null ? 0 : this._method.hashCode();
    }

    @Override // com.fasterxml.classmate.members.RawMember
    public Method getRawMember() {
        return this._method;
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

    public MethodKey createKey() {
        String name = this._method.getName();
        Class<?>[] argTypes = this._method.getParameterTypes();
        return new MethodKey(name, argTypes);
    }

    @Override // com.fasterxml.classmate.members.RawMember
    public int hashCode() {
        return this._hashCode;
    }

    @Override // com.fasterxml.classmate.members.RawMember
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        RawMethod other = (RawMethod) o;
        return other._method == this._method;
    }
}