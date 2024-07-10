package org.springframework.core.codec;

import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/codec/ByteArrayDecoder.class */
public class ByteArrayDecoder extends AbstractDataBufferDecoder<byte[]> {
    @Override // org.springframework.core.codec.AbstractDataBufferDecoder
    protected /* bridge */ /* synthetic */ byte[] decodeDataBuffer(DataBuffer dataBuffer, ResolvableType resolvableType, @Nullable MimeType mimeType, @Nullable Map map) {
        return decodeDataBuffer(dataBuffer, resolvableType, mimeType, (Map<String, Object>) map);
    }

    public ByteArrayDecoder() {
        super(MimeTypeUtils.ALL);
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> clazz = elementType.getRawClass();
        return super.canDecode(elementType, mimeType) && byte[].class == clazz;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.core.codec.AbstractDataBufferDecoder
    protected byte[] decodeDataBuffer(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        byte[] result = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(result);
        DataBufferUtils.release(dataBuffer);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(Hints.getLogPrefix(hints) + "Read " + result.length + " bytes");
        }
        return result;
    }
}