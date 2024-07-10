package org.springframework.core.codec;

import java.io.ByteArrayInputStream;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/codec/ResourceDecoder.class */
public class ResourceDecoder extends AbstractDataBufferDecoder<Resource> {
    @Override // org.springframework.core.codec.AbstractDataBufferDecoder
    protected /* bridge */ /* synthetic */ Resource decodeDataBuffer(DataBuffer dataBuffer, ResolvableType resolvableType, @Nullable MimeType mimeType, @Nullable Map map) {
        return decodeDataBuffer(dataBuffer, resolvableType, mimeType, (Map<String, Object>) map);
    }

    public ResourceDecoder() {
        super(MimeTypeUtils.ALL);
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> clazz = elementType.getRawClass();
        return clazz != null && Resource.class.isAssignableFrom(clazz) && super.canDecode(elementType, mimeType);
    }

    @Override // org.springframework.core.codec.AbstractDataBufferDecoder, org.springframework.core.codec.Decoder
    public Flux<Resource> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(decodeToMono(inputStream, elementType, mimeType, hints));
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.core.codec.AbstractDataBufferDecoder
    protected Resource decodeDataBuffer(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        Class<?> clazz = elementType.getRawClass();
        Assert.state(clazz != null, "No resource class");
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(Hints.getLogPrefix(hints) + "Read " + bytes.length + " bytes");
        }
        if (InputStreamResource.class == clazz) {
            return new InputStreamResource(new ByteArrayInputStream(bytes));
        }
        if (Resource.class.isAssignableFrom(clazz)) {
            return new ByteArrayResource(bytes);
        }
        throw new IllegalStateException("Unsupported resource class: " + clazz);
    }
}