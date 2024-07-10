package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.util.EnumSet;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/EnumSetDeserializer.class */
public class EnumSetDeserializer extends StdDeserializer<EnumSet<?>> implements ContextualDeserializer {
    private static final long serialVersionUID = 1;
    protected final JavaType _enumType;
    protected final Class<Enum> _enumClass;
    protected JsonDeserializer<Enum<?>> _enumDeserializer;
    protected final Boolean _unwrapSingle;

    /* JADX WARN: Multi-variable type inference failed */
    public EnumSetDeserializer(JavaType enumType, JsonDeserializer<?> deser) {
        super(EnumSet.class);
        this._enumType = enumType;
        this._enumClass = enumType.getRawClass();
        if (!this._enumClass.isEnum()) {
            throw new IllegalArgumentException("Type " + enumType + " not Java Enum type");
        }
        this._enumDeserializer = deser;
        this._unwrapSingle = null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected EnumSetDeserializer(EnumSetDeserializer base, JsonDeserializer<?> deser, Boolean unwrapSingle) {
        super(base);
        this._enumType = base._enumType;
        this._enumClass = base._enumClass;
        this._enumDeserializer = deser;
        this._unwrapSingle = unwrapSingle;
    }

    public EnumSetDeserializer withDeserializer(JsonDeserializer<?> deser) {
        if (this._enumDeserializer == deser) {
            return this;
        }
        return new EnumSetDeserializer(this, deser, this._unwrapSingle);
    }

    public EnumSetDeserializer withResolved(JsonDeserializer<?> deser, Boolean unwrapSingle) {
        if (this._unwrapSingle == unwrapSingle && this._enumDeserializer == deser) {
            return this;
        }
        return new EnumSetDeserializer(this, deser, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public boolean isCachable() {
        if (this._enumType.getValueHandler() != null) {
            return false;
        }
        return true;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.TRUE;
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> deser;
        Boolean unwrapSingle = findFormatFeature(ctxt, property, EnumSet.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        JsonDeserializer<?> deser2 = this._enumDeserializer;
        if (deser2 == null) {
            deser = ctxt.findContextualValueDeserializer(this._enumType, property);
        } else {
            deser = ctxt.handleSecondaryContextualization(deser2, property, this._enumType);
        }
        return withResolved(deser, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public EnumSet<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        EnumSet result = constructSet();
        if (!p.isExpectedStartArrayToken()) {
            return handleNonArray(p, ctxt, result);
        }
        return _deserialize(p, ctxt, result);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public EnumSet<?> deserialize(JsonParser p, DeserializationContext ctxt, EnumSet<?> result) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return handleNonArray(p, ctxt, result);
        }
        return _deserialize(p, ctxt, result);
    }

    protected final EnumSet<?> _deserialize(JsonParser p, DeserializationContext ctxt, EnumSet result) throws IOException {
        while (true) {
            try {
                JsonToken t = p.nextToken();
                if (t != JsonToken.END_ARRAY) {
                    if (t == JsonToken.VALUE_NULL) {
                        return (EnumSet) ctxt.handleUnexpectedToken(this._enumClass, p);
                    }
                    Enum<?> value = this._enumDeserializer.deserialize(p, ctxt);
                    if (value != null) {
                        result.add(value);
                    }
                } else {
                    return result;
                }
            } catch (Exception e) {
                throw JsonMappingException.wrapWithPath(e, result, result.size());
            }
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    private EnumSet constructSet() {
        return EnumSet.noneOf(this._enumClass);
    }

    protected EnumSet<?> handleNonArray(JsonParser p, DeserializationContext ctxt, EnumSet result) throws IOException {
        boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (!canWrap) {
            return (EnumSet) ctxt.handleUnexpectedToken(EnumSet.class, p);
        }
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            return (EnumSet) ctxt.handleUnexpectedToken(this._enumClass, p);
        }
        try {
            Enum<?> value = this._enumDeserializer.deserialize(p, ctxt);
            if (value != null) {
                result.add(value);
            }
            return result;
        } catch (Exception e) {
            throw JsonMappingException.wrapWithPath(e, result, result.size());
        }
    }
}