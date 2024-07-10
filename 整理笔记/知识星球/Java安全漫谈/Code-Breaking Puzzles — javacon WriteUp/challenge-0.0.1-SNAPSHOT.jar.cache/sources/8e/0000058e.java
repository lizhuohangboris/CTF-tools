package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/deser/MonthDayDeserializer.class */
public class MonthDayDeserializer extends JSR310DateTimeDeserializerBase<MonthDay> {
    private static final long serialVersionUID = 1;
    public static final MonthDayDeserializer INSTANCE = new MonthDayDeserializer(null);

    public MonthDayDeserializer(DateTimeFormatter formatter) {
        super(MonthDay.class, formatter);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    protected JsonDeserializer<MonthDay> withDateFormat(DateTimeFormatter dtf) {
        return new MonthDayDeserializer(dtf);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public MonthDay deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            String string = parser.getValueAsString().trim();
            try {
                if (this._formatter == null) {
                    return MonthDay.parse(string);
                }
                return MonthDay.parse(string, this._formatter);
            } catch (DateTimeException e) {
                return (MonthDay) _handleDateTimeException(context, e, string);
            }
        } else if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (MonthDay) parser.getEmbeddedObject();
        } else {
            if (parser.hasToken(JsonToken.START_ARRAY)) {
                return _deserializeFromArray(parser, context);
            }
            return (MonthDay) _handleUnexpectedToken(context, parser, JsonToken.VALUE_STRING, JsonToken.VALUE_NUMBER_INT);
        }
    }
}