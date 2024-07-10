package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/jsonFormatVisitors/JsonFormatTypes.class */
public enum JsonFormatTypes {
    STRING,
    NUMBER,
    INTEGER,
    BOOLEAN,
    OBJECT,
    ARRAY,
    NULL,
    ANY;
    
    private static final Map<String, JsonFormatTypes> _byLCName = new HashMap();

    static {
        JsonFormatTypes[] arr$ = values();
        for (JsonFormatTypes t : arr$) {
            _byLCName.put(t.name().toLowerCase(), t);
        }
    }

    @JsonValue
    public String value() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static JsonFormatTypes forValue(String s) {
        return _byLCName.get(s);
    }
}