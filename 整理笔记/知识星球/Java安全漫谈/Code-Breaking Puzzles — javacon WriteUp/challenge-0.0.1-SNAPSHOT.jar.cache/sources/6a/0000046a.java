package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.JsonParserSequence;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/jsontype/impl/AsPropertyTypeDeserializer.class */
public class AsPropertyTypeDeserializer extends AsArrayTypeDeserializer {
    private static final long serialVersionUID = 1;
    protected final JsonTypeInfo.As _inclusion;

    public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl) {
        this(bt, idRes, typePropertyName, typeIdVisible, defaultImpl, JsonTypeInfo.As.PROPERTY);
    }

    public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl, JsonTypeInfo.As inclusion) {
        super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
        this._inclusion = inclusion;
    }

    public AsPropertyTypeDeserializer(AsPropertyTypeDeserializer src, BeanProperty property) {
        super(src, property);
        this._inclusion = src._inclusion;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.impl.AsArrayTypeDeserializer, com.fasterxml.jackson.databind.jsontype.impl.TypeDeserializerBase, com.fasterxml.jackson.databind.jsontype.TypeDeserializer
    public TypeDeserializer forProperty(BeanProperty prop) {
        return prop == this._property ? this : new AsPropertyTypeDeserializer(this, prop);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.impl.AsArrayTypeDeserializer, com.fasterxml.jackson.databind.jsontype.impl.TypeDeserializerBase, com.fasterxml.jackson.databind.jsontype.TypeDeserializer
    public JsonTypeInfo.As getTypeInclusion() {
        return this._inclusion;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.impl.AsArrayTypeDeserializer, com.fasterxml.jackson.databind.jsontype.TypeDeserializer
    public Object deserializeTypedFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object typeId;
        if (p.canReadTypeId() && (typeId = p.getTypeId()) != null) {
            return _deserializeWithNativeTypeId(p, ctxt, typeId);
        }
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        } else if (t != JsonToken.FIELD_NAME) {
            return _deserializeTypedUsingDefaultImpl(p, ctxt, null);
        }
        TokenBuffer tb = null;
        while (t == JsonToken.FIELD_NAME) {
            String name = p.getCurrentName();
            p.nextToken();
            if (name.equals(this._typePropertyName)) {
                return _deserializeTypedForId(p, ctxt, tb);
            }
            if (tb == null) {
                tb = new TokenBuffer(p, ctxt);
            }
            tb.writeFieldName(name);
            tb.copyCurrentStructure(p);
            t = p.nextToken();
        }
        return _deserializeTypedUsingDefaultImpl(p, ctxt, tb);
    }

    protected Object _deserializeTypedForId(JsonParser p, DeserializationContext ctxt, TokenBuffer tb) throws IOException {
        String typeId = p.getText();
        JsonDeserializer<Object> deser = _findDeserializer(ctxt, typeId);
        if (this._typeIdVisible) {
            if (tb == null) {
                tb = new TokenBuffer(p, ctxt);
            }
            tb.writeFieldName(p.getCurrentName());
            tb.writeString(typeId);
        }
        if (tb != null) {
            p.clearCurrentToken();
            p = JsonParserSequence.createFlattened(false, tb.asParser(p), p);
        }
        p.nextToken();
        return deser.deserialize(p, ctxt);
    }

    protected Object _deserializeTypedUsingDefaultImpl(JsonParser p, DeserializationContext ctxt, TokenBuffer tb) throws IOException {
        JsonDeserializer<Object> deser = _findDefaultImplDeserializer(ctxt);
        if (deser == null) {
            Object result = TypeDeserializer.deserializeIfNatural(p, ctxt, this._baseType);
            if (result != null) {
                return result;
            }
            if (p.isExpectedStartArrayToken()) {
                return super.deserializeTypedFromAny(p, ctxt);
            }
            if (p.hasToken(JsonToken.VALUE_STRING) && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
                String str = p.getText().trim();
                if (str.isEmpty()) {
                    return null;
                }
            }
            String msg = String.format("missing type id property '%s'", this._typePropertyName);
            if (this._property != null) {
                msg = String.format("%s (for POJO property '%s')", msg, this._property.getName());
            }
            JavaType t = _handleMissingTypeId(ctxt, msg);
            if (t == null) {
                return null;
            }
            deser = ctxt.findContextualValueDeserializer(t, this._property);
        }
        if (tb != null) {
            tb.writeEndObject();
            p = tb.asParser(p);
            p.nextToken();
        }
        return deser.deserialize(p, ctxt);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.impl.AsArrayTypeDeserializer, com.fasterxml.jackson.databind.jsontype.TypeDeserializer
    public Object deserializeTypedFromAny(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.getCurrentToken() == JsonToken.START_ARRAY) {
            return super.deserializeTypedFromArray(p, ctxt);
        }
        return deserializeTypedFromObject(p, ctxt);
    }
}