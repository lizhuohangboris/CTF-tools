package org.springframework.core.codec;

import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/codec/DataBufferDecoder.class */
public class DataBufferDecoder extends AbstractDataBufferDecoder<DataBuffer> {
    @Override // org.springframework.core.codec.AbstractDataBufferDecoder
    protected /* bridge */ /* synthetic */ DataBuffer decodeDataBuffer(DataBuffer dataBuffer, ResolvableType resolvableType, @Nullable MimeType mimeType, @Nullable Map map) {
        return decodeDataBuffer(dataBuffer, resolvableType, mimeType, (Map<String, Object>) map);
    }

    public DataBufferDecoder() {
        super(MimeTypeUtils.ALL);
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> clazz = elementType.getRawClass();
        return super.canDecode(elementType, mimeType) && clazz != null && DataBuffer.class.isAssignableFrom(clazz);
    }

    @Override // org.springframework.core.codec.AbstractDataBufferDecoder, org.springframework.core.codec.Decoder
    public Flux<DataBuffer> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(inputStream);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.core.codec.AbstractDataBufferDecoder
    protected DataBuffer decodeDataBuffer(DataBuffer buffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(Hints.getLogPrefix(hints) + "Read " + buffer.readableByteCount() + " bytes");
        }
        return buffer;
    }
}