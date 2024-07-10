package org.springframework.boot.json;

import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/json/JsonParserFactory.class */
public abstract class JsonParserFactory {
    public static JsonParser getJsonParser() {
        if (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", null)) {
            return new JacksonJsonParser();
        }
        if (ClassUtils.isPresent("com.google.gson.Gson", null)) {
            return new GsonJsonParser();
        }
        if (ClassUtils.isPresent("org.yaml.snakeyaml.Yaml", null)) {
            return new YamlJsonParser();
        }
        return new BasicJsonParser();
    }
}