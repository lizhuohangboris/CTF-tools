package org.springframework.boot.json;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/json/JsonParseException.class */
public class JsonParseException extends IllegalArgumentException {
    public JsonParseException() {
        this(null);
    }

    public JsonParseException(Throwable cause) {
        super("Cannot parse JSON", cause);
    }
}