package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;

@JacksonStdImpl
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/StringDeserializer.class */
public class StringDeserializer extends StdScalarDeserializer<String> {
    private static final long serialVersionUID = 1;
    public static final StringDeserializer instance = new StringDeserializer();

    public StringDeserializer() {
        super(String.class);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public boolean isCachable() {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return "";
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text;
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return p.getText();
        }
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_ARRAY) {
            return _deserializeFromArray(p, ctxt);
        }
        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Object ob = p.getEmbeddedObject();
            if (ob == null) {
                return null;
            }
            if (ob instanceof byte[]) {
                return ctxt.getBase64Variant().encode((byte[]) ob, false);
            }
            return ob.toString();
        } else if (t.isScalarValue() && (text = p.getValueAsString()) != null) {
            return text;
        } else {
            return (String) ctxt.handleUnexpectedToken(this._valueClass, p);
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public String deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return deserialize(p, ctxt);
    }
}