package com.fasterxml.classmate;

import com.fasterxml.classmate.members.RawConstructor;
import com.fasterxml.classmate.members.RawField;
import com.fasterxml.classmate.members.RawMethod;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/ResolvedType.class */
public abstract class ResolvedType implements Type {
    public static final ResolvedType[] NO_TYPES = new ResolvedType[0];
    protected static final RawConstructor[] NO_CONSTRUCTORS = new RawConstructor[0];
    protected static final RawField[] NO_FIELDS = new RawField[0];
    protected static final RawMethod[] NO_METHODS = new RawMethod[0];
    protected final Class<?> _erasedType;
    protected final TypeBindings _typeBindings;

    public abstract boolean canCreateSubtypes();

    public abstract ResolvedType getParentClass();

    public abstract ResolvedType getSelfReferencedType();

    public abstract ResolvedType getArrayElementType();

    public abstract List<ResolvedType> getImplementedInterfaces();

    public abstract boolean isInterface();

    public abstract boolean isAbstract();

    public abstract boolean isArray();

    public abstract boolean isPrimitive();

    public abstract StringBuilder appendBriefDescription(StringBuilder sb);

    public abstract StringBuilder appendFullDescription(StringBuilder sb);

    public abstract StringBuilder appendSignature(StringBuilder sb);

    public abstract StringBuilder appendErasedSignature(StringBuilder sb);

    /* JADX INFO: Access modifiers changed from: protected */
    public ResolvedType(Class<?> cls, TypeBindings bindings) {
        this._erasedType = cls;
        this._typeBindings = bindings == null ? TypeBindings.emptyBindings() : bindings;
    }

    public final boolean canCreateSubtype(Class<?> subtype) {
        return canCreateSubtypes() && this._erasedType.isAssignableFrom(subtype);
    }

    public Class<?> getErasedType() {
        return this._erasedType;
    }

    public List<ResolvedType> getTypeParameters() {
        return this._typeBindings.getTypeParameters();
    }

    public TypeBindings getTypeBindings() {
        return this._typeBindings;
    }

    public List<ResolvedType> typeParametersFor(Class<?> erasedSupertype) {
        ResolvedType type = findSupertype(erasedSupertype);
        if (type != null) {
            return type.getTypeParameters();
        }
        return null;
    }

    public ResolvedType findSupertype(Class<?> erasedSupertype) {
        ResolvedType type;
        if (erasedSupertype == this._erasedType) {
            return this;
        }
        if (erasedSupertype.isInterface()) {
            for (ResolvedType it : getImplementedInterfaces()) {
                ResolvedType type2 = it.findSupertype(erasedSupertype);
                if (type2 != null) {
                    return type2;
                }
            }
        }
        ResolvedType pc = getParentClass();
        if (pc != null && (type = pc.findSupertype(erasedSupertype)) != null) {
            return type;
        }
        return null;
    }

    public final boolean isConcrete() {
        return !isAbstract();
    }

    public final boolean isInstanceOf(Class<?> type) {
        return type.isAssignableFrom(this._erasedType);
    }

    public List<RawConstructor> getConstructors() {
        return Collections.emptyList();
    }

    public List<RawField> getMemberFields() {
        return Collections.emptyList();
    }

    public List<RawMethod> getMemberMethods() {
        return Collections.emptyList();
    }

    public List<RawField> getStaticFields() {
        return Collections.emptyList();
    }

    public List<RawMethod> getStaticMethods() {
        return Collections.emptyList();
    }

    public String getSignature() {
        StringBuilder sb = new StringBuilder();
        return appendSignature(sb).toString();
    }

    public String getErasedSignature() {
        StringBuilder sb = new StringBuilder();
        return appendErasedSignature(sb).toString();
    }

    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        return appendFullDescription(sb).toString();
    }

    public String getBriefDescription() {
        StringBuilder sb = new StringBuilder();
        return appendBriefDescription(sb).toString();
    }

    public String toString() {
        return getBriefDescription();
    }

    public int hashCode() {
        return this._erasedType.getName().hashCode() + this._typeBindings.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        ResolvedType other = (ResolvedType) o;
        if (other._erasedType != this._erasedType) {
            return false;
        }
        return this._typeBindings.equals(other._typeBindings);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StringBuilder _appendClassSignature(StringBuilder sb) {
        sb.append('L');
        StringBuilder sb2 = _appendClassName(sb);
        int count = this._typeBindings.size();
        if (count > 0) {
            sb2.append('<');
            for (int i = 0; i < count; i++) {
                sb2 = this._typeBindings.getBoundType(i).appendErasedSignature(sb2);
            }
            sb2.append('>');
        }
        sb2.append(';');
        return sb2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StringBuilder _appendErasedClassSignature(StringBuilder sb) {
        sb.append('L');
        StringBuilder sb2 = _appendClassName(sb);
        sb2.append(';');
        return sb2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StringBuilder _appendClassDescription(StringBuilder sb) {
        sb.append(this._erasedType.getName());
        int count = this._typeBindings.size();
        if (count > 0) {
            sb.append('<');
            for (int i = 0; i < count; i++) {
                if (i > 0) {
                    sb.append(',');
                }
                sb = this._typeBindings.getBoundType(i).appendBriefDescription(sb);
            }
            sb.append('>');
        }
        return sb;
    }

    protected StringBuilder _appendClassName(StringBuilder sb) {
        String name = this._erasedType.getName();
        int len = name.length();
        for (int i = 0; i < len; i++) {
            char c = name.charAt(i);
            if (c == '.') {
                c = '/';
            }
            sb.append(c);
        }
        return sb;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public RawField[] _getFields(boolean statics) {
        Field[] declaredFields;
        ArrayList<RawField> fields = new ArrayList<>();
        for (Field f : this._erasedType.getDeclaredFields()) {
            if (!f.isSynthetic() && Modifier.isStatic(f.getModifiers()) == statics) {
                fields.add(new RawField(this, f));
            }
        }
        if (fields.isEmpty()) {
            return NO_FIELDS;
        }
        return (RawField[]) fields.toArray(new RawField[fields.size()]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public RawMethod[] _getMethods(boolean statics) {
        Method[] declaredMethods;
        ArrayList<RawMethod> methods = new ArrayList<>();
        for (Method m : this._erasedType.getDeclaredMethods()) {
            if (!m.isSynthetic() && Modifier.isStatic(m.getModifiers()) == statics) {
                methods.add(new RawMethod(this, m));
            }
        }
        if (methods.isEmpty()) {
            return NO_METHODS;
        }
        return (RawMethod[]) methods.toArray(new RawMethod[methods.size()]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public RawConstructor[] _getConstructors() {
        Constructor<?>[] declaredConstructors;
        ArrayList<RawConstructor> ctors = new ArrayList<>();
        for (Constructor<?> c : this._erasedType.getDeclaredConstructors()) {
            if (!c.isSynthetic()) {
                ctors.add(new RawConstructor(this, c));
            }
        }
        if (ctors.isEmpty()) {
            return NO_CONSTRUCTORS;
        }
        return (RawConstructor[]) ctors.toArray(new RawConstructor[ctors.size()]);
    }
}