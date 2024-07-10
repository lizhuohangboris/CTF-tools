package org.springframework.http;

import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/RequestEntity.class */
public class RequestEntity<T> extends HttpEntity<T> {
    @Nullable
    private final HttpMethod method;
    private final URI url;
    @Nullable
    private final Type type;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/RequestEntity$BodyBuilder.class */
    public interface BodyBuilder extends HeadersBuilder<BodyBuilder> {
        BodyBuilder contentLength(long j);

        BodyBuilder contentType(MediaType mediaType);

        <T> RequestEntity<T> body(T t);

        <T> RequestEntity<T> body(T t, Type type);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/RequestEntity$HeadersBuilder.class */
    public interface HeadersBuilder<B extends HeadersBuilder<B>> {
        B header(String str, String... strArr);

        B accept(MediaType... mediaTypeArr);

        B acceptCharset(Charset... charsetArr);

        B ifModifiedSince(long j);

        B ifNoneMatch(String... strArr);

        RequestEntity<Void> build();
    }

    public RequestEntity(HttpMethod method, URI url) {
        this(null, null, method, url, null);
    }

    public RequestEntity(@Nullable T body, HttpMethod method, URI url) {
        this(body, null, method, url, null);
    }

    public RequestEntity(@Nullable T body, HttpMethod method, URI url, Type type) {
        this(body, null, method, url, type);
    }

    public RequestEntity(MultiValueMap<String, String> headers, HttpMethod method, URI url) {
        this(null, headers, method, url, null);
    }

    public RequestEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, @Nullable HttpMethod method, URI url) {
        this(body, headers, method, url, null);
    }

    public RequestEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, @Nullable HttpMethod method, URI url, @Nullable Type type) {
        super(body, headers);
        this.method = method;
        this.url = url;
        this.type = type;
    }

    @Nullable
    public HttpMethod getMethod() {
        return this.method;
    }

    public URI getUrl() {
        return this.url;
    }

    @Nullable
    public Type getType() {
        T body;
        if (this.type == null && (body = getBody()) != null) {
            return body.getClass();
        }
        return this.type;
    }

    @Override // org.springframework.http.HttpEntity
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        RequestEntity<?> otherEntity = (RequestEntity) other;
        return ObjectUtils.nullSafeEquals(getMethod(), otherEntity.getMethod()) && ObjectUtils.nullSafeEquals(getUrl(), otherEntity.getUrl());
    }

    @Override // org.springframework.http.HttpEntity
    public int hashCode() {
        int hashCode = super.hashCode();
        return (29 * ((29 * hashCode) + ObjectUtils.nullSafeHashCode(this.method))) + ObjectUtils.nullSafeHashCode(this.url);
    }

    @Override // org.springframework.http.HttpEntity
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        builder.append(getMethod());
        builder.append(' ');
        builder.append(getUrl());
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

    public static BodyBuilder method(HttpMethod method, URI url) {
        return new DefaultBodyBuilder(method, url);
    }

    public static HeadersBuilder<?> get(URI url) {
        return method(HttpMethod.GET, url);
    }

    public static HeadersBuilder<?> head(URI url) {
        return method(HttpMethod.HEAD, url);
    }

    public static BodyBuilder post(URI url) {
        return method(HttpMethod.POST, url);
    }

    public static BodyBuilder put(URI url) {
        return method(HttpMethod.PUT, url);
    }

    public static BodyBuilder patch(URI url) {
        return method(HttpMethod.PATCH, url);
    }

    public static HeadersBuilder<?> delete(URI url) {
        return method(HttpMethod.DELETE, url);
    }

    public static HeadersBuilder<?> options(URI url) {
        return method(HttpMethod.OPTIONS, url);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/RequestEntity$DefaultBodyBuilder.class */
    public static class DefaultBodyBuilder implements BodyBuilder {
        private final HttpMethod method;
        private final URI url;
        private final HttpHeaders headers = new HttpHeaders();

        public DefaultBodyBuilder(HttpMethod method, URI url) {
            this.method = method;
            this.url = url;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.RequestEntity.HeadersBuilder
        public BodyBuilder header(String headerName, String... headerValues) {
            for (String headerValue : headerValues) {
                this.headers.add(headerName, headerValue);
            }
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.RequestEntity.HeadersBuilder
        public BodyBuilder accept(MediaType... acceptableMediaTypes) {
            this.headers.setAccept(Arrays.asList(acceptableMediaTypes));
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.RequestEntity.HeadersBuilder
        public BodyBuilder acceptCharset(Charset... acceptableCharsets) {
            this.headers.setAcceptCharset(Arrays.asList(acceptableCharsets));
            return this;
        }

        @Override // org.springframework.http.RequestEntity.BodyBuilder
        public BodyBuilder contentLength(long contentLength) {
            this.headers.setContentLength(contentLength);
            return this;
        }

        @Override // org.springframework.http.RequestEntity.BodyBuilder
        public BodyBuilder contentType(MediaType contentType) {
            this.headers.setContentType(contentType);
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.RequestEntity.HeadersBuilder
        public BodyBuilder ifModifiedSince(long ifModifiedSince) {
            this.headers.setIfModifiedSince(ifModifiedSince);
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.RequestEntity.HeadersBuilder
        public BodyBuilder ifNoneMatch(String... ifNoneMatches) {
            this.headers.setIfNoneMatch(Arrays.asList(ifNoneMatches));
            return this;
        }

        @Override // org.springframework.http.RequestEntity.HeadersBuilder
        public RequestEntity<Void> build() {
            return new RequestEntity<>(this.headers, this.method, this.url);
        }

        @Override // org.springframework.http.RequestEntity.BodyBuilder
        public <T> RequestEntity<T> body(T body) {
            return new RequestEntity<>(body, this.headers, this.method, this.url);
        }

        @Override // org.springframework.http.RequestEntity.BodyBuilder
        public <T> RequestEntity<T> body(T body, Type type) {
            return new RequestEntity<>(body, this.headers, this.method, this.url, type);
        }
    }
}