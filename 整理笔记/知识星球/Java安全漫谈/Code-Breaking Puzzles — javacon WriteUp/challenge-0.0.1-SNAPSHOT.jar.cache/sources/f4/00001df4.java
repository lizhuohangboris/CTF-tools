package org.springframework.core.codec;

import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/codec/ResourceEncoder.class */
public class ResourceEncoder extends AbstractSingleValueEncoder<Resource> {
    public static final int DEFAULT_BUFFER_SIZE = 4096;
    private final int bufferSize;

    @Override // org.springframework.core.codec.AbstractSingleValueEncoder
    protected /* bridge */ /* synthetic */ Flux encode(Resource resource, DataBufferFactory dataBufferFactory, ResolvableType resolvableType, @Nullable MimeType mimeType, @Nullable Map map) {
        return encode2(resource, dataBufferFactory, resolvableType, mimeType, (Map<String, Object>) map);
    }

    public ResourceEncoder() {
        this(4096);
    }

    public ResourceEncoder(int bufferSize) {
        super(MimeTypeUtils.APPLICATION_OCTET_STREAM, MimeTypeUtils.ALL);
        Assert.isTrue(bufferSize > 0, "'bufferSize' must be larger than 0");
        this.bufferSize = bufferSize;
    }

    @Override // org.springframework.core.codec.AbstractEncoder, org.springframework.core.codec.Encoder
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> clazz = elementType.toClass();
        return super.canEncode(elementType, mimeType) && Resource.class.isAssignableFrom(clazz);
    }

    /* renamed from: encode  reason: avoid collision after fix types in other method */
    protected Flux<DataBuffer> encode2(Resource resource, DataBufferFactory dataBufferFactory, ResolvableType type, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        if (this.logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
            String logPrefix = Hints.getLogPrefix(hints);
            this.logger.debug(logPrefix + "Writing [" + resource + "]");
        }
        return DataBufferUtils.read(resource, dataBufferFactory, this.bufferSize);
    }
}