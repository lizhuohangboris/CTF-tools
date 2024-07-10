package com.fasterxml.classmate.types;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import com.fasterxml.classmate.members.RawConstructor;
import com.fasterxml.classmate.members.RawField;
import com.fasterxml.classmate.members.RawMethod;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/types/ResolvedObjectType.class */
public class ResolvedObjectType extends ResolvedType {
    protected final ResolvedType _superClass;
    protected final ResolvedType[] _superInterfaces;
    protected final int _modifiers;
    protected RawConstructor[] _constructors;
    protected RawField[] _memberFields;
    protected RawField[] _staticFields;
    protected RawMethod[] _memberMethods;
    protected RawMethod[] _staticMethods;

    public ResolvedObjectType(Class<?> erased, TypeBindings bindings, ResolvedType superClass, List<ResolvedType> interfaces) {
        this(erased, bindings, superClass, (interfaces == null || interfaces.isEmpty()) ? NO_TYPES : (ResolvedType[]) interfaces.toArray(new ResolvedType[interfaces.size()]));
    }

    public ResolvedObjectType(Class<?> erased, TypeBindings bindings, ResolvedType superClass, ResolvedType[] interfaces) {
        super(erased, bindings);
        if (superClass != null && !(superClass instanceof ResolvedObjectType) && !(superClass instanceof ResolvedRecursiveType)) {
            throw new IllegalArgumentException("Unexpected parent type for " + erased.getName() + ": " + superClass.getClass().getName());
        }
        this._superClass = superClass;
        this._superInterfaces = interfaces == null ? NO_TYPES : interfaces;
        this._modifiers = erased.getModifiers();
    }

    @Deprecated
    public ResolvedObjectType(Class<?> erased, TypeBindings bindings, ResolvedObjectType superClass, List<ResolvedType> interfaces) {
        this(erased, bindings, (ResolvedType) superClass, interfaces);
    }

    @Deprecated
    public ResolvedObjectType(Class<?> erased, TypeBindings bindings, ResolvedObjectType superClass, ResolvedType[] interfaces) {
        this(erased, bindings, (ResolvedType) superClass, interfaces);
    }

    public static ResolvedObjectType create(Class<?> erased, TypeBindings bindings, ResolvedType superClass, List<ResolvedType> interfaces) {
        return new ResolvedObjectType(erased, bindings, superClass, interfaces);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean canCreateSubtypes() {
        return true;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public ResolvedObjectType getParentClass() {
        if (this._superClass == null) {
            return null;
        }
        if (this._superClass instanceof ResolvedObjectType) {
            return (ResolvedObjectType) this._superClass;
        }
        ResolvedType rt = ((ResolvedRecursiveType) this._superClass).getSelfReferencedType();
        if (!(rt instanceof ResolvedObjectType)) {
            throw new IllegalStateException("Internal error: self-referential parent type (" + this._superClass + ") does not resolve into proper ResolvedObjectType, but instead to: " + rt);
        }
        return (ResolvedObjectType) rt;
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
    public final ResolvedType getArrayElementType() {
        return null;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public final boolean isInterface() {
        return false;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public boolean isAbstract() {
        return Modifier.isAbstract(this._modifiers);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public final boolean isArray() {
        return false;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public final boolean isPrimitive() {
        return false;
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public synchronized List<RawField> getMemberFields() {
        if (this._memberFields == null) {
            this._memberFields = _getFields(false);
        }
        if (this._memberFields.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(this._memberFields);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public synchronized List<RawField> getStaticFields() {
        if (this._staticFields == null) {
            this._staticFields = _getFields(true);
        }
        if (this._staticFields.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(this._staticFields);
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
    public synchronized List<RawMethod> getStaticMethods() {
        if (this._staticMethods == null) {
            this._staticMethods = _getMethods(true);
        }
        if (this._staticMethods.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(this._staticMethods);
    }

    @Override // com.fasterxml.classmate.ResolvedType
    public List<RawConstructor> getConstructors() {
        if (this._constructors == null) {
            this._constructors = _getConstructors();
        }
        if (this._constructors.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(this._constructors);
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
        if (this._superClass != null) {
            sb2.append(" extends ");
            sb2 = this._superClass.appendBriefDescription(sb2);
        }
        int count = this._superInterfaces.length;
        if (count > 0) {
            sb2.append(" implements ");
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