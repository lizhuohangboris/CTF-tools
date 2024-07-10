package org.springframework.http.codec;

import java.util.List;
import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/HttpMessageReader.class */
public interface HttpMessageReader<T> {
    List<MediaType> getReadableMediaTypes();

    boolean canRead(ResolvableType resolvableType, @Nullable MediaType mediaType);

    Flux<T> read(ResolvableType resolvableType, ReactiveHttpInputMessage reactiveHttpInputMessage, Map<String, Object> map);

    Mono<T> readMono(ResolvableType resolvableType, ReactiveHttpInputMessage reactiveHttpInputMessage, Map<String, Object> map);

    default Flux<T> read(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        return read(elementType, request, hints);
    }

    default Mono<T> readMono(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        return readMono(elementType, request, hints);
    }
}