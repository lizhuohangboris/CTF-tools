package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/deser/YearMonthDeserializer.class */
public class YearMonthDeserializer extends JSR310DateTimeDeserializerBase<YearMonth> {
    private static final long serialVersionUID = 1;
    public static final YearMonthDeserializer INSTANCE = new YearMonthDeserializer();

    private YearMonthDeserializer() {
        this(DateTimeFormatter.ofPattern("uuuu-MM"));
    }

    public YearMonthDeserializer(DateTimeFormatter formatter) {
        super(YearMonth.class, formatter);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    protected JsonDeserializer<YearMonth> withDateFormat(DateTimeFormatter dtf) {
        return new YearMonthDeserializer(dtf);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public YearMonth deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            String string = parser.getText().trim();
            if (string.length() == 0) {
                return null;
            }
            try {
                return YearMonth.parse(string, this._formatter);
            } catch (DateTimeException e) {
                return (YearMonth) _handleDateTimeException(context, e, string);
            }
        } else if (parser.isExpectedStartArrayToken()) {
            JsonToken t = parser.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT) && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                YearMonth parsed = deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;
            }
            if (t != JsonToken.VALUE_NUMBER_INT) {
                _reportWrongToken(context, JsonToken.VALUE_NUMBER_INT, "years");
            }
            int year = parser.getIntValue();
            int month = parser.nextIntValue(-1);
            if (month == -1) {
                if (!parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                    _reportWrongToken(context, JsonToken.VALUE_NUMBER_INT, "months");
                }
                month = parser.getIntValue();
            }
            if (parser.nextToken() != JsonToken.END_ARRAY) {
                throw context.wrongTokenException(parser, handledType(), JsonToken.END_ARRAY, "Expected array to end");
            }
            return YearMonth.of(year, month);
        } else if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (YearMonth) parser.getEmbeddedObject();
        } else {
            return (YearMonth) _handleUnexpectedToken(context, parser, JsonToken.VALUE_STRING, JsonToken.START_ARRAY);
        }
    }
}