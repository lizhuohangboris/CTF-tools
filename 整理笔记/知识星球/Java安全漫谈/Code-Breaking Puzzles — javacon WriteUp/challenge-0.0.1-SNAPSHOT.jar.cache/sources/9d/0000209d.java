package org.springframework.http.codec;

import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/HttpMessageWriter.class */
public interface HttpMessageWriter<T> {
    List<MediaType> getWritableMediaTypes();

    boolean canWrite(ResolvableType resolvableType, @Nullable MediaType mediaType);

    Mono<Void> write(Publisher<? extends T> publisher, ResolvableType resolvableType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage reactiveHttpOutputMessage, Map<String, Object> map);

    default Mono<Void> write(Publisher<? extends T> inputStream, ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        return write(inputStream, elementType, mediaType, response, hints);
    }
}