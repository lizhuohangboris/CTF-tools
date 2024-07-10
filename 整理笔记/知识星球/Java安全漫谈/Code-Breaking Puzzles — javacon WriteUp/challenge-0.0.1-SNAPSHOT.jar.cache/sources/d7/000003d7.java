package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/StdDelegatingDeserializer.class */
public class StdDelegatingDeserializer<T> extends StdDeserializer<T> implements ContextualDeserializer, ResolvableDeserializer {
    private static final long serialVersionUID = 1;
    protected final Converter<Object, T> _converter;
    protected final JavaType _delegateType;
    protected final JsonDeserializer<Object> _delegateDeserializer;

    public StdDelegatingDeserializer(Converter<?, T> converter) {
        super(Object.class);
        this._converter = converter;
        this._delegateType = null;
        this._delegateDeserializer = null;
    }

    public StdDelegatingDeserializer(Converter<Object, T> converter, JavaType delegateType, JsonDeserializer<?> delegateDeserializer) {
        super(delegateType);
        this._converter = converter;
        this._delegateType = delegateType;
        this._delegateDeserializer = delegateDeserializer;
    }

    protected StdDelegatingDeserializer(StdDelegatingDeserializer<T> src) {
        super(src);
        this._converter = src._converter;
        this._delegateType = src._delegateType;
        this._delegateDeserializer = src._delegateDeserializer;
    }

    protected StdDelegatingDeserializer<T> withDelegate(Converter<Object, T> converter, JavaType delegateType, JsonDeserializer<?> delegateDeserializer) {
        ClassUtil.verifyMustOverride(StdDelegatingDeserializer.class, this, "withDelegate");
        return new StdDelegatingDeserializer<>(converter, delegateType, delegateDeserializer);
    }

    @Override // com.fasterxml.jackson.databind.deser.ResolvableDeserializer
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        if (this._delegateDeserializer != null && (this._delegateDeserializer instanceof ResolvableDeserializer)) {
            ((ResolvableDeserializer) this._delegateDeserializer).resolve(ctxt);
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        if (this._delegateDeserializer != null) {
            JsonDeserializer<?> deser = ctxt.handleSecondaryContextualization(this._delegateDeserializer, property, this._delegateType);
            if (deser != this._delegateDeserializer) {
                return withDelegate(this._converter, this._delegateType, deser);
            }
            return this;
        }
        JavaType delegateType = this._converter.getInputType(ctxt.getTypeFactory());
        return withDelegate(this._converter, delegateType, ctxt.findContextualValueDeserializer(delegateType, property));
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public JsonDeserializer<?> getDelegatee() {
        return this._delegateDeserializer;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Class<?> handledType() {
        return this._delegateDeserializer.handledType();
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return this._delegateDeserializer.supportsUpdate(config);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object delegateValue = this._delegateDeserializer.deserialize(p, ctxt);
        if (delegateValue == null) {
            return null;
        }
        return convertValue(delegateValue);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        Object delegateValue = this._delegateDeserializer.deserialize(p, ctxt);
        if (delegateValue == null) {
            return null;
        }
        return convertValue(delegateValue);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public T deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue) throws IOException {
        if (this._delegateType.getRawClass().isAssignableFrom(intoValue.getClass())) {
            return (T) this._delegateDeserializer.deserialize(p, ctxt, intoValue);
        }
        return (T) _handleIncompatibleUpdateValue(p, ctxt, intoValue);
    }

    protected Object _handleIncompatibleUpdateValue(JsonParser p, DeserializationContext ctxt, Object intoValue) throws IOException {
        throw new UnsupportedOperationException(String.format("Cannot update object of type %s (using deserializer for type %s)" + intoValue.getClass().getName(), this._delegateType));
    }

    protected T convertValue(Object delegateValue) {
        return this._converter.convert(delegateValue);
    }
}