package org.springframework.web.server;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/ServerWebInputException.class */
public class ServerWebInputException extends ResponseStatusException {
    @Nullable
    private final MethodParameter parameter;

    public ServerWebInputException(String reason) {
        this(reason, null, null);
    }

    public ServerWebInputException(String reason, @Nullable MethodParameter parameter) {
        this(reason, parameter, null);
    }

    public ServerWebInputException(String reason, @Nullable MethodParameter parameter, @Nullable Throwable cause) {
        super(HttpStatus.BAD_REQUEST, reason, cause);
        this.parameter = parameter;
    }

    @Nullable
    public MethodParameter getMethodParameter() {
        return this.parameter;
    }
}