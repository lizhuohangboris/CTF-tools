package org.springframework.http;

import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/HttpEntity.class */
public class HttpEntity<T> {
    public static final HttpEntity<?> EMPTY = new HttpEntity<>();
    private final HttpHeaders headers;
    @Nullable
    private final T body;

    protected HttpEntity() {
        this(null, null);
    }

    public HttpEntity(T body) {
        this(body, null);
    }

    public HttpEntity(MultiValueMap<String, String> headers) {
        this(null, headers);
    }

    public HttpEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers) {
        this.body = body;
        HttpHeaders tempHeaders = new HttpHeaders();
        if (headers != null) {
            tempHeaders.putAll(headers);
        }
        this.headers = HttpHeaders.readOnlyHttpHeaders(tempHeaders);
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Nullable
    public T getBody() {
        return this.body;
    }

    public boolean hasBody() {
        return this.body != null;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        HttpEntity<?> otherEntity = (HttpEntity) other;
        return ObjectUtils.nullSafeEquals(this.headers, otherEntity.headers) && ObjectUtils.nullSafeEquals(this.body, otherEntity.body);
    }

    public int hashCode() {
        return (ObjectUtils.nullSafeHashCode(this.headers) * 29) + ObjectUtils.nullSafeHashCode(this.body);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        if (this.body != null) {
            builder.append(this.body);
            builder.append(',');
        }
        builder.append(this.headers);
        builder.append('>');
        return builder.toString();
    }
}