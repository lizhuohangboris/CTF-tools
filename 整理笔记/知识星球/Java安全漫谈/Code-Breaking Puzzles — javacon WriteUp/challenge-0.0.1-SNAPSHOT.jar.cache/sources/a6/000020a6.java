package org.springframework.http.codec;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/ServerSentEventHttpMessageReader.class */
public class ServerSentEventHttpMessageReader implements HttpMessageReader<Object> {
    private static final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
    private static final StringDecoder stringDecoder = StringDecoder.textPlainOnly();
    private static final ResolvableType STRING_TYPE = ResolvableType.forClass(String.class);
    @Nullable
    private final Decoder<?> decoder;

    public ServerSentEventHttpMessageReader() {
        this(null);
    }

    public ServerSentEventHttpMessageReader(@Nullable Decoder<?> decoder) {
        this.decoder = decoder;
    }

    @Nullable
    public Decoder<?> getDecoder() {
        return this.decoder;
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public List<MediaType> getReadableMediaTypes() {
        return Collections.singletonList(MediaType.TEXT_EVENT_STREAM);
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        return MediaType.TEXT_EVENT_STREAM.includes(mediaType) || isServerSentEvent(elementType);
    }

    private boolean isServerSentEvent(ResolvableType elementType) {
        Class<?> rawClass = elementType.getRawClass();
        return rawClass != null && ServerSentEvent.class.isAssignableFrom(rawClass);
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Flux<Object> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        boolean shouldWrap = isServerSentEvent(elementType);
        ResolvableType valueType = shouldWrap ? elementType.getGeneric(new int[0]) : elementType;
        return stringDecoder.decode(message.getBody(), STRING_TYPE, null, hints).bufferUntil(line -> {
            return line.equals("");
        }).concatMap(lines -> {
            return buildEvent(lines, valueType, shouldWrap, hints);
        });
    }

    private Mono<?> buildEvent(List<String> lines, ResolvableType valueType, boolean shouldWrap, Map<String, Object> hints) {
        ServerSentEvent.Builder<Object> sseBuilder = shouldWrap ? ServerSentEvent.builder() : null;
        StringBuilder data = null;
        StringBuilder comment = null;
        for (String line : lines) {
            if (line.startsWith("data:")) {
                data = data != null ? data : new StringBuilder();
                data.append(line.substring(5)).append("\n");
            }
            if (shouldWrap) {
                if (line.startsWith("id:")) {
                    sseBuilder.id(line.substring(3));
                } else if (line.startsWith("event:")) {
                    sseBuilder.event(line.substring(6));
                } else if (line.startsWith("retry:")) {
                    sseBuilder.retry(Duration.ofMillis(Long.valueOf(line.substring(6)).longValue()));
                } else if (line.startsWith(":")) {
                    comment = comment != null ? comment : new StringBuilder();
                    comment.append(line.substring(1)).append("\n");
                }
            }
        }
        Mono<?> decodedData = data != null ? decodeData(data.toString(), valueType, hints) : Mono.empty();
        if (shouldWrap) {
            if (comment != null) {
                sseBuilder.comment(comment.toString().substring(0, comment.length() - 1));
            }
            return decodedData.map(o -> {
                sseBuilder.data(o);
                return sseBuilder.build();
            });
        }
        return decodedData;
    }

    private Mono<?> decodeData(String data, ResolvableType dataType, Map<String, Object> hints) {
        if (String.class == dataType.resolve()) {
            return Mono.just(data.substring(0, data.length() - 1));
        }
        if (this.decoder == null) {
            return Mono.error(new CodecException("No SSE decoder configured and the data is not String."));
        }
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        return this.decoder.decodeToMono(Mono.just(bufferFactory.wrap(bytes)), dataType, MediaType.TEXT_EVENT_STREAM, hints);
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Mono<Object> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        if (String.class.equals(elementType.getRawClass())) {
            return stringDecoder.decodeToMono(message.getBody(), elementType, null, null).cast(Object.class);
        }
        return Mono.error(new UnsupportedOperationException("ServerSentEventHttpMessageReader only supports reading stream of events as a Flux"));
    }
}