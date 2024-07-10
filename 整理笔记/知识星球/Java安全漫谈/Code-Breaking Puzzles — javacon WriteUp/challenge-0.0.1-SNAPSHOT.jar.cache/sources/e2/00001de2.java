package org.springframework.core.codec;

import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/codec/AbstractDataBufferDecoder.class */
public abstract class AbstractDataBufferDecoder<T> extends AbstractDecoder<T> {
    protected abstract T decodeDataBuffer(DataBuffer dataBuffer, ResolvableType resolvableType, @Nullable MimeType mimeType, @Nullable Map<String, Object> map);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractDataBufferDecoder(MimeType... supportedMimeTypes) {
        super(supportedMimeTypes);
    }

    @Override // org.springframework.core.codec.Decoder
    public Flux<T> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(inputStream).map(buffer -> {
            return decodeDataBuffer(buffer, elementType, mimeType, hints);
        });
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public Mono<T> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return DataBufferUtils.join(inputStream).map(buffer -> {
            return decodeDataBuffer(buffer, elementType, mimeType, hints);
        });
    }
}