package org.springframework.web.server;

import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/NotAcceptableStatusException.class */
public class NotAcceptableStatusException extends ResponseStatusException {
    private final List<MediaType> supportedMediaTypes;

    public NotAcceptableStatusException(String reason) {
        super(HttpStatus.NOT_ACCEPTABLE, reason);
        this.supportedMediaTypes = Collections.emptyList();
    }

    public NotAcceptableStatusException(List<MediaType> supportedMediaTypes) {
        super(HttpStatus.NOT_ACCEPTABLE, "Could not find acceptable representation", null);
        this.supportedMediaTypes = Collections.unmodifiableList(supportedMediaTypes);
    }

    public List<MediaType> getSupportedMediaTypes() {
        return this.supportedMediaTypes;
    }
}