package org.springframework.core.codec;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/codec/StringDecoder.class */
public final class StringDecoder extends AbstractDataBufferDecoder<String> {
    private static final DataBuffer END_FRAME = new DefaultDataBufferFactory().wrap(new byte[0]);
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final List<String> DEFAULT_DELIMITERS = Arrays.asList("\r\n", "\n");
    private static final List<byte[]> DEFAULT_DELIMITER_BYTES = (List) DEFAULT_DELIMITERS.stream().map(s -> {
        return s.getBytes(StandardCharsets.UTF_8);
    }).collect(Collectors.toList());
    @Nullable
    private final List<String> delimiters;
    private final boolean stripDelimiter;

    @Override // org.springframework.core.codec.AbstractDataBufferDecoder
    protected /* bridge */ /* synthetic */ String decodeDataBuffer(DataBuffer dataBuffer, ResolvableType resolvableType, @Nullable MimeType mimeType, @Nullable Map map) {
        return decodeDataBuffer(dataBuffer, resolvableType, mimeType, (Map<String, Object>) map);
    }

    private StringDecoder(@Nullable List<String> delimiters, boolean stripDelimiter, MimeType... mimeTypes) {
        super(mimeTypes);
        this.delimiters = delimiters != null ? new ArrayList(delimiters) : null;
        this.stripDelimiter = stripDelimiter;
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return super.canDecode(elementType, mimeType) && String.class.equals(elementType.getRawClass());
    }

    @Override // org.springframework.core.codec.AbstractDataBufferDecoder, org.springframework.core.codec.Decoder
    public Flux<String> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        List<byte[]> delimiterBytes = this.delimiters != null ? (List) this.delimiters.stream().map(s -> {
            return s.getBytes(getCharset(mimeType));
        }).collect(Collectors.toList()) : DEFAULT_DELIMITER_BYTES;
        Flux<DataBuffer> inputFlux = Flux.from(inputStream).flatMapIterable(dataBuffer -> {
            return splitOnDelimiter(dataBuffer, delimiterBytes);
        }).bufferUntil(StringDecoder::isEndFrame).map(StringDecoder::joinUntilEndFrame).doOnDiscard(PooledDataBuffer.class, (v0) -> {
            DataBufferUtils.release(v0);
        });
        return super.decode(inputFlux, elementType, mimeType, hints);
    }

    private List<DataBuffer> splitOnDelimiter(DataBuffer dataBuffer, List<byte[]> delimiterBytes) {
        DataBuffer frame;
        List<DataBuffer> frames = new ArrayList<>();
        do {
            int length = Integer.MAX_VALUE;
            byte[] matchingDelimiter = null;
            for (byte[] delimiter : delimiterBytes) {
                int index = indexOf(dataBuffer, delimiter);
                if (index >= 0 && index < length) {
                    length = index;
                    matchingDelimiter = delimiter;
                }
            }
            int readPosition = dataBuffer.readPosition();
            if (matchingDelimiter != null) {
                if (this.stripDelimiter) {
                    frame = dataBuffer.slice(readPosition, length);
                } else {
                    frame = dataBuffer.slice(readPosition, length + matchingDelimiter.length);
                }
                dataBuffer.readPosition(readPosition + length + matchingDelimiter.length);
                frames.add(DataBufferUtils.retain(frame));
                frames.add(END_FRAME);
            } else {
                DataBuffer frame2 = dataBuffer.slice(readPosition, dataBuffer.readableByteCount());
                dataBuffer.readPosition(readPosition + dataBuffer.readableByteCount());
                frames.add(DataBufferUtils.retain(frame2));
            }
        } while (dataBuffer.readableByteCount() > 0);
        DataBufferUtils.release(dataBuffer);
        return frames;
    }

    private static int indexOf(DataBuffer dataBuffer, byte[] delimiter) {
        for (int i = dataBuffer.readPosition(); i < dataBuffer.writePosition(); i++) {
            int dataBufferPos = i;
            int delimiterPos = 0;
            while (delimiterPos < delimiter.length && dataBuffer.getByte(dataBufferPos) == delimiter[delimiterPos]) {
                dataBufferPos++;
                if (dataBufferPos == dataBuffer.writePosition() && delimiterPos != delimiter.length - 1) {
                    return -1;
                }
                delimiterPos++;
            }
            if (delimiterPos == delimiter.length) {
                return i - dataBuffer.readPosition();
            }
        }
        return -1;
    }

    private static boolean isEndFrame(DataBuffer dataBuffer) {
        return dataBuffer == END_FRAME;
    }

    private static DataBuffer joinUntilEndFrame(List<DataBuffer> dataBuffers) {
        if (!dataBuffers.isEmpty()) {
            int lastIdx = dataBuffers.size() - 1;
            if (isEndFrame(dataBuffers.get(lastIdx))) {
                dataBuffers.remove(lastIdx);
            }
        }
        return dataBuffers.get(0).factory().join(dataBuffers);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.core.codec.AbstractDataBufferDecoder
    protected String decodeDataBuffer(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Charset charset = getCharset(mimeType);
        CharBuffer charBuffer = charset.decode(dataBuffer.asByteBuffer());
        DataBufferUtils.release(dataBuffer);
        String value = charBuffer.toString();
        LogFormatUtils.traceDebug(this.logger, traceOn -> {
            String formatted = LogFormatUtils.formatValue(value, !traceOn.booleanValue());
            return Hints.getLogPrefix(hints) + "Decoded " + formatted;
        });
        return value;
    }

    private static Charset getCharset(@Nullable MimeType mimeType) {
        if (mimeType != null && mimeType.getCharset() != null) {
            return mimeType.getCharset();
        }
        return DEFAULT_CHARSET;
    }

    @Deprecated
    public static StringDecoder textPlainOnly(boolean ignored) {
        return textPlainOnly();
    }

    public static StringDecoder textPlainOnly() {
        return textPlainOnly(null, true);
    }

    public static StringDecoder textPlainOnly(@Nullable List<String> delimiters, boolean stripDelimiter) {
        return new StringDecoder(delimiters, stripDelimiter, new MimeType("text", "plain", DEFAULT_CHARSET));
    }

    @Deprecated
    public static StringDecoder allMimeTypes(boolean ignored) {
        return allMimeTypes();
    }

    public static StringDecoder allMimeTypes() {
        return allMimeTypes(null, true);
    }

    public static StringDecoder allMimeTypes(@Nullable List<String> delimiters, boolean stripDelimiter) {
        return new StringDecoder(delimiters, stripDelimiter, new MimeType("text", "plain", DEFAULT_CHARSET), MimeTypeUtils.ALL);
    }
}