package com.fasterxml.classmate.members;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.util.MethodKey;
import java.lang.reflect.Constructor;
import org.springframework.cglib.core.Constants;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/members/RawConstructor.class */
public final class RawConstructor extends RawMember {
    protected final Constructor<?> _constructor;
    protected final int _hashCode;

    public RawConstructor(ResolvedType context, Constructor<?> constructor) {
        super(context);
        this._constructor = constructor;
        this._hashCode = this._constructor == null ? 0 : this._constructor.hashCode();
    }

    public MethodKey createKey() {
        Class<?>[] argTypes = this._constructor.getParameterTypes();
        return new MethodKey(Constants.CONSTRUCTOR_NAME, argTypes);
    }

    @Override // com.fasterxml.classmate.members.RawMember
    public Constructor<?> getRawMember() {
        return this._constructor;
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
        RawConstructor other = (RawConstructor) o;
        return other._constructor == this._constructor;
    }
}