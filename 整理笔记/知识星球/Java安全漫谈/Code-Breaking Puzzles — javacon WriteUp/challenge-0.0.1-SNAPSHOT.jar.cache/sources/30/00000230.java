package com.fasterxml.classmate.members;

import com.fasterxml.classmate.ResolvedType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/members/HierarchicType.class */
public final class HierarchicType {
    protected final boolean _isMixin;
    protected final ResolvedType _type;
    protected final int _priority;

    public HierarchicType(ResolvedType type, boolean mixin, int priority) {
        this._type = type;
        this._isMixin = mixin;
        this._priority = priority;
    }

    public ResolvedType getType() {
        return this._type;
    }

    public Class<?> getErasedType() {
        return this._type.getErasedType();
    }

    public boolean isMixin() {
        return this._isMixin;
    }

    public int getPriority() {
        return this._priority;
    }

    public String toString() {
        return this._type.toString();
    }

    public int hashCode() {
        return this._type.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        HierarchicType other = (HierarchicType) o;
        return this._type.equals(other._type);
    }
}