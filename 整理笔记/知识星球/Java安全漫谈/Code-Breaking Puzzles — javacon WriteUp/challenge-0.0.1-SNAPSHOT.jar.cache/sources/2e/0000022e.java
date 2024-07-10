package com.fasterxml.classmate;

import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/TypeBindings.class */
public final class TypeBindings {
    private static final String[] NO_STRINGS = new String[0];
    private static final ResolvedType[] NO_TYPES = new ResolvedType[0];
    private static final TypeBindings EMPTY = new TypeBindings(NO_STRINGS, NO_TYPES, null);
    private final String[] _names;
    private final ResolvedType[] _types;
    private final String[] _unboundVariables;
    private final int _hashCode;

    private TypeBindings(String[] names, ResolvedType[] types, String[] uvars) {
        this._names = names == null ? NO_STRINGS : names;
        this._types = types == null ? NO_TYPES : types;
        if (this._names.length != this._types.length) {
            throw new IllegalArgumentException("Mismatching names (" + this._names.length + "), types (" + this._types.length + ")");
        }
        int h = 1;
        int len = this._types.length;
        for (int i = 0; i < len; i++) {
            h += this._types[i].hashCode();
        }
        this._unboundVariables = uvars;
        this._hashCode = h;
    }

    public static TypeBindings emptyBindings() {
        return EMPTY;
    }

    public static TypeBindings create(Class<?> erasedType, List<ResolvedType> typeList) {
        ResolvedType[] types = (typeList == null || typeList.isEmpty()) ? NO_TYPES : (ResolvedType[]) typeList.toArray(new ResolvedType[typeList.size()]);
        return create(erasedType, types);
    }

    public static TypeBindings create(Class<?> erasedType, ResolvedType[] types) {
        String[] names;
        if (types == null) {
            types = NO_TYPES;
        }
        TypeVariable<?>[] vars = erasedType.getTypeParameters();
        if (vars == null || vars.length == 0) {
            names = NO_STRINGS;
        } else {
            int len = vars.length;
            names = new String[len];
            for (int i = 0; i < len; i++) {
                names[i] = vars[i].getName();
            }
        }
        if (names.length != types.length) {
            throw new IllegalArgumentException("Can not create TypeBinding for class " + erasedType.getName() + " with " + types.length + " type parameter" + (types.length == 1 ? "" : "s") + ": class expects " + names.length);
        }
        return new TypeBindings(names, types, null);
    }

    public TypeBindings withUnboundVariable(String name) {
        int len = this._unboundVariables == null ? 0 : this._unboundVariables.length;
        String[] names = len == 0 ? new String[1] : (String[]) Arrays.copyOf(this._unboundVariables, len + 1);
        names[len] = name;
        return new TypeBindings(this._names, this._types, names);
    }

    public ResolvedType findBoundType(String name) {
        int len = this._names.length;
        for (int i = 0; i < len; i++) {
            if (name.equals(this._names[i])) {
                return this._types[i];
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return this._types.length == 0;
    }

    public int size() {
        return this._types.length;
    }

    public String getBoundName(int index) {
        if (index < 0 || index >= this._names.length) {
            return null;
        }
        return this._names[index];
    }

    public ResolvedType getBoundType(int index) {
        if (index < 0 || index >= this._types.length) {
            return null;
        }
        return this._types[index];
    }

    public List<ResolvedType> getTypeParameters() {
        if (this._types.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(this._types);
    }

    public boolean hasUnbound(String name) {
        if (this._unboundVariables != null) {
            int i = this._unboundVariables.length;
            do {
                i--;
                if (i < 0) {
                    return false;
                }
            } while (!name.equals(this._unboundVariables[i]));
            return true;
        }
        return false;
    }

    public String toString() {
        if (this._types.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('<');
        int len = this._types.length;
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb = this._types[i].appendBriefDescription(sb);
        }
        sb.append('>');
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
        TypeBindings other = (TypeBindings) o;
        int len = this._types.length;
        if (len != other.size()) {
            return false;
        }
        ResolvedType[] otherTypes = other._types;
        for (int i = 0; i < len; i++) {
            if (!otherTypes[i].equals(this._types[i])) {
                return false;
            }
        }
        return true;
    }

    public ResolvedType[] typeParameterArray() {
        return this._types;
    }
}