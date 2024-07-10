package com.fasterxml.classmate.types;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import java.util.Collections;
import java.util.List;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/types/ResolvedArrayType.class */
public final class ResolvedArrayType extends ResolvedType {
    protected final ResolvedType _elementType;

    public ResolvedArrayType(Class<?> erased, TypeBindings bindings, ResolvedType elementType) {
        super(erased, bindings);
        this._elementType = elementType;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean canCreateSubtypes() {
        return false;
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
    public boolean isInterface() {
        return false;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean isAbstract() {
        return false;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public ResolvedType getArrayElementType() {
        return this._elementType;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean isArray() {
        return true;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean isPrimitive() {
        return false;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendSignature(StringBuilder sb) {
        sb.append('[');
        return this._elementType.appendSignature(sb);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendErasedSignature(StringBuilder sb) {
        sb.append('[');
        return this._elementType.appendErasedSignature(sb);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendBriefDescription(StringBuilder sb) {
        StringBuilder sb2 = this._elementType.appendBriefDescription(sb);
        sb2.append(ClassUtils.ARRAY_SUFFIX);
        return sb2;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendFullDescription(StringBuilder sb) {
        return appendBriefDescription(sb);
    }
}