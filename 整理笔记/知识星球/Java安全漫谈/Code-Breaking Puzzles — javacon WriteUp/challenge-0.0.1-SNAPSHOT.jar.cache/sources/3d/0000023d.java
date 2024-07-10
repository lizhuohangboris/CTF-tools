package com.fasterxml.classmate.types;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/types/ResolvedPrimitiveType.class */
public final class ResolvedPrimitiveType extends ResolvedType {
    private static final ResolvedPrimitiveType VOID = new ResolvedPrimitiveType(Void.TYPE, 'V', "void");
    protected final String _signature;
    protected final String _description;

    protected ResolvedPrimitiveType(Class<?> erased, char sig, String desc) {
        super(erased, TypeBindings.emptyBindings());
        this._signature = String.valueOf(sig);
        this._description = desc;
    }

    public static List<ResolvedPrimitiveType> all() {
        ArrayList<ResolvedPrimitiveType> all = new ArrayList<>();
        all.add(new ResolvedPrimitiveType(Boolean.TYPE, 'Z', "boolean"));
        all.add(new ResolvedPrimitiveType(Byte.TYPE, 'B', "byte"));
        all.add(new ResolvedPrimitiveType(Short.TYPE, 'S', "short"));
        all.add(new ResolvedPrimitiveType(Character.TYPE, 'C', "char"));
        all.add(new ResolvedPrimitiveType(Integer.TYPE, 'I', "int"));
        all.add(new ResolvedPrimitiveType(Long.TYPE, 'J', "long"));
        all.add(new ResolvedPrimitiveType(Float.TYPE, 'F', "float"));
        all.add(new ResolvedPrimitiveType(Double.TYPE, 'D', "double"));
        return all;
    }

    public static ResolvedPrimitiveType voidType() {
        return VOID;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean canCreateSubtypes() {
        return false;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public ResolvedType getSelfReferencedType() {
        return null;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public ResolvedType getParentClass() {
        return null;
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
        return null;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean isArray() {
        return false;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean isPrimitive() {
        return true;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public List<ResolvedType> getImplementedInterfaces() {
        return Collections.emptyList();
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public String getSignature() {
        return this._signature;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public String getErasedSignature() {
        return this._signature;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public String getFullDescription() {
        return this._description;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendSignature(StringBuilder sb) {
        sb.append(this._signature);
        return sb;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendErasedSignature(StringBuilder sb) {
        sb.append(this._signature);
        return sb;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendFullDescription(StringBuilder sb) {
        sb.append(this._description);
        return sb;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendBriefDescription(StringBuilder sb) {
        sb.append(this._description);
        return sb;
    }
}