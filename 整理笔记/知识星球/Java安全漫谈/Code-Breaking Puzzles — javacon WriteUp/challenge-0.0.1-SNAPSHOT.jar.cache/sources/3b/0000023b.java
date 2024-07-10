package com.fasterxml.classmate.types;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import com.fasterxml.classmate.members.RawField;
import com.fasterxml.classmate.members.RawMethod;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/types/ResolvedInterfaceType.class */
public class ResolvedInterfaceType extends ResolvedType {
    protected final ResolvedType[] _superInterfaces;
    protected RawField[] _constantFields;
    protected RawMethod[] _memberMethods;

    public ResolvedInterfaceType(Class<?> erased, TypeBindings bindings, ResolvedType[] superInterfaces) {
        super(erased, bindings);
        this._superInterfaces = superInterfaces == null ? NO_TYPES : superInterfaces;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean canCreateSubtypes() {
        return true;
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
        return this._superInterfaces.length == 0 ? Collections.emptyList() : Arrays.asList(this._superInterfaces);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public ResolvedType getArrayElementType() {
        return null;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean isInterface() {
        return true;
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
    public synchronized List<RawField> getStaticFields() {
        if (this._constantFields == null) {
            this._constantFields = _getFields(true);
        }
        if (this._constantFields.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(this._constantFields);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public synchronized List<RawMethod> getMemberMethods() {
        if (this._memberMethods == null) {
            this._memberMethods = _getMethods(false);
        }
        if (this._memberMethods.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(this._memberMethods);
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
        return _appendClassDescription(sb);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public StringBuilder appendFullDescription(StringBuilder sb) {
        StringBuilder sb2 = _appendClassDescription(sb);
        int count = this._superInterfaces.length;
        if (count > 0) {
            sb2.append(" extends ");
            for (int i = 0; i < count; i++) {
                if (i > 0) {
                    sb2.append(",");
                }
                sb2 = this._superInterfaces[i].appendBriefDescription(sb2);
            }
        }
        return sb2;
    }
}