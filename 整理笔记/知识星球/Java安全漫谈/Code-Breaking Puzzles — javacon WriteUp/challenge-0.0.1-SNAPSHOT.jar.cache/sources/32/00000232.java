package com.fasterxml.classmate.members;

import com.fasterxml.classmate.ResolvedType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/members/RawField.class */
public final class RawField extends RawMember {
    protected final Field _field;
    private final int _hashCode;

    public RawField(ResolvedType context, Field field) {
        super(context);
        this._field = field;
        this._hashCode = this._field == null ? 0 : this._field.hashCode();
    }

    @Override // com.fasterxml.classmate.members.RawMember
    public Field getRawMember() {
        return this._field;
    }

    public boolean isTransient() {
        return Modifier.isTransient(getModifiers());
    }

    public boolean isVolatile() {
        return Modifier.isVolatile(getModifiers());
    }

    @Override // com.fasterxml.classmate.members.RawMember
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        RawField other = (RawField) o;
        return other._field == this._field;
    }

    @Override // com.fasterxml.classmate.members.RawMember
    public int hashCode() {
        return this._hashCode;
    }
}