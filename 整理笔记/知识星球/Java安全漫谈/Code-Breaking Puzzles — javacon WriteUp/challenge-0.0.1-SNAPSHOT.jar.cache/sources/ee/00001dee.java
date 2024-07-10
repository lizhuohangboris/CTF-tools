package org.springframework.core.codec;

import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/codec/Decoder.class */
public interface Decoder<T> {
    boolean canDecode(ResolvableType resolvableType, @Nullable MimeType mimeType);

    Flux<T> decode(Publisher<DataBuffer> publisher, ResolvableType resolvableType, @Nullable MimeType mimeType, @Nullable Map<String, Object> map);

    Mono<T> decodeToMono(Publisher<DataBuffer> publisher, ResolvableType resolvableType, @Nullable MimeType mimeType, @Nullable Map<String, Object> map);

    List<MimeType> getDecodableMimeTypes();
}