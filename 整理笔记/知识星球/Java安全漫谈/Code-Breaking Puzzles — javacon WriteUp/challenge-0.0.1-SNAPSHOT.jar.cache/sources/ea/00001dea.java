package org.springframework.core.codec;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/codec/CharSequenceEncoder.class */
public final class CharSequenceEncoder extends AbstractEncoder<CharSequence> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private CharSequenceEncoder(MimeType... mimeTypes) {
        super(mimeTypes);
    }

    @Override // org.springframework.core.codec.AbstractEncoder, org.springframework.core.codec.Encoder
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> clazz = elementType.toClass();
        return super.canEncode(elementType, mimeType) && CharSequence.class.isAssignableFrom(clazz);
    }

    @Override // org.springframework.core.codec.Encoder
    public Flux<DataBuffer> encode(Publisher<? extends CharSequence> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Charset charset = getCharset(mimeType);
        return Flux.from(inputStream).map(charSequence -> {
            if (!Hints.isLoggingSuppressed(hints)) {
                LogFormatUtils.traceDebug(this.logger, traceOn -> {
                    String formatted = LogFormatUtils.formatValue(charSequence, !traceOn.booleanValue());
                    return Hints.getLogPrefix(hints) + "Writing " + formatted;
                });
            }
            CharBuffer charBuffer = CharBuffer.wrap(charSequence);
            ByteBuffer byteBuffer = charset.encode(charBuffer);
            return bufferFactory.wrap(byteBuffer);
        });
    }

    private Charset getCharset(@Nullable MimeType mimeType) {
        Charset charset;
        if (mimeType != null && mimeType.getCharset() != null) {
            charset = mimeType.getCharset();
        } else {
            charset = DEFAULT_CHARSET;
        }
        return charset;
    }

    public static CharSequenceEncoder textPlainOnly() {
        return new CharSequenceEncoder(new MimeType("text", "plain", DEFAULT_CHARSET));
    }

    public static CharSequenceEncoder allMimeTypes() {
        return new CharSequenceEncoder(new MimeType("text", "plain", DEFAULT_CHARSET), MimeTypeUtils.ALL);
    }
}