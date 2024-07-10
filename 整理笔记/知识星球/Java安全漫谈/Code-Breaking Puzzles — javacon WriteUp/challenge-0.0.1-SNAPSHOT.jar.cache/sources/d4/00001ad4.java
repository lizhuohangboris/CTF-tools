package org.springframework.boot.web.server;

import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/server/ErrorPage.class */
public class ErrorPage {
    private final HttpStatus status;
    private final Class<? extends Throwable> exception;
    private final String path;

    public ErrorPage(String path) {
        this.status = null;
        this.exception = null;
        this.path = path;
    }

    public ErrorPage(HttpStatus status, String path) {
        this.status = status;
        this.exception = null;
        this.path = path;
    }

    public ErrorPage(Class<? extends Throwable> exception, String path) {
        this.status = null;
        this.exception = exception;
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public Class<? extends Throwable> getException() {
        return this.exception;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public int getStatusCode() {
        if (this.status != null) {
            return this.status.value();
        }
        return 0;
    }

    public String getExceptionName() {
        if (this.exception != null) {
            return this.exception.getName();
        }
        return null;
    }

    public boolean isGlobal() {
        return this.status == null && this.exception == null;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof ErrorPage)) {
            ErrorPage other = (ErrorPage) obj;
            boolean rtn = 1 != 0 && ObjectUtils.nullSafeEquals(getExceptionName(), other.getExceptionName());
            boolean rtn2 = rtn && ObjectUtils.nullSafeEquals(this.path, other.path);
            boolean rtn3 = rtn2 && this.status == other.status;
            return rtn3;
        }
        return false;
    }

    public int hashCode() {
        int result = (31 * 1) + ObjectUtils.nullSafeHashCode(getExceptionName());
        return (31 * ((31 * result) + ObjectUtils.nullSafeHashCode(this.path))) + getStatusCode();
    }
}