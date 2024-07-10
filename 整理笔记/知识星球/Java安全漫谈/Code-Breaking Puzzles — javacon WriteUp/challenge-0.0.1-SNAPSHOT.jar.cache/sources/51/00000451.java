package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormatVisitor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/jsonFormatVisitors/JsonNumberFormatVisitor.class */
public interface JsonNumberFormatVisitor extends JsonValueFormatVisitor {
    void numberType(JsonParser.NumberType numberType);

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/jsonFormatVisitors/JsonNumberFormatVisitor$Base.class */
    public static class Base extends JsonValueFormatVisitor.Base implements JsonNumberFormatVisitor {
        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor
        public void numberType(JsonParser.NumberType type) {
        }
    }
}