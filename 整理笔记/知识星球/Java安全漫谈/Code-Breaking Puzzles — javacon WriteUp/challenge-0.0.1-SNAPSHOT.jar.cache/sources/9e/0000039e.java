package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/AtomicBooleanDeserializer.class */
public class AtomicBooleanDeserializer extends StdScalarDeserializer<AtomicBoolean> {
    private static final long serialVersionUID = 1;

    public AtomicBooleanDeserializer() {
        super(AtomicBoolean.class);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public AtomicBoolean deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return new AtomicBoolean(_parseBooleanPrimitive(jp, ctxt));
    }
}