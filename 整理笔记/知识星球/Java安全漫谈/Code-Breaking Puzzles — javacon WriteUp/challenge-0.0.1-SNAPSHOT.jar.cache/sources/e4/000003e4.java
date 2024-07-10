package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.util.Collection;

@JacksonStdImpl
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/StringCollectionDeserializer.class */
public final class StringCollectionDeserializer extends ContainerDeserializerBase<Collection<String>> implements ContextualDeserializer {
    private static final long serialVersionUID = 1;
    protected final JsonDeserializer<String> _valueDeserializer;
    protected final ValueInstantiator _valueInstantiator;
    protected final JsonDeserializer<Object> _delegateDeserializer;

    public StringCollectionDeserializer(JavaType collectionType, JsonDeserializer<?> valueDeser, ValueInstantiator valueInstantiator) {
        this(collectionType, valueInstantiator, null, valueDeser, valueDeser, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected StringCollectionDeserializer(JavaType collectionType, ValueInstantiator valueInstantiator, JsonDeserializer<?> delegateDeser, JsonDeserializer<?> valueDeser, NullValueProvider nuller, Boolean unwrapSingle) {
        super(collectionType, nuller, unwrapSingle);
        this._valueDeserializer = valueDeser;
        this._valueInstantiator = valueInstantiator;
        this._delegateDeserializer = delegateDeser;
    }

    protected StringCollectionDeserializer withResolved(JsonDeserializer<?> delegateDeser, JsonDeserializer<?> valueDeser, NullValueProvider nuller, Boolean unwrapSingle) {
        if (this._unwrapSingle == unwrapSingle && this._nullProvider == nuller && this._valueDeserializer == valueDeser && this._delegateDeserializer == delegateDeser) {
            return this;
        }
        return new StringCollectionDeserializer(this._containerType, this._valueInstantiator, delegateDeser, valueDeser, nuller, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public boolean isCachable() {
        return this._valueDeserializer == null && this._delegateDeserializer == null;
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> valueDeser;
        JsonDeserializer<Object> delegate = null;
        if (this._valueInstantiator != null) {
            AnnotatedWithParams delegateCreator = this._valueInstantiator.getDelegateCreator();
            if (delegateCreator != null) {
                JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
                delegate = findDeserializer(ctxt, delegateType, property);
            }
        }
        JsonDeserializer<?> valueDeser2 = this._valueDeserializer;
        JavaType valueType = this._containerType.getContentType();
        if (valueDeser2 == null) {
            valueDeser = findConvertingContentDeserializer(ctxt, property, valueDeser2);
            if (valueDeser == null) {
                valueDeser = ctxt.findContextualValueDeserializer(valueType, property);
            }
        } else {
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser2, property, valueType);
        }
        Boolean unwrapSingle = findFormatFeature(ctxt, property, Collection.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        NullValueProvider nuller = findContentNullProvider(ctxt, property, valueDeser);
        if (isDefaultDeserializer(valueDeser)) {
            valueDeser = null;
        }
        return withResolved(delegate, valueDeser, nuller, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase
    public JsonDeserializer<Object> getContentDeserializer() {
        JsonDeserializer<?> deser = this._valueDeserializer;
        return deser;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase, com.fasterxml.jackson.databind.deser.ValueInstantiator.Gettable
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Collection<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._delegateDeserializer != null) {
            return (Collection) this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        Collection<String> result = (Collection) this._valueInstantiator.createUsingDefault(ctxt);
        return deserialize(p, ctxt, result);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Collection<String> deserialize(JsonParser p, DeserializationContext ctxt, Collection<String> result) throws IOException {
        String value;
        if (!p.isExpectedStartArrayToken()) {
            return handleNonArray(p, ctxt, result);
        }
        if (this._valueDeserializer != null) {
            return deserializeUsingCustom(p, ctxt, result, this._valueDeserializer);
        }
        while (true) {
            try {
                String value2 = p.nextTextValue();
                if (value2 != null) {
                    result.add(value2);
                } else {
                    JsonToken t = p.getCurrentToken();
                    if (t != JsonToken.END_ARRAY) {
                        if (t == JsonToken.VALUE_NULL) {
                            if (!this._skipNullValues) {
                                value = (String) this._nullProvider.getNullValue(ctxt);
                            }
                        } else {
                            value = _parseString(p, ctxt);
                        }
                        result.add(value);
                    } else {
                        return result;
                    }
                }
            } catch (Exception e) {
                throw JsonMappingException.wrapWithPath(e, result, result.size());
            }
        }
    }

    private Collection<String> deserializeUsingCustom(JsonParser p, DeserializationContext ctxt, Collection<String> result, JsonDeserializer<String> deser) throws IOException {
        Object deserialize;
        while (true) {
            if (p.nextTextValue() == null) {
                JsonToken t = p.getCurrentToken();
                if (t != JsonToken.END_ARRAY) {
                    if (t == JsonToken.VALUE_NULL) {
                        if (!this._skipNullValues) {
                            deserialize = this._nullProvider.getNullValue(ctxt);
                        }
                    } else {
                        deserialize = deser.deserialize(p, ctxt);
                    }
                } else {
                    return result;
                }
            } else {
                deserialize = deser.deserialize(p, ctxt);
            }
            String value = (String) deserialize;
            result.add(value);
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    private final Collection<String> handleNonArray(JsonParser p, DeserializationContext ctxt, Collection<String> result) throws IOException {
        String value;
        boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (!canWrap) {
            return (Collection) ctxt.handleUnexpectedToken(this._containerType.getRawClass(), p);
        }
        JsonDeserializer<String> valueDes = this._valueDeserializer;
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.VALUE_NULL) {
            if (this._skipNullValues) {
                return result;
            }
            value = (String) this._nullProvider.getNullValue(ctxt);
        } else {
            value = valueDes == null ? _parseString(p, ctxt) : valueDes.deserialize(p, ctxt);
        }
        result.add(value);
        return result;
    }
}