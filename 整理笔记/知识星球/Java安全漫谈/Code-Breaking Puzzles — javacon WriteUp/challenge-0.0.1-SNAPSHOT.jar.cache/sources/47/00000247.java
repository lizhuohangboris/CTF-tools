package com.fasterxml.classmate.util;

import com.fasterxml.classmate.ResolvedType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/util/ResolvedTypeKey.class */
public class ResolvedTypeKey {
    private final Class<?> _erasedType;
    private final ResolvedType[] _typeParameters;
    private final int _hashCode;

    public ResolvedTypeKey(Class<?> simpleType) {
        this(simpleType, null);
    }

    public ResolvedTypeKey(Class<?> erasedType, ResolvedType[] tp) {
        if (tp != null && tp.length == 0) {
            tp = null;
        }
        this._erasedType = erasedType;
        this._typeParameters = tp;
        int h = erasedType.getName().hashCode();
        this._hashCode = tp != null ? h + tp.length : h;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[CacheKey: ");
        sb.append(this._erasedType.getName()).append('(');
        if (this._typeParameters != null) {
            for (int i = 0; i < this._typeParameters.length; i++) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(this._typeParameters[i]);
            }
        }
        sb.append(")]");
        return sb.toString();
    }

    public int hashCode() {
        return this._hashCode;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        ResolvedTypeKey other = (ResolvedTypeKey) o;
        if (other._erasedType != this._erasedType) {
            return false;
        }
        ResolvedType[] otherTP = other._typeParameters;
        if (this._typeParameters == null) {
            return otherTP == null;
        } else if (otherTP == null || otherTP.length != this._typeParameters.length) {
            return false;
        } else {
            int len = this._typeParameters.length;
            for (int i = 0; i < len; i++) {
                if (!this._typeParameters[i].equals(otherTP[i])) {
                    return false;
                }
            }
            return true;
        }
    }
}