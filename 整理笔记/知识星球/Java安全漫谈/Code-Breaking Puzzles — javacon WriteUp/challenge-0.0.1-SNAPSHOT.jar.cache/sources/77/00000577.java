package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.IOException;
import java.util.OptionalLong;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.9.7.jar:com/fasterxml/jackson/datatype/jdk8/OptionalLongDeserializer.class */
public class OptionalLongDeserializer extends BaseScalarOptionalDeserializer<OptionalLong> {
    private static final long serialVersionUID = 1;
    static final OptionalLongDeserializer INSTANCE = new OptionalLongDeserializer();

    public OptionalLongDeserializer() {
        super(OptionalLong.class, OptionalLong.empty());
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public OptionalLong deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return OptionalLong.of(p.getLongValue());
        }
        switch (p.getCurrentTokenId()) {
            case 3:
                if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    p.nextToken();
                    OptionalLong parsed = deserialize(p, ctxt);
                    _verifyEndArrayForSingle(p, ctxt);
                    return parsed;
                }
                break;
            case 6:
                String text = p.getText().trim();
                if (text.length() == 0) {
                    _coerceEmptyString(ctxt, false);
                    return (OptionalLong) this._empty;
                } else if (_hasTextualNull(text)) {
                    _coerceTextualNull(ctxt, false);
                    return (OptionalLong) this._empty;
                } else {
                    return OptionalLong.of(_parseLongPrimitive(ctxt, text));
                }
            case 8:
                if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                    _failDoubleToIntCoercion(p, ctxt, "long");
                }
                return OptionalLong.of(p.getValueAsLong());
            case 11:
                return (OptionalLong) this._empty;
        }
        return (OptionalLong) ctxt.handleUnexpectedToken(this._valueClass, p);
    }
}