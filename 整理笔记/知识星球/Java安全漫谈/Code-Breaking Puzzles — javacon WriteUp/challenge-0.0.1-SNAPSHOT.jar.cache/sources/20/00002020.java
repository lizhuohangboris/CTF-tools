package org.springframework.http;

import org.springframework.util.InvalidMimeTypeException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/InvalidMediaTypeException.class */
public class InvalidMediaTypeException extends IllegalArgumentException {
    private final String mediaType;

    public InvalidMediaTypeException(String mediaType, String message) {
        super("Invalid media type \"" + mediaType + "\": " + message);
        this.mediaType = mediaType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InvalidMediaTypeException(InvalidMimeTypeException ex) {
        super(ex.getMessage(), ex);
        this.mediaType = ex.getMimeType();
    }

    public String getMediaType() {
        return this.mediaType;
    }
}