package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.annotation.NoClass;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.util.Collection;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/jsontype/impl/StdTypeResolverBuilder.class */
public class StdTypeResolverBuilder implements TypeResolverBuilder<StdTypeResolverBuilder> {
    protected JsonTypeInfo.Id _idType;
    protected JsonTypeInfo.As _includeAs;
    protected String _typeProperty;
    protected boolean _typeIdVisible = false;
    protected Class<?> _defaultImpl;
    protected TypeIdResolver _customIdResolver;

    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public /* bridge */ /* synthetic */ StdTypeResolverBuilder defaultImpl(Class x0) {
        return defaultImpl((Class<?>) x0);
    }

    public StdTypeResolverBuilder() {
    }

    protected StdTypeResolverBuilder(JsonTypeInfo.Id idType, JsonTypeInfo.As idAs, String propName) {
        this._idType = idType;
        this._includeAs = idAs;
        this._typeProperty = propName;
    }

    public static StdTypeResolverBuilder noTypeInfoBuilder() {
        return new StdTypeResolverBuilder().init(JsonTypeInfo.Id.NONE, (TypeIdResolver) null);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public StdTypeResolverBuilder init(JsonTypeInfo.Id idType, TypeIdResolver idRes) {
        if (idType == null) {
            throw new IllegalArgumentException("idType cannot be null");
        }
        this._idType = idType;
        this._customIdResolver = idRes;
        this._typeProperty = idType.getDefaultPropertyName();
        return this;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
        if (this._idType == JsonTypeInfo.Id.NONE || baseType.isPrimitive()) {
            return null;
        }
        TypeIdResolver idRes = idResolver(config, baseType, subtypes, true, false);
        switch (this._includeAs) {
            case WRAPPER_ARRAY:
                return new AsArrayTypeSerializer(idRes, null);
            case PROPERTY:
                return new AsPropertyTypeSerializer(idRes, null, this._typeProperty);
            case WRAPPER_OBJECT:
                return new AsWrapperTypeSerializer(idRes, null);
            case EXTERNAL_PROPERTY:
                return new AsExternalTypeSerializer(idRes, null, this._typeProperty);
            case EXISTING_PROPERTY:
                return new AsExistingPropertyTypeSerializer(idRes, null, this._typeProperty);
            default:
                throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
        }
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
        if (this._idType == JsonTypeInfo.Id.NONE || baseType.isPrimitive()) {
            return null;
        }
        TypeIdResolver idRes = idResolver(config, baseType, subtypes, false, true);
        JavaType defaultImpl = defineDefaultImpl(config, baseType);
        switch (this._includeAs) {
            case WRAPPER_ARRAY:
                return new AsArrayTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
            case PROPERTY:
            case EXISTING_PROPERTY:
                return new AsPropertyTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl, this._includeAs);
            case WRAPPER_OBJECT:
                return new AsWrapperTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
            case EXTERNAL_PROPERTY:
                return new AsExternalTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
            default:
                throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
        }
    }

    protected JavaType defineDefaultImpl(DeserializationConfig config, JavaType baseType) {
        JavaType defaultImpl;
        if (this._defaultImpl == null) {
            if (config.isEnabled(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL) && !baseType.isAbstract()) {
                defaultImpl = baseType;
            } else {
                defaultImpl = null;
            }
        } else if (this._defaultImpl == Void.class || this._defaultImpl == NoClass.class) {
            defaultImpl = config.getTypeFactory().constructType(this._defaultImpl);
        } else if (baseType.hasRawClass(this._defaultImpl)) {
            defaultImpl = baseType;
        } else if (baseType.isTypeOrSuperTypeOf(this._defaultImpl)) {
            defaultImpl = config.getTypeFactory().constructSpecializedType(baseType, this._defaultImpl);
        } else {
            defaultImpl = null;
        }
        return defaultImpl;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public StdTypeResolverBuilder inclusion(JsonTypeInfo.As includeAs) {
        if (includeAs == null) {
            throw new IllegalArgumentException("includeAs cannot be null");
        }
        this._includeAs = includeAs;
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public StdTypeResolverBuilder typeProperty(String typeIdPropName) {
        if (typeIdPropName == null || typeIdPropName.length() == 0) {
            typeIdPropName = this._idType.getDefaultPropertyName();
        }
        this._typeProperty = typeIdPropName;
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public StdTypeResolverBuilder defaultImpl(Class<?> defaultImpl) {
        this._defaultImpl = defaultImpl;
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public StdTypeResolverBuilder typeIdVisibility(boolean isVisible) {
        this._typeIdVisible = isVisible;
        return this;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public Class<?> getDefaultImpl() {
        return this._defaultImpl;
    }

    public String getTypeProperty() {
        return this._typeProperty;
    }

    public boolean isTypeIdVisible() {
        return this._typeIdVisible;
    }

    protected TypeIdResolver idResolver(MapperConfig<?> config, JavaType baseType, Collection<NamedType> subtypes, boolean forSer, boolean forDeser) {
        if (this._customIdResolver != null) {
            return this._customIdResolver;
        }
        if (this._idType == null) {
            throw new IllegalStateException("Cannot build, 'init()' not yet called");
        }
        switch (this._idType) {
            case CLASS:
                return new ClassNameIdResolver(baseType, config.getTypeFactory());
            case MINIMAL_CLASS:
                return new MinimalClassNameIdResolver(baseType, config.getTypeFactory());
            case NAME:
                return TypeNameIdResolver.construct(config, baseType, subtypes, forSer, forDeser);
            case NONE:
                return null;
            case CUSTOM:
            default:
                throw new IllegalStateException("Do not know how to construct standard type id resolver for idType: " + this._idType);
        }
    }
}