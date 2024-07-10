package com.fasterxml.classmate.types;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import java.util.Collections;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/types/TypePlaceHolder.class */
public class TypePlaceHolder extends ResolvedType {
    protected final int _ordinal;
    protected ResolvedType _actualType;

    public TypePlaceHolder(int ordinal) {
        super(Object.class, TypeBindings.emptyBindings());
        this._ordinal = ordinal;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean canCreateSubtypes() {
        return false;
    }

    public ResolvedType actualType() {
        return this._actualType;
    }

    public void actualType(ResolvedType t) {
        this._actualType = t;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public ResolvedType getParentClass() {
        return null;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public ResolvedType getSelfReferencedType() {
        return null;
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
        return false;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean isAbstract() {
        return true;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean isArray() {
        return false;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean isPrimitive() {
        return false;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendSignature(StringBuilder sb) {
        return _appendClassSignature(sb);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendErasedSignature(StringBuilder sb) {
        return _appendErasedClassSignature(sb);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendBriefDescription(StringBuilder sb) {
        sb.append('<').append(this._ordinal).append('>');
        return sb;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendFullDescription(StringBuilder sb) {
        return appendBriefDescription(sb);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean equals(Object o) {
        return o == this;
    }
}