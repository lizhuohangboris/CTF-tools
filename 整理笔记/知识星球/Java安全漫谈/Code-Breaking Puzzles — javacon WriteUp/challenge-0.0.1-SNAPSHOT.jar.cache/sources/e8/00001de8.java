package org.springframework.core.codec;

import java.nio.ByteBuffer;
import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/codec/ByteBufferDecoder.class */
public class ByteBufferDecoder extends AbstractDataBufferDecoder<ByteBuffer> {
    @Override // org.springframework.core.codec.AbstractDataBufferDecoder
    protected /* bridge */ /* synthetic */ ByteBuffer decodeDataBuffer(DataBuffer dataBuffer, ResolvableType resolvableType, @Nullable MimeType mimeType, @Nullable Map map) {
        return decodeDataBuffer(dataBuffer, resolvableType, mimeType, (Map<String, Object>) map);
    }

    public ByteBufferDecoder() {
        super(MimeTypeUtils.ALL);
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> clazz = elementType.getRawClass();
        return super.canDecode(elementType, mimeType) && clazz != null && ByteBuffer.class.isAssignableFrom(clazz);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.core.codec.AbstractDataBufferDecoder
    protected ByteBuffer decodeDataBuffer(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        int byteCount = dataBuffer.readableByteCount();
        ByteBuffer copy = ByteBuffer.allocate(byteCount);
        copy.put(dataBuffer.asByteBuffer());
        copy.flip();
        DataBufferUtils.release(dataBuffer);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(Hints.getLogPrefix(hints) + "Read " + byteCount + " bytes");
        }
        return copy;
    }
}