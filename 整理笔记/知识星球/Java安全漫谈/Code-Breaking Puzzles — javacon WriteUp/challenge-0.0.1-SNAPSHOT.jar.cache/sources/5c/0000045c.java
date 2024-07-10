package com.fasterxml.jackson.databind.jsonschema;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.lang.reflect.Type;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/jsonschema/SchemaAware.class */
public interface SchemaAware {
    JsonNode getSchema(SerializerProvider serializerProvider, Type type) throws JsonMappingException;

    JsonNode getSchema(SerializerProvider serializerProvider, Type type, boolean z) throws JsonMappingException;
}