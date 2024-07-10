package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import java.io.IOException;

@JacksonStdImpl
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/StringArrayDeserializer.class */
public final class StringArrayDeserializer extends StdDeserializer<String[]> implements ContextualDeserializer {
    private static final long serialVersionUID = 2;
    private static final String[] NO_STRINGS = new String[0];
    public static final StringArrayDeserializer instance = new StringArrayDeserializer();
    protected JsonDeserializer<String> _elementDeserializer;
    protected final NullValueProvider _nullProvider;
    protected final Boolean _unwrapSingle;
    protected final boolean _skipNullValues;

    public StringArrayDeserializer() {
        this(null, null, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected StringArrayDeserializer(JsonDeserializer<?> deser, NullValueProvider nuller, Boolean unwrapSingle) {
        super(String[].class);
        this._elementDeserializer = deser;
        this._nullProvider = nuller;
        this._unwrapSingle = unwrapSingle;
        this._skipNullValues = NullsConstantProvider.isSkipper(nuller);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.TRUE;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.CONSTANT;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return NO_STRINGS;
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> deser;
        JsonDeserializer<?> deser2 = findConvertingContentDeserializer(ctxt, property, this._elementDeserializer);
        JavaType type = ctxt.constructType(String.class);
        if (deser2 == null) {
            deser = ctxt.findContextualValueDeserializer(type, property);
        } else {
            deser = ctxt.handleSecondaryContextualization(deser2, property, type);
        }
        Boolean unwrapSingle = findFormatFeature(ctxt, property, String[].class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        NullValueProvider nuller = findContentNullProvider(ctxt, property, deser);
        if (deser != null && isDefaultDeserializer(deser)) {
            deser = null;
        }
        if (this._elementDeserializer == deser && this._unwrapSingle == unwrapSingle && this._nullProvider == nuller) {
            return this;
        }
        return new StringArrayDeserializer(deser, nuller, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public String[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return handleNonArray(p, ctxt);
        }
        if (this._elementDeserializer != null) {
            return _deserializeCustom(p, ctxt, null);
        }
        ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] chunk = buffer.resetAndStart();
        int ix = 0;
        while (true) {
            try {
                String value = p.nextTextValue();
                if (value == null) {
                    JsonToken t = p.getCurrentToken();
                    if (t != JsonToken.END_ARRAY) {
                        if (t == JsonToken.VALUE_NULL) {
                            if (!this._skipNullValues) {
                                value = (String) this._nullProvider.getNullValue(ctxt);
                            }
                        } else {
                            value = _parseString(p, ctxt);
                        }
                    } else {
                        String[] result = (String[]) buffer.completeAndClearBuffer(chunk, ix, String.class);
                        ctxt.returnObjectBuffer(buffer);
                        return result;
                    }
                }
                if (ix >= chunk.length) {
                    chunk = buffer.appendCompletedChunk(chunk);
                    ix = 0;
                }
                int i = ix;
                ix++;
                chunk[i] = value;
            } catch (Exception e) {
                throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
            }
        }
    }

    protected final String[] _deserializeCustom(JsonParser p, DeserializationContext ctxt, String[] old) throws IOException {
        int ix;
        Object[] chunk;
        String value;
        ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        if (old == null) {
            ix = 0;
            chunk = buffer.resetAndStart();
        } else {
            ix = old.length;
            chunk = buffer.resetAndStart(old, ix);
        }
        JsonDeserializer<String> deser = this._elementDeserializer;
        while (true) {
            try {
                if (p.nextTextValue() == null) {
                    JsonToken t = p.getCurrentToken();
                    if (t != JsonToken.END_ARRAY) {
                        if (t == JsonToken.VALUE_NULL) {
                            if (!this._skipNullValues) {
                                value = (String) this._nullProvider.getNullValue(ctxt);
                            }
                        } else {
                            value = deser.deserialize(p, ctxt);
                        }
                    } else {
                        String[] result = (String[]) buffer.completeAndClearBuffer(chunk, ix, String.class);
                        ctxt.returnObjectBuffer(buffer);
                        return result;
                    }
                } else {
                    value = deser.deserialize(p, ctxt);
                }
                if (ix >= chunk.length) {
                    chunk = buffer.appendCompletedChunk(chunk);
                    ix = 0;
                }
                int i = ix;
                ix++;
                chunk[i] = value;
            } catch (Exception e) {
                throw JsonMappingException.wrapWithPath(e, String.class, ix);
            }
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public String[] deserialize(JsonParser p, DeserializationContext ctxt, String[] intoValue) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            String[] arr = handleNonArray(p, ctxt);
            if (arr == null) {
                return intoValue;
            }
            int offset = intoValue.length;
            String[] result = new String[offset + arr.length];
            System.arraycopy(intoValue, 0, result, 0, offset);
            System.arraycopy(arr, 0, result, offset, arr.length);
            return result;
        } else if (this._elementDeserializer != null) {
            return _deserializeCustom(p, ctxt, intoValue);
        } else {
            ObjectBuffer buffer = ctxt.leaseObjectBuffer();
            int ix = intoValue.length;
            Object[] chunk = buffer.resetAndStart(intoValue, ix);
            while (true) {
                try {
                    String value = p.nextTextValue();
                    if (value == null) {
                        JsonToken t = p.getCurrentToken();
                        if (t != JsonToken.END_ARRAY) {
                            if (t == JsonToken.VALUE_NULL) {
                                if (this._skipNullValues) {
                                    return NO_STRINGS;
                                }
                                value = (String) this._nullProvider.getNullValue(ctxt);
                            } else {
                                value = _parseString(p, ctxt);
                            }
                        } else {
                            String[] result2 = (String[]) buffer.completeAndClearBuffer(chunk, ix, String.class);
                            ctxt.returnObjectBuffer(buffer);
                            return result2;
                        }
                    }
                    if (ix >= chunk.length) {
                        chunk = buffer.appendCompletedChunk(chunk);
                        ix = 0;
                    }
                    int i = ix;
                    ix++;
                    chunk[i] = value;
                } catch (Exception e) {
                    throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
                }
            }
        }
    }

    private final String[] handleNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (canWrap) {
            String value = p.hasToken(JsonToken.VALUE_NULL) ? (String) this._nullProvider.getNullValue(ctxt) : _parseString(p, ctxt);
            return new String[]{value};
        }
        if (p.hasToken(JsonToken.VALUE_STRING) && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            String str = p.getText();
            if (str.length() == 0) {
                return null;
            }
        }
        return (String[]) ctxt.handleUnexpectedToken(this._valueClass, p);
    }
}