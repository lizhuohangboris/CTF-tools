package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.databind.SerializerProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/jsonFormatVisitors/JsonFormatVisitorWithSerializerProvider.class */
public interface JsonFormatVisitorWithSerializerProvider {
    SerializerProvider getProvider();

    void setProvider(SerializerProvider serializerProvider);
}