package org.springframework.web.client;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/RestClientResponseException.class */
public class RestClientResponseException extends RestClientException {
    private static final long serialVersionUID = -8803556342728481792L;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;
    private final int rawStatusCode;
    private final String statusText;
    private final byte[] responseBody;
    @Nullable
    private final HttpHeaders responseHeaders;
    @Nullable
    private final String responseCharset;

    public RestClientResponseException(String message, int statusCode, String statusText, @Nullable HttpHeaders responseHeaders, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        super(message);
        this.rawStatusCode = statusCode;
        this.statusText = statusText;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody != null ? responseBody : new byte[0];
        this.responseCharset = responseCharset != null ? responseCharset.name() : null;
    }

    public int getRawStatusCode() {
        return this.rawStatusCode;
    }

    public String getStatusText() {
        return this.statusText;
    }

    @Nullable
    public HttpHeaders getResponseHeaders() {
        return this.responseHeaders;
    }

    public byte[] getResponseBodyAsByteArray() {
        return this.responseBody;
    }

    public String getResponseBodyAsString() {
        if (this.responseCharset == null) {
            return new String(this.responseBody, DEFAULT_CHARSET);
        }
        try {
            return new String(this.responseBody, this.responseCharset);
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }
}