package com.fasterxml.classmate.util;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/util/ClassKey.class */
public class ClassKey implements Comparable<ClassKey>, Serializable {
    private final String _className;
    private final Class<?> _class;
    private final int _hashCode;

    public ClassKey(Class<?> clz) {
        this._class = clz;
        this._className = clz.getName();
        this._hashCode = this._className.hashCode();
    }

    @Override // java.lang.Comparable
    public int compareTo(ClassKey other) {
        return this._className.compareTo(other._className);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o != null && o.getClass() == getClass()) {
            ClassKey other = (ClassKey) o;
            return other._class == this._class;
        }
        return false;
    }

    public int hashCode() {
        return this._hashCode;
    }

    public String toString() {
        return this._className;
    }
}