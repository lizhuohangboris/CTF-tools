package org.springframework.core.codec;

import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/codec/Encoder.class */
public interface Encoder<T> {
    boolean canEncode(ResolvableType resolvableType, @Nullable MimeType mimeType);

    Flux<DataBuffer> encode(Publisher<? extends T> publisher, DataBufferFactory dataBufferFactory, ResolvableType resolvableType, @Nullable MimeType mimeType, @Nullable Map<String, Object> map);

    List<MimeType> getEncodableMimeTypes();
}