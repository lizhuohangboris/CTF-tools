package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/deser/LocalTimeDeserializer.class */
public class LocalTimeDeserializer extends JSR310DateTimeDeserializerBase<LocalTime> {
    private static final long serialVersionUID = 1;
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;
    public static final LocalTimeDeserializer INSTANCE = new LocalTimeDeserializer();

    private LocalTimeDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    public LocalTimeDeserializer(DateTimeFormatter formatter) {
        super(LocalTime.class, formatter);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    protected JsonDeserializer<LocalTime> withDateFormat(DateTimeFormatter formatter) {
        return new LocalTimeDeserializer(formatter);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LocalTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        LocalTime result;
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            String string = parser.getText().trim();
            if (string.length() == 0) {
                return null;
            }
            DateTimeFormatter format = this._formatter;
            try {
                if (format == DEFAULT_FORMATTER && string.contains("T")) {
                    return LocalTime.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
                return LocalTime.parse(string, format);
            } catch (DateTimeException e) {
                return (LocalTime) _handleDateTimeException(context, e, string);
            }
        }
        if (parser.isExpectedStartArrayToken()) {
            JsonToken t = parser.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if (context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS) && (t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)) {
                LocalTime parsed = deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;
            } else if (t == JsonToken.VALUE_NUMBER_INT) {
                int hour = parser.getIntValue();
                parser.nextToken();
                int minute = parser.getIntValue();
                if (parser.nextToken() == JsonToken.END_ARRAY) {
                    result = LocalTime.of(hour, minute);
                } else {
                    int second = parser.getIntValue();
                    if (parser.nextToken() == JsonToken.END_ARRAY) {
                        result = LocalTime.of(hour, minute, second);
                    } else {
                        int partialSecond = parser.getIntValue();
                        if (partialSecond < 1000 && !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                            partialSecond *= 1000000;
                        }
                        if (parser.nextToken() != JsonToken.END_ARRAY) {
                            throw context.wrongTokenException(parser, handledType(), JsonToken.END_ARRAY, "Expected array to end");
                        }
                        result = LocalTime.of(hour, minute, second, partialSecond);
                    }
                }
                return result;
            } else {
                context.reportInputMismatch(handledType(), "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT", t);
            }
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (LocalTime) parser.getEmbeddedObject();
        }
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            _throwNoNumericTimestampNeedTimeZone(parser, context);
        }
        return (LocalTime) _handleUnexpectedToken(context, parser, "Expected array or string.", new Object[0]);
    }
}