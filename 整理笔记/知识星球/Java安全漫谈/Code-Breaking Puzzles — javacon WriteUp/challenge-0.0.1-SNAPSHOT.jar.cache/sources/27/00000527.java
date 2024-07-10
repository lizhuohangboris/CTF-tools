package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/type/ResolvedRecursiveType.class */
public class ResolvedRecursiveType extends TypeBase {
    private static final long serialVersionUID = 1;
    protected JavaType _referencedType;

    public ResolvedRecursiveType(Class<?> erasedType, TypeBindings bindings) {
        super(erasedType, bindings, null, null, 0, null, null, false);
    }

    public void setReference(JavaType ref) {
        if (this._referencedType != null) {
            throw new IllegalStateException("Trying to re-set self reference; old value = " + this._referencedType + ", new = " + ref);
        }
        this._referencedType = ref;
    }

    @Override // com.fasterxml.jackson.databind.type.TypeBase, com.fasterxml.jackson.databind.JavaType
    public JavaType getSuperClass() {
        if (this._referencedType != null) {
            return this._referencedType.getSuperClass();
        }
        return super.getSuperClass();
    }

    public JavaType getSelfReferencedType() {
        return this._referencedType;
    }

    @Override // com.fasterxml.jackson.databind.type.TypeBase, com.fasterxml.jackson.databind.JavaType
    public StringBuilder getGenericSignature(StringBuilder sb) {
        return this._referencedType.getGenericSignature(sb);
    }

    @Override // com.fasterxml.jackson.databind.type.TypeBase, com.fasterxml.jackson.databind.JavaType
    public StringBuilder getErasedSignature(StringBuilder sb) {
        return this._referencedType.getErasedSignature(sb);
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public JavaType withContentType(JavaType contentType) {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public JavaType withTypeHandler(Object h) {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public JavaType withContentTypeHandler(Object h) {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public JavaType withValueHandler(Object h) {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public JavaType withContentValueHandler(Object h) {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public JavaType withStaticTyping() {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    @Deprecated
    protected JavaType _narrow(Class<?> subclass) {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JavaType, com.fasterxml.jackson.core.type.ResolvedType
    public boolean isContainerType() {
        return false;
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public String toString() {
        StringBuilder sb = new StringBuilder(40).append("[recursive type; ");
        if (this._referencedType == null) {
            sb.append("UNRESOLVED");
        } else {
            sb.append(this._referencedType.getRawClass().getName());
        }
        return sb.toString();
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o != null && o.getClass() == getClass()) {
            return false;
        }
        return false;
    }
}