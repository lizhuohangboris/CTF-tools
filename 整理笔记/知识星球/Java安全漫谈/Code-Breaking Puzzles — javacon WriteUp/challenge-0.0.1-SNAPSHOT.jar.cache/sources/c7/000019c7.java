package org.springframework.boot.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/json/JacksonJsonParser.class */
public class JacksonJsonParser extends AbstractJsonParser {
    private static final TypeReference<?> MAP_TYPE = new MapTypeReference();
    private static final TypeReference<?> LIST_TYPE = new ListTypeReference();
    private ObjectMapper objectMapper;

    public JacksonJsonParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JacksonJsonParser() {
    }

    @Override // org.springframework.boot.json.JsonParser
    public Map<String, Object> parseMap(String json) {
        return (Map) tryParse(() -> {
            return (Map) getObjectMapper().readValue(json, MAP_TYPE);
        }, Exception.class);
    }

    @Override // org.springframework.boot.json.JsonParser
    public List<Object> parseList(String json) {
        return (List) tryParse(() -> {
            return (List) getObjectMapper().readValue(json, LIST_TYPE);
        }, Exception.class);
    }

    private ObjectMapper getObjectMapper() {
        if (this.objectMapper == null) {
            this.objectMapper = new ObjectMapper();
        }
        return this.objectMapper;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/json/JacksonJsonParser$MapTypeReference.class */
    private static class MapTypeReference extends TypeReference<Map<String, Object>> {
        private MapTypeReference() {
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/json/JacksonJsonParser$ListTypeReference.class */
    private static class ListTypeReference extends TypeReference<List<Object>> {
        private ListTypeReference() {
        }
    }
}