package org.springframework.http;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/ResponseEntity.class */
public class ResponseEntity<T> extends HttpEntity<T> {
    private final Object status;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/ResponseEntity$BodyBuilder.class */
    public interface BodyBuilder extends HeadersBuilder<BodyBuilder> {
        BodyBuilder contentLength(long j);

        BodyBuilder contentType(MediaType mediaType);

        <T> ResponseEntity<T> body(@Nullable T t);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/ResponseEntity$HeadersBuilder.class */
    public interface HeadersBuilder<B extends HeadersBuilder<B>> {
        B header(String str, String... strArr);

        B headers(@Nullable HttpHeaders httpHeaders);

        B allow(HttpMethod... httpMethodArr);

        B eTag(String str);

        B lastModified(long j);

        B location(URI uri);

        B cacheControl(CacheControl cacheControl);

        B varyBy(String... strArr);

        <T> ResponseEntity<T> build();
    }

    public ResponseEntity(HttpStatus status) {
        this((Object) null, (MultiValueMap<String, String>) null, status);
    }

    public ResponseEntity(@Nullable T body, HttpStatus status) {
        this((Object) body, (MultiValueMap<String, String>) null, status);
    }

    public ResponseEntity(MultiValueMap<String, String> headers, HttpStatus status) {
        this((Object) null, headers, status);
    }

    public ResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, HttpStatus status) {
        super(body, headers);
        Assert.notNull(status, "HttpStatus must not be null");
        this.status = status;
    }

    private ResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, Object status) {
        super(body, headers);
        Assert.notNull(status, "HttpStatus must not be null");
        this.status = status;
    }

    public HttpStatus getStatusCode() {
        if (this.status instanceof HttpStatus) {
            return (HttpStatus) this.status;
        }
        return HttpStatus.valueOf(((Integer) this.status).intValue());
    }

    public int getStatusCodeValue() {
        if (this.status instanceof HttpStatus) {
            return ((HttpStatus) this.status).value();
        }
        return ((Integer) this.status).intValue();
    }

    @Override // org.springframework.http.HttpEntity
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        ResponseEntity<?> otherEntity = (ResponseEntity) other;
        return ObjectUtils.nullSafeEquals(this.status, otherEntity.status);
    }

    @Override // org.springframework.http.HttpEntity
    public int hashCode() {
        return (super.hashCode() * 29) + ObjectUtils.nullSafeHashCode(this.status);
    }

    @Override // org.springframework.http.HttpEntity
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        builder.append(this.status.toString());
        if (this.status instanceof HttpStatus) {
            builder.append(' ');
            builder.append(((HttpStatus) this.status).getReasonPhrase());
        }
        builder.append(',');
        T body = getBody();
        HttpHeaders headers = getHeaders();
        if (body != null) {
            builder.append(body);
            builder.append(',');
        }
        builder.append(headers);
        builder.append('>');
        return builder.toString();
    }

    public static BodyBuilder status(HttpStatus status) {
        Assert.notNull(status, "HttpStatus must not be null");
        return new DefaultBuilder(status);
    }

    public static BodyBuilder status(int status) {
        return new DefaultBuilder(Integer.valueOf(status));
    }

    public static <T> ResponseEntity<T> of(Optional<T> body) {
        Assert.notNull(body, "Body must not be null");
        return (ResponseEntity) body.map(ResponseEntity::ok).orElse(notFound().build());
    }

    public static BodyBuilder ok() {
        return status(HttpStatus.OK);
    }

    public static <T> ResponseEntity<T> ok(T body) {
        BodyBuilder builder = ok();
        return builder.body(body);
    }

    public static BodyBuilder created(URI location) {
        BodyBuilder builder = status(HttpStatus.CREATED);
        return builder.location(location);
    }

    public static BodyBuilder accepted() {
        return status(HttpStatus.ACCEPTED);
    }

    public static HeadersBuilder<?> noContent() {
        return status(HttpStatus.NO_CONTENT);
    }

    public static BodyBuilder badRequest() {
        return status(HttpStatus.BAD_REQUEST);
    }

    public static HeadersBuilder<?> notFound() {
        return status(HttpStatus.NOT_FOUND);
    }

    public static BodyBuilder unprocessableEntity() {
        return status(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/ResponseEntity$DefaultBuilder.class */
    public static class DefaultBuilder implements BodyBuilder {
        private final Object statusCode;
        private final HttpHeaders headers = new HttpHeaders();

        public DefaultBuilder(Object statusCode) {
            this.statusCode = statusCode;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder header(String headerName, String... headerValues) {
            for (String headerValue : headerValues) {
                this.headers.add(headerName, headerValue);
            }
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder headers(@Nullable HttpHeaders headers) {
            if (headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder allow(HttpMethod... allowedMethods) {
            this.headers.setAllow(new LinkedHashSet(Arrays.asList(allowedMethods)));
            return this;
        }

        @Override // org.springframework.http.ResponseEntity.BodyBuilder
        public BodyBuilder contentLength(long contentLength) {
            this.headers.setContentLength(contentLength);
            return this;
        }

        @Override // org.springframework.http.ResponseEntity.BodyBuilder
        public BodyBuilder contentType(MediaType contentType) {
            this.headers.setContentType(contentType);
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder eTag(String etag) {
            if (!etag.startsWith("\"") && !etag.startsWith("W/\"")) {
                etag = "\"" + etag;
            }
            if (!etag.endsWith("\"")) {
                etag = etag + "\"";
            }
            this.headers.setETag(etag);
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder lastModified(long date) {
            this.headers.setLastModified(date);
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder location(URI location) {
            this.headers.setLocation(location);
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder cacheControl(CacheControl cacheControl) {
            String ccValue = cacheControl.getHeaderValue();
            if (ccValue != null) {
                this.headers.setCacheControl(cacheControl.getHeaderValue());
            }
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public BodyBuilder varyBy(String... requestHeaders) {
            this.headers.setVary(Arrays.asList(requestHeaders));
            return this;
        }

        @Override // org.springframework.http.ResponseEntity.HeadersBuilder
        public <T> ResponseEntity<T> build() {
            return body(null);
        }

        @Override // org.springframework.http.ResponseEntity.BodyBuilder
        public <T> ResponseEntity<T> body(@Nullable T body) {
            return new ResponseEntity<>(body, this.headers, this.statusCode);
        }
    }
}