package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/ReferenceTypeDeserializer.class */
public abstract class ReferenceTypeDeserializer<T> extends StdDeserializer<T> implements ContextualDeserializer {
    private static final long serialVersionUID = 2;
    protected final JavaType _fullType;
    protected final ValueInstantiator _valueInstantiator;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final JsonDeserializer<Object> _valueDeserializer;

    protected abstract ReferenceTypeDeserializer<T> withResolved(TypeDeserializer typeDeserializer, JsonDeserializer<?> jsonDeserializer);

    @Override // com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
    public abstract T getNullValue(DeserializationContext deserializationContext);

    public abstract T referenceValue(Object obj);

    public abstract T updateReference(T t, Object obj);

    public abstract Object getReferenced(T t);

    public ReferenceTypeDeserializer(JavaType fullType, ValueInstantiator vi, TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
        super(fullType);
        this._valueInstantiator = vi;
        this._fullType = fullType;
        this._valueDeserializer = deser;
        this._valueTypeDeserializer = typeDeser;
    }

    @Deprecated
    public ReferenceTypeDeserializer(JavaType fullType, TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
        this(fullType, null, typeDeser, deser);
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> deser;
        JsonDeserializer<?> deser2 = this._valueDeserializer;
        if (deser2 == null) {
            deser = ctxt.findContextualValueDeserializer(this._fullType.getReferencedType(), property);
        } else {
            deser = ctxt.handleSecondaryContextualization(deser2, property, this._fullType.getReferencedType());
        }
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(property);
        }
        if (deser == this._valueDeserializer && typeDeser == this._valueTypeDeserializer) {
            return this;
        }
        return withResolved(typeDeser, deser);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
    public AccessPattern getNullAccessPattern() {
        return AccessPattern.DYNAMIC;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.DYNAMIC;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object getEmptyValue(DeserializationContext ctxt) {
        return getNullValue(ctxt);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer
    public JavaType getValueType() {
        return this._fullType;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        if (this._valueDeserializer == null) {
            return null;
        }
        return this._valueDeserializer.supportsUpdate(config);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._valueInstantiator != null) {
            return (T) deserialize(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
        }
        Object contents = this._valueTypeDeserializer == null ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
        return (T) referenceValue(contents);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public T deserialize(JsonParser p, DeserializationContext ctxt, T reference) throws IOException {
        Object contents;
        Boolean B = this._valueDeserializer.supportsUpdate(ctxt.getConfig());
        if (B.equals(Boolean.FALSE) || this._valueTypeDeserializer != null) {
            contents = this._valueTypeDeserializer == null ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
        } else {
            Object contents2 = getReferenced(reference);
            if (contents2 == null) {
                Object contents3 = this._valueTypeDeserializer == null ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
                return referenceValue(contents3);
            }
            contents = this._valueDeserializer.deserialize(p, ctxt, contents2);
        }
        return updateReference(reference, contents);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.VALUE_NULL) {
            return getNullValue(ctxt);
        }
        if (this._valueTypeDeserializer == null) {
            return deserialize(p, ctxt);
        }
        return referenceValue(this._valueTypeDeserializer.deserializeTypedFromAny(p, ctxt));
    }
}