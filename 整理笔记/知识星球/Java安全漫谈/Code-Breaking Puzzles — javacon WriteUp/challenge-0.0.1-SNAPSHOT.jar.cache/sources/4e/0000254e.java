package org.springframework.web.server;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/ResponseStatusException.class */
public class ResponseStatusException extends NestedRuntimeException {
    private final HttpStatus status;
    @Nullable
    private final String reason;

    public ResponseStatusException(HttpStatus status) {
        this(status, null, null);
    }

    public ResponseStatusException(HttpStatus status, @Nullable String reason) {
        this(status, reason, null);
    }

    public ResponseStatusException(HttpStatus status, @Nullable String reason, @Nullable Throwable cause) {
        super(null, cause);
        Assert.notNull(status, "HttpStatus is required");
        this.status = status;
        this.reason = reason;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    @Nullable
    public String getReason() {
        return this.reason;
    }

    @Override // org.springframework.core.NestedRuntimeException, java.lang.Throwable
    public String getMessage() {
        String msg = this.status + (this.reason != null ? " \"" + this.reason + "\"" : "");
        return NestedExceptionUtils.buildMessage(msg, getCause());
    }
}