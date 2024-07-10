package org.springframework.http;

import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.servlets.WebdavStatus;
import org.springframework.asm.Opcodes;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/HttpStatus.class */
public enum HttpStatus {
    CONTINUE(100, "Continue"),
    SWITCHING_PROTOCOLS(101, "Switching Protocols"),
    PROCESSING(Opcodes.FSUB, "Processing"),
    CHECKPOINT(Opcodes.DSUB, "Checkpoint"),
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION, "Non-Authoritative Information"),
    NO_CONTENT(204, "No Content"),
    RESET_CONTENT(HttpServletResponse.SC_RESET_CONTENT, "Reset Content"),
    PARTIAL_CONTENT(HttpServletResponse.SC_PARTIAL_CONTENT, "Partial Content"),
    MULTI_STATUS(WebdavStatus.SC_MULTI_STATUS, "Multi-Status"),
    ALREADY_REPORTED(208, "Already Reported"),
    IM_USED(226, "IM Used"),
    MULTIPLE_CHOICES(300, "Multiple Choices"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    MOVED_TEMPORARILY(302, "Moved Temporarily"),
    SEE_OTHER(303, "See Other"),
    NOT_MODIFIED(304, "Not Modified"),
    USE_PROXY(305, "Use Proxy"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    PERMANENT_REDIRECT(308, "Permanent Redirect"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(HttpServletResponse.SC_PAYMENT_REQUIRED, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(HttpServletResponse.SC_NOT_ACCEPTABLE, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(HttpServletResponse.SC_REQUEST_TIMEOUT, "Request Timeout"),
    CONFLICT(409, "Conflict"),
    GONE(HttpServletResponse.SC_GONE, "Gone"),
    LENGTH_REQUIRED(HttpServletResponse.SC_LENGTH_REQUIRED, "Length Required"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
    URI_TOO_LONG(HttpServletResponse.SC_REQUEST_URI_TOO_LONG, "URI Too Long"),
    REQUEST_URI_TOO_LONG(HttpServletResponse.SC_REQUEST_URI_TOO_LONG, "Request-URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE, "Requested range not satisfiable"),
    EXPECTATION_FAILED(HttpServletResponse.SC_EXPECTATION_FAILED, "Expectation Failed"),
    I_AM_A_TEAPOT(WebdavStatus.SC_UNPROCESSABLE_ENTITY, "I'm a teapot"),
    INSUFFICIENT_SPACE_ON_RESOURCE(WebdavStatus.SC_INSUFFICIENT_SPACE_ON_RESOURCE, "Insufficient Space On Resource"),
    METHOD_FAILURE(WebdavStatus.SC_METHOD_FAILURE, "Method Failure"),
    DESTINATION_LOCKED(421, "Destination Locked"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    LOCKED(WebdavStatus.SC_LOCKED, "Locked"),
    FAILED_DEPENDENCY(424, "Failed Dependency"),
    UPGRADE_REQUIRED(426, "Upgrade Required"),
    PRECONDITION_REQUIRED(428, "Precondition Required"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(HttpServletResponse.SC_GATEWAY_TIMEOUT, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED, "HTTP Version not supported"),
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
    LOOP_DETECTED(508, "Loop Detected"),
    BANDWIDTH_LIMIT_EXCEEDED(509, "Bandwidth Limit Exceeded"),
    NOT_EXTENDED(510, "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");
    
    private final int value;
    private final String reasonPhrase;

    HttpStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public boolean is1xxInformational() {
        return Series.INFORMATIONAL.equals(series());
    }

    public boolean is2xxSuccessful() {
        return Series.SUCCESSFUL.equals(series());
    }

    public boolean is3xxRedirection() {
        return Series.REDIRECTION.equals(series());
    }

    public boolean is4xxClientError() {
        return Series.CLIENT_ERROR.equals(series());
    }

    public boolean is5xxServerError() {
        return Series.SERVER_ERROR.equals(series());
    }

    public boolean isError() {
        return is4xxClientError() || is5xxServerError();
    }

    public Series series() {
        return Series.valueOf(this);
    }

    @Override // java.lang.Enum
    public String toString() {
        return Integer.toString(this.value) + " " + name();
    }

    public static HttpStatus valueOf(int statusCode) {
        HttpStatus status = resolve(statusCode);
        if (status == null) {
            throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
        }
        return status;
    }

    @Nullable
    public static HttpStatus resolve(int statusCode) {
        HttpStatus[] values;
        for (HttpStatus status : values()) {
            if (status.value == statusCode) {
                return status;
            }
        }
        return null;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/HttpStatus$Series.class */
    public enum Series {
        INFORMATIONAL(1),
        SUCCESSFUL(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5);
        
        private final int value;

        Series(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static Series valueOf(int status) {
            Series[] values;
            int seriesCode = status / 100;
            for (Series series : values()) {
                if (series.value == seriesCode) {
                    return series;
                }
            }
            throw new IllegalArgumentException("No matching constant for [" + status + "]");
        }

        public static Series valueOf(HttpStatus status) {
            return valueOf(status.value);
        }
    }
}