package org.springframework.web;

import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import org.springframework.http.MediaType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/HttpMediaTypeException.class */
public abstract class HttpMediaTypeException extends ServletException {
    private final List<MediaType> supportedMediaTypes;

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpMediaTypeException(String message) {
        super(message);
        this.supportedMediaTypes = Collections.emptyList();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpMediaTypeException(String message, List<MediaType> supportedMediaTypes) {
        super(message);
        this.supportedMediaTypes = Collections.unmodifiableList(supportedMediaTypes);
    }

    public List<MediaType> getSupportedMediaTypes() {
        return this.supportedMediaTypes;
    }
}