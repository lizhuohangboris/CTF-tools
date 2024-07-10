package org.springframework.core.codec;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/codec/AbstractDecoder.class */
public abstract class AbstractDecoder<T> implements Decoder<T> {
    protected Log logger = LogFactory.getLog(getClass());
    private final List<MimeType> decodableMimeTypes;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractDecoder(MimeType... supportedMimeTypes) {
        this.decodableMimeTypes = Arrays.asList(supportedMimeTypes);
    }

    public void setLogger(Log logger) {
        this.logger = logger;
    }

    public Log getLogger() {
        return this.logger;
    }

    @Override // org.springframework.core.codec.Decoder
    public List<MimeType> getDecodableMimeTypes() {
        return this.decodableMimeTypes;
    }

    @Override // org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        if (mimeType == null) {
            return true;
        }
        return this.decodableMimeTypes.stream().anyMatch(candidate -> {
            return candidate.isCompatibleWith(mimeType);
        });
    }

    @Override // org.springframework.core.codec.Decoder
    public Mono<T> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        throw new UnsupportedOperationException();
    }
}