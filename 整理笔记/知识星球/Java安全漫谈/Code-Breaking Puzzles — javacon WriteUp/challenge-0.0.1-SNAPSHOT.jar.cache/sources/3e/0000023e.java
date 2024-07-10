package com.fasterxml.classmate.types;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import com.fasterxml.classmate.members.RawConstructor;
import com.fasterxml.classmate.members.RawField;
import com.fasterxml.classmate.members.RawMethod;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/types/ResolvedRecursiveType.class */
public class ResolvedRecursiveType extends ResolvedType {
    protected ResolvedType _referencedType;

    public ResolvedRecursiveType(Class<?> erased, TypeBindings bindings) {
        super(erased, bindings);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean canCreateSubtypes() {
        return this._referencedType.canCreateSubtypes();
    }

    public void setReference(ResolvedType ref) {
        if (this._referencedType != null) {
            throw new IllegalStateException("Trying to re-set self reference; old value = " + this._referencedType + ", new = " + ref);
        }
        this._referencedType = ref;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public ResolvedType getParentClass() {
        return null;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public ResolvedType getSelfReferencedType() {
        return this._referencedType;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public List<ResolvedType> getImplementedInterfaces() {
        return Collections.emptyList();
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public ResolvedType getArrayElementType() {
        return null;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean isInterface() {
        return this._erasedType.isInterface();
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean isAbstract() {
        return Modifier.isAbstract(this._erasedType.getModifiers());
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean isArray() {
        return this._erasedType.isArray();
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean isPrimitive() {
        return false;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public List<RawField> getMemberFields() {
        return this._referencedType.getMemberFields();
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public List<RawField> getStaticFields() {
        return this._referencedType.getStaticFields();
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public List<RawMethod> getStaticMethods() {
        return this._referencedType.getStaticMethods();
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public List<RawMethod> getMemberMethods() {
        return this._referencedType.getMemberMethods();
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public List<RawConstructor> getConstructors() {
        return this._referencedType.getConstructors();
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendSignature(StringBuilder sb) {
        return appendErasedSignature(sb);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendErasedSignature(StringBuilder sb) {
        return _appendErasedClassSignature(sb);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendBriefDescription(StringBuilder sb) {
        return _appendClassDescription(sb);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendFullDescription(StringBuilder sb) {
        return appendBriefDescription(sb);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        ResolvedRecursiveType other = (ResolvedRecursiveType) o;
        if (this._referencedType == null) {
            return other._referencedType == null;
        }
        return this._referencedType.equals(other._referencedType);
    }
}