package org.springframework.http.codec;

import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractEncoder;
import org.springframework.core.codec.Encoder;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpLogging;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/EncoderHttpMessageWriter.class */
public class EncoderHttpMessageWriter<T> implements HttpMessageWriter<T> {
    private final Encoder<T> encoder;
    private final List<MediaType> mediaTypes;
    @Nullable
    private final MediaType defaultMediaType;

    public EncoderHttpMessageWriter(Encoder<T> encoder) {
        Assert.notNull(encoder, "Encoder is required");
        this.encoder = encoder;
        this.mediaTypes = MediaType.asMediaTypes(encoder.getEncodableMimeTypes());
        this.defaultMediaType = initDefaultMediaType(this.mediaTypes);
        initLogger(encoder);
    }

    private void initLogger(Encoder<T> encoder) {
        if ((encoder instanceof AbstractEncoder) && encoder.getClass().getPackage().getName().startsWith("org.springframework.core.codec")) {
            Log logger = HttpLogging.forLog(((AbstractEncoder) encoder).getLogger());
            ((AbstractEncoder) encoder).setLogger(logger);
        }
    }

    @Nullable
    private static MediaType initDefaultMediaType(List<MediaType> mediaTypes) {
        return mediaTypes.stream().filter((v0) -> {
            return v0.isConcrete();
        }).findFirst().orElse(null);
    }

    public Encoder<T> getEncoder() {
        return this.encoder;
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public List<MediaType> getWritableMediaTypes() {
        return this.mediaTypes;
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
        return this.encoder.canEncode(elementType, mediaType);
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public Mono<Void> write(Publisher<? extends T> inputStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        MediaType contentType = updateContentType(message, mediaType);
        Flux<DataBuffer> body = this.encoder.encode(inputStream, message.bufferFactory(), elementType, contentType, hints);
        if (inputStream instanceof Mono) {
            HttpHeaders headers = message.getHeaders();
            if (headers.getFirst(HttpHeaders.CONTENT_LENGTH) == null) {
                return Mono.from(body).defaultIfEmpty(message.bufferFactory().wrap(new byte[0])).flatMap(buffer -> {
                    headers.setContentLength(buffer.readableByteCount());
                    return message.writeWith(Mono.just(buffer));
                });
            }
        }
        return isStreamingMediaType(contentType) ? message.writeAndFlushWith(body.map((v0) -> {
            return Flux.just(v0);
        })) : message.writeWith(body);
    }

    @Nullable
    private MediaType updateContentType(ReactiveHttpOutputMessage message, @Nullable MediaType mediaType) {
        MediaType result = message.getHeaders().getContentType();
        if (result != null) {
            return result;
        }
        MediaType fallback = this.defaultMediaType;
        MediaType result2 = useFallback(mediaType, fallback) ? fallback : mediaType;
        if (result2 != null) {
            result2 = addDefaultCharset(result2, fallback);
            message.getHeaders().setContentType(result2);
        }
        return result2;
    }

    private static boolean useFallback(@Nullable MediaType main, @Nullable MediaType fallback) {
        return main == null || !main.isConcrete() || (main.equals(MediaType.APPLICATION_OCTET_STREAM) && fallback != null);
    }

    private static MediaType addDefaultCharset(MediaType main, @Nullable MediaType defaultType) {
        if (main.getCharset() == null && defaultType != null && defaultType.getCharset() != null) {
            return new MediaType(main, defaultType.getCharset());
        }
        return main;
    }

    private boolean isStreamingMediaType(@Nullable MediaType contentType) {
        return contentType != null && (this.encoder instanceof HttpMessageEncoder) && ((HttpMessageEncoder) this.encoder).getStreamingMediaTypes().stream().anyMatch(streamingMediaType -> {
            return contentType.isCompatibleWith(streamingMediaType) && contentType.getParameters().entrySet().containsAll(streamingMediaType.getParameters().keySet());
        });
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public Mono<Void> write(Publisher<? extends T> inputStream, ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        Map<String, Object> allHints = Hints.merge(hints, getWriteHints(actualType, elementType, mediaType, request, response));
        return write(inputStream, elementType, mediaType, response, allHints);
    }

    protected Map<String, Object> getWriteHints(ResolvableType streamType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response) {
        if (this.encoder instanceof HttpMessageEncoder) {
            HttpMessageEncoder<?> encoder = (HttpMessageEncoder) this.encoder;
            return encoder.getEncodeHints(streamType, elementType, mediaType, request, response);
        }
        return Hints.none();
    }
}