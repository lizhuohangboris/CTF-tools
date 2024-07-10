package com.fasterxml.classmate.util;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/util/MethodKey.class */
public class MethodKey implements Serializable {
    private static final Class<?>[] NO_CLASSES = new Class[0];
    private final String _name;
    private final Class<?>[] _argumentTypes;
    private final int _hashCode;

    public MethodKey(String name) {
        this._name = name;
        this._argumentTypes = NO_CLASSES;
        this._hashCode = name.hashCode();
    }

    public MethodKey(String name, Class<?>[] argTypes) {
        this._name = name;
        this._argumentTypes = argTypes;
        this._hashCode = name.hashCode() + argTypes.length;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        MethodKey other = (MethodKey) o;
        Class<?>[] otherArgs = other._argumentTypes;
        int len = this._argumentTypes.length;
        if (otherArgs.length != len) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (otherArgs[i] != this._argumentTypes[i]) {
                return false;
            }
        }
        return this._name.equals(other._name);
    }

    public int hashCode() {
        return this._hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this._name);
        sb.append('(');
        int len = this._argumentTypes.length;
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(this._argumentTypes[i].getName());
        }
        sb.append(')');
        return sb.toString();
    }
}