package com.fasterxml.jackson.datatype.jsr310.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import java.io.IOException;
import java.time.DateTimeException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/deser/key/Jsr310KeyDeserializer.class */
abstract class Jsr310KeyDeserializer extends KeyDeserializer {
    protected abstract Object deserialize(String str, DeserializationContext deserializationContext) throws IOException;

    @Override // com.fasterxml.jackson.databind.KeyDeserializer
    public final Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        if ("".equals(key)) {
            return null;
        }
        return deserialize(key, ctxt);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <T> T _handleDateTimeException(DeserializationContext ctxt, Class<?> type, DateTimeException e0, String value) throws IOException {
        try {
            return (T) ctxt.handleWeirdKey(type, value, "Failed to deserialize %s: (%s) %s", type.getName(), e0.getClass().getName(), e0.getMessage());
        } catch (JsonMappingException e) {
            e.initCause(e0);
            throw e;
        } catch (IOException e2) {
            if (null == e2.getCause()) {
                e2.initCause(e0);
            }
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
    }
}