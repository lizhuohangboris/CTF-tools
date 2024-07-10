package org.springframework.web.client;

import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/HttpStatusCodeException.class */
public abstract class HttpStatusCodeException extends RestClientResponseException {
    private static final long serialVersionUID = 5696801857651587810L;
    private final HttpStatus statusCode;

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpStatusCodeException(HttpStatus statusCode) {
        this(statusCode, statusCode.name(), null, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpStatusCodeException(HttpStatus statusCode, String statusText) {
        this(statusCode, statusText, null, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpStatusCodeException(HttpStatus statusCode, String statusText, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        this(statusCode, statusText, null, responseBody, responseCharset);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpStatusCodeException(HttpStatus statusCode, String statusText, @Nullable HttpHeaders responseHeaders, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        super(statusCode.value() + " " + statusText, statusCode.value(), statusText, responseHeaders, responseBody, responseCharset);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return this.statusCode;
    }
}