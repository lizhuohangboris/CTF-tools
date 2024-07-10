package org.springframework.http.client;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.reactivestreams.Publisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/MultipartBodyBuilder.class */
public final class MultipartBodyBuilder {
    private final LinkedMultiValueMap<String, DefaultPartBuilder> parts = new LinkedMultiValueMap<>();

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/MultipartBodyBuilder$PartBuilder.class */
    public interface PartBuilder {
        PartBuilder header(String str, String... strArr);

        PartBuilder headers(Consumer<HttpHeaders> consumer);
    }

    public PartBuilder part(String name, Object part) {
        return part(name, part, null);
    }

    public PartBuilder part(String name, Object part, @Nullable MediaType contentType) {
        Object partBody;
        Assert.hasLength(name, "'name' must not be empty");
        Assert.notNull(part, "'part' must not be null");
        if (part instanceof Publisher) {
            throw new IllegalArgumentException("Use publisher(String, Publisher, Class) or publisher(String, Publisher, ParameterizedTypeReference) for adding Publisher parts");
        }
        if (part instanceof PublisherEntity) {
            PublisherPartBuilder<?, ?> builder = new PublisherPartBuilder<>((PublisherEntity) part);
            this.parts.add(name, builder);
            return builder;
        }
        HttpHeaders partHeaders = new HttpHeaders();
        if (part instanceof HttpEntity) {
            HttpEntity<?> httpEntity = (HttpEntity) part;
            partBody = httpEntity.getBody();
            partHeaders.addAll(httpEntity.getHeaders());
        } else {
            partBody = part;
        }
        if (contentType != null) {
            partHeaders.setContentType(contentType);
        }
        DefaultPartBuilder builder2 = new DefaultPartBuilder(partHeaders, partBody);
        this.parts.add(name, builder2);
        return builder2;
    }

    public <T, P extends Publisher<T>> PartBuilder asyncPart(String name, P publisher, Class<T> elementClass) {
        Assert.hasLength(name, "'name' must not be empty");
        Assert.notNull(publisher, "'publisher' must not be null");
        Assert.notNull(elementClass, "'elementClass' must not be null");
        HttpHeaders headers = new HttpHeaders();
        PublisherPartBuilder<T, P> builder = new PublisherPartBuilder<>(headers, publisher, elementClass);
        this.parts.add(name, builder);
        return builder;
    }

    public <T, P extends Publisher<T>> PartBuilder asyncPart(String name, P publisher, ParameterizedTypeReference<T> typeReference) {
        Assert.hasLength(name, "'name' must not be empty");
        Assert.notNull(publisher, "'publisher' must not be null");
        Assert.notNull(typeReference, "'typeReference' must not be null");
        HttpHeaders headers = new HttpHeaders();
        PublisherPartBuilder<T, P> builder = new PublisherPartBuilder<>(headers, publisher, typeReference);
        this.parts.add(name, builder);
        return builder;
    }

    public MultiValueMap<String, HttpEntity<?>> build() {
        MultiValueMap<String, HttpEntity<?>> result = new LinkedMultiValueMap<>(this.parts.size());
        for (Map.Entry<String, List<DefaultPartBuilder>> entry : this.parts.entrySet()) {
            for (DefaultPartBuilder builder : entry.getValue()) {
                HttpEntity<?> entity = builder.build();
                result.add(entry.getKey(), entity);
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/MultipartBodyBuilder$DefaultPartBuilder.class */
    public static class DefaultPartBuilder implements PartBuilder {
        protected final HttpHeaders headers;
        @Nullable
        protected final Object body;

        public DefaultPartBuilder(HttpHeaders headers, @Nullable Object body) {
            this.headers = headers;
            this.body = body;
        }

        @Override // org.springframework.http.client.MultipartBodyBuilder.PartBuilder
        public PartBuilder header(String headerName, String... headerValues) {
            this.headers.addAll(headerName, Arrays.asList(headerValues));
            return this;
        }

        @Override // org.springframework.http.client.MultipartBodyBuilder.PartBuilder
        public PartBuilder headers(Consumer<HttpHeaders> headersConsumer) {
            headersConsumer.accept(this.headers);
            return this;
        }

        public HttpEntity<?> build() {
            return new HttpEntity<>(this.body, this.headers);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/MultipartBodyBuilder$PublisherPartBuilder.class */
    public static class PublisherPartBuilder<S, P extends Publisher<S>> extends DefaultPartBuilder {
        private final ResolvableType resolvableType;

        public PublisherPartBuilder(HttpHeaders headers, P body, Class<S> elementClass) {
            super(headers, body);
            this.resolvableType = ResolvableType.forClass(elementClass);
        }

        public PublisherPartBuilder(HttpHeaders headers, P body, ParameterizedTypeReference<S> typeReference) {
            super(headers, body);
            this.resolvableType = ResolvableType.forType((ParameterizedTypeReference<?>) typeReference);
        }

        public PublisherPartBuilder(PublisherEntity<S, P> other) {
            super(other.getHeaders(), other.getBody());
            this.resolvableType = other.getResolvableType();
        }

        @Override // org.springframework.http.client.MultipartBodyBuilder.DefaultPartBuilder
        public HttpEntity<?> build() {
            Publisher publisher = (Publisher) this.body;
            Assert.state(publisher != null, "Publisher must not be null");
            return new PublisherEntity(this.headers, publisher, this.resolvableType);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/MultipartBodyBuilder$PublisherEntity.class */
    public static final class PublisherEntity<T, P extends Publisher<T>> extends HttpEntity<P> {
        private final ResolvableType resolvableType;

        private PublisherEntity(@Nullable MultiValueMap<String, String> headers, P publisher, ResolvableType resolvableType) {
            super(publisher, headers);
            Assert.notNull(publisher, "'publisher' must not be null");
            Assert.notNull(resolvableType, "'resolvableType' must not be null");
            this.resolvableType = resolvableType;
        }

        public ResolvableType getResolvableType() {
            return this.resolvableType;
        }
    }
}