package org.springframework.web.client;

import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/UnknownHttpStatusCodeException.class */
public class UnknownHttpStatusCodeException extends RestClientResponseException {
    private static final long serialVersionUID = 7103980251635005491L;

    public UnknownHttpStatusCodeException(int rawStatusCode, String statusText, @Nullable HttpHeaders responseHeaders, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        super("Unknown status code [" + rawStatusCode + "] " + statusText, rawStatusCode, statusText, responseHeaders, responseBody, responseCharset);
    }
}