package org.springframework.util;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/InvalidMimeTypeException.class */
public class InvalidMimeTypeException extends IllegalArgumentException {
    private final String mimeType;

    public InvalidMimeTypeException(String mimeType, String message) {
        super("Invalid mime type \"" + mimeType + "\": " + message);
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return this.mimeType;
    }
}