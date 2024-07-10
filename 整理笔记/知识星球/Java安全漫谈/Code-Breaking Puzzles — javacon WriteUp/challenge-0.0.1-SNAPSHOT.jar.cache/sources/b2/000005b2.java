package com.fasterxml.jackson.datatype.jsr310.ser.key;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/ser/key/ZonedDateTimeKeySerializer.class */
public class ZonedDateTimeKeySerializer extends JsonSerializer<ZonedDateTime> {
    public static final ZonedDateTimeKeySerializer INSTANCE = new ZonedDateTimeKeySerializer();

    private ZonedDateTimeKeySerializer() {
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeFieldName(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value));
    }
}