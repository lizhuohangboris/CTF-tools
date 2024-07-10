package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.IOException;
import java.util.OptionalDouble;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.9.7.jar:com/fasterxml/jackson/datatype/jdk8/OptionalDoubleDeserializer.class */
class OptionalDoubleDeserializer extends BaseScalarOptionalDeserializer<OptionalDouble> {
    private static final long serialVersionUID = 1;
    static final OptionalDoubleDeserializer INSTANCE = new OptionalDoubleDeserializer();

    public OptionalDoubleDeserializer() {
        super(OptionalDouble.class, OptionalDouble.empty());
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public OptionalDouble deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            return OptionalDouble.of(p.getDoubleValue());
        }
        switch (p.getCurrentTokenId()) {
            case 3:
                if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    p.nextToken();
                    OptionalDouble parsed = deserialize(p, ctxt);
                    _verifyEndArrayForSingle(p, ctxt);
                    return parsed;
                }
                break;
            case 6:
                String text = p.getText().trim();
                if (text.length() == 0) {
                    _coerceEmptyString(ctxt, false);
                    return (OptionalDouble) this._empty;
                } else if (_hasTextualNull(text)) {
                    _coerceTextualNull(ctxt, false);
                    return (OptionalDouble) this._empty;
                } else {
                    return OptionalDouble.of(_parseDoublePrimitive(ctxt, text));
                }
            case 7:
                return OptionalDouble.of(p.getDoubleValue());
            case 11:
                return (OptionalDouble) this._empty;
        }
        return (OptionalDouble) ctxt.handleUnexpectedToken(this._valueClass, p);
    }
}