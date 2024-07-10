package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/deser/LocalDateTimeDeserializer.class */
public class LocalDateTimeDeserializer extends JSR310DateTimeDeserializerBase<LocalDateTime> {
    private static final long serialVersionUID = 1;
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final LocalDateTimeDeserializer INSTANCE = new LocalDateTimeDeserializer();

    private LocalDateTimeDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    public LocalDateTimeDeserializer(DateTimeFormatter formatter) {
        super(LocalDateTime.class, formatter);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    protected JsonDeserializer<LocalDateTime> withDateFormat(DateTimeFormatter formatter) {
        return new LocalDateTimeDeserializer(formatter);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        LocalDateTime result;
        if (parser.hasTokenId(6)) {
            String string = parser.getText().trim();
            if (string.length() == 0) {
                return null;
            }
            try {
                if (this._formatter == DEFAULT_FORMATTER && string.length() > 10 && string.charAt(10) == 'T') {
                    if (string.endsWith("Z")) {
                        return LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC);
                    }
                    return LocalDateTime.parse(string, DEFAULT_FORMATTER);
                }
                return LocalDateTime.parse(string, this._formatter);
            } catch (DateTimeException e) {
                return (LocalDateTime) _handleDateTimeException(context, e, string);
            }
        }
        if (parser.isExpectedStartArrayToken()) {
            JsonToken t = parser.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT) && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                LocalDateTime parsed = deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;
            } else if (t == JsonToken.VALUE_NUMBER_INT) {
                int year = parser.getIntValue();
                int month = parser.nextIntValue(-1);
                int day = parser.nextIntValue(-1);
                int hour = parser.nextIntValue(-1);
                int minute = parser.nextIntValue(-1);
                if (parser.nextToken() == JsonToken.END_ARRAY) {
                    result = LocalDateTime.of(year, month, day, hour, minute);
                } else {
                    int second = parser.getIntValue();
                    if (parser.nextToken() == JsonToken.END_ARRAY) {
                        result = LocalDateTime.of(year, month, day, hour, minute, second);
                    } else {
                        int partialSecond = parser.getIntValue();
                        if (partialSecond < 1000 && !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                            partialSecond *= 1000000;
                        }
                        if (parser.nextToken() != JsonToken.END_ARRAY) {
                            throw context.wrongTokenException(parser, handledType(), JsonToken.END_ARRAY, "Expected array to end");
                        }
                        result = LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
                    }
                }
                return result;
            } else {
                context.reportInputMismatch(handledType(), "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT", t);
            }
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (LocalDateTime) parser.getEmbeddedObject();
        }
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            _throwNoNumericTimestampNeedTimeZone(parser, context);
        }
        return (LocalDateTime) _handleUnexpectedToken(context, parser, "Expected array or string.", new Object[0]);
    }
}