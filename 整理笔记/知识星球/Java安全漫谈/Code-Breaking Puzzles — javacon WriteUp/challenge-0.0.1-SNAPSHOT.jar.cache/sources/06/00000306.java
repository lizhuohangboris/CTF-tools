package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.util.Collection;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/JsonDeserializer.class */
public abstract class JsonDeserializer<T> implements NullValueProvider {
    public abstract T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException;

    public T deserialize(JsonParser p, DeserializationContext ctxt, T intoValue) throws IOException {
        if (ctxt.isEnabled(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)) {
            return deserialize(p, ctxt);
        }
        throw new UnsupportedOperationException("Cannot update object of type " + intoValue.getClass().getName() + " (by deserializer of type " + getClass().getName() + ")");
    }

    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }

    public JsonDeserializer<T> unwrappingDeserializer(NameTransformer unwrapper) {
        return this;
    }

    public JsonDeserializer<?> replaceDelegatee(JsonDeserializer<?> delegatee) {
        throw new UnsupportedOperationException();
    }

    public Class<?> handledType() {
        return null;
    }

    public boolean isCachable() {
        return false;
    }

    public JsonDeserializer<?> getDelegatee() {
        return null;
    }

    public Collection<Object> getKnownPropertyNames() {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.deser.NullValueProvider
    public T getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        return getNullValue();
    }

    @Override // com.fasterxml.jackson.databind.deser.NullValueProvider
    public AccessPattern getNullAccessPattern() {
        return AccessPattern.CONSTANT;
    }

    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.DYNAMIC;
    }

    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return getNullValue(ctxt);
    }

    public ObjectIdReader getObjectIdReader() {
        return null;
    }

    public SettableBeanProperty findBackReference(String refName) {
        throw new IllegalArgumentException("Cannot handle managed/back reference '" + refName + "': type: value deserializer of type " + getClass().getName() + " does not support them");
    }

    public Boolean supportsUpdate(DeserializationConfig config) {
        return null;
    }

    @Deprecated
    public T getNullValue() {
        return null;
    }

    @Deprecated
    public Object getEmptyValue() {
        return getNullValue();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/JsonDeserializer$None.class */
    public static abstract class None extends JsonDeserializer<Object> {
        private None() {
        }
    }
}