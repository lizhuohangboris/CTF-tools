package com.fasterxml.jackson.datatype.jsr310.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/deser/key/YearKeyDeserializer.class */
public class YearKeyDeserializer extends Jsr310KeyDeserializer {
    public static final YearKeyDeserializer INSTANCE = new YearKeyDeserializer();
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).toFormatter();

    private YearKeyDeserializer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.key.Jsr310KeyDeserializer
    public Year deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return Year.parse(key, FORMATTER);
        } catch (DateTimeException e) {
            return (Year) _handleDateTimeException(ctxt, Year.class, e, key);
        }
    }
}