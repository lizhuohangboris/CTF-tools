package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/deser/JSR310StringParsableDeserializer.class */
public class JSR310StringParsableDeserializer extends JSR310DeserializerBase<Object> {
    private static final long serialVersionUID = 1;
    protected static final int TYPE_PERIOD = 1;
    protected static final int TYPE_ZONE_ID = 2;
    protected static final int TYPE_ZONE_OFFSET = 3;
    public static final JsonDeserializer<Period> PERIOD = createDeserializer(Period.class, 1);
    public static final JsonDeserializer<ZoneId> ZONE_ID = createDeserializer(ZoneId.class, 2);
    public static final JsonDeserializer<ZoneOffset> ZONE_OFFSET = createDeserializer(ZoneOffset.class, 3);
    protected final int _valueType;

    protected JSR310StringParsableDeserializer(Class<?> supportedType, int valueId) {
        super(supportedType);
        this._valueType = valueId;
    }

    protected static <T> JsonDeserializer<T> createDeserializer(Class<T> type, int typeId) {
        return new JSR310StringParsableDeserializer(type, typeId);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            String string = parser.getText().trim();
            if (string.length() == 0) {
                return null;
            }
            try {
                switch (this._valueType) {
                    case 1:
                        return Period.parse(string);
                    case 2:
                        return ZoneId.of(string);
                    case 3:
                        return ZoneOffset.of(string);
                }
            } catch (DateTimeException e) {
                return _handleDateTimeException(context, e, string);
            }
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return parser.getEmbeddedObject();
        }
        if (parser.hasToken(JsonToken.START_ARRAY)) {
            return _deserializeFromArray(parser, context);
        }
        throw context.wrongTokenException(parser, handledType(), JsonToken.VALUE_STRING, (String) null);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer deserializer) throws IOException {
        JsonToken t = parser.getCurrentToken();
        if (t != null && t.isScalarValue()) {
            return deserialize(parser, context);
        }
        return deserializer.deserializeTypedFromAny(parser, context);
    }
}