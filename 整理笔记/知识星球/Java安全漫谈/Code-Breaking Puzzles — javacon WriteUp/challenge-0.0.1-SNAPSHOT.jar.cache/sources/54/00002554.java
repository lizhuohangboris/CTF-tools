package org.springframework.web.server;

import java.util.Collections;
import java.util.List;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/UnsupportedMediaTypeStatusException.class */
public class UnsupportedMediaTypeStatusException extends ResponseStatusException {
    @Nullable
    private final MediaType contentType;
    private final List<MediaType> supportedMediaTypes;
    @Nullable
    private final ResolvableType bodyType;

    public UnsupportedMediaTypeStatusException(@Nullable String reason) {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, reason);
        this.contentType = null;
        this.supportedMediaTypes = Collections.emptyList();
        this.bodyType = null;
    }

    public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes) {
        this(contentType, supportedTypes, null);
    }

    public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes, @Nullable ResolvableType bodyType) {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, initReason(contentType, bodyType));
        this.contentType = contentType;
        this.supportedMediaTypes = Collections.unmodifiableList(supportedTypes);
        this.bodyType = bodyType;
    }

    private static String initReason(@Nullable MediaType contentType, @Nullable ResolvableType bodyType) {
        return "Content type '" + (contentType != null ? contentType : "") + "' not supported" + (bodyType != null ? " for bodyType=" + bodyType.toString() : "");
    }

    @Nullable
    public MediaType getContentType() {
        return this.contentType;
    }

    public List<MediaType> getSupportedMediaTypes() {
        return this.supportedMediaTypes;
    }

    @Nullable
    public ResolvableType getBodyType() {
        return this.bodyType;
    }
}