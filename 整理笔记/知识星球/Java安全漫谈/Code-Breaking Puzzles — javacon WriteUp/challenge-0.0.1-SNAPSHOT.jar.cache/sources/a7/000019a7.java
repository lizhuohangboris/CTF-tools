package org.springframework.boot.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jackson/JsonObjectSerializer.class */
public abstract class JsonObjectSerializer<T> extends JsonSerializer<T> {
    protected abstract void serializeObject(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException;

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public final void serialize(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        try {
            jgen.writeStartObject();
            serializeObject(value, jgen, provider);
            jgen.writeEndObject();
        } catch (Exception ex) {
            if (ex instanceof IOException) {
                throw ((IOException) ex);
            }
            throw new JsonMappingException(jgen, "Object serialize error", ex);
        }
    }
}