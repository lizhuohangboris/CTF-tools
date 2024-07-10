package org.springframework.boot.json;

import java.util.List;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/json/JsonParser.class */
public interface JsonParser {
    Map<String, Object> parseMap(String json) throws JsonParseException;

    List<Object> parseList(String json) throws JsonParseException;
}