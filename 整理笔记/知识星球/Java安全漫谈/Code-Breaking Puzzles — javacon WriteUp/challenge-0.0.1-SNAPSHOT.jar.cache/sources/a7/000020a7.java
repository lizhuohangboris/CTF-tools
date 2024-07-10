package org.springframework.http.codec;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.Encoder;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/ServerSentEventHttpMessageWriter.class */
public class ServerSentEventHttpMessageWriter implements HttpMessageWriter<Object> {
    private static final MediaType DEFAULT_MEDIA_TYPE = new MediaType("text", "event-stream", StandardCharsets.UTF_8);
    private static final List<MediaType> WRITABLE_MEDIA_TYPES = Collections.singletonList(MediaType.TEXT_EVENT_STREAM);
    @Nullable
    private final Encoder<?> encoder;

    public ServerSentEventHttpMessageWriter() {
        this(null);
    }

    public ServerSentEventHttpMessageWriter(@Nullable Encoder<?> encoder) {
        this.encoder = encoder;
    }

    @Nullable
    public Encoder<?> getEncoder() {
        return this.encoder;
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public List<MediaType> getWritableMediaTypes() {
        return WRITABLE_MEDIA_TYPES;
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
        return mediaType == null || MediaType.TEXT_EVENT_STREAM.includes(mediaType) || ServerSentEvent.class.isAssignableFrom(elementType.toClass());
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public Mono<Void> write(Publisher<? extends Object> publisher, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        MediaType mediaType2 = (mediaType == null || mediaType.getCharset() == null) ? DEFAULT_MEDIA_TYPE : mediaType;
        DataBufferFactory bufferFactory = message.bufferFactory();
        message.getHeaders().setContentType(mediaType2);
        return message.writeAndFlushWith(encode(publisher, elementType, mediaType2, bufferFactory, hints));
    }

    private Flux<Publisher<DataBuffer>> encode(Publisher<?> input, ResolvableType elementType, MediaType mediaType, DataBufferFactory factory, Map<String, Object> hints) {
        Class<?> elementClass = elementType.getRawClass();
        ResolvableType valueType = (elementClass == null || !ServerSentEvent.class.isAssignableFrom(elementClass)) ? elementType : elementType.getGeneric(new int[0]);
        return Flux.from(input).map(element -> {
            ServerSentEvent<?> sse = element instanceof ServerSentEvent ? (ServerSentEvent) element : ServerSentEvent.builder().data(element).build();
            StringBuilder sb = new StringBuilder();
            String id = sse.id();
            String event = sse.event();
            Duration retry = sse.retry();
            String comment = sse.comment();
            Object data = sse.data();
            if (id != null) {
                writeField("id", id, sb);
            }
            if (event != null) {
                writeField("event", event, sb);
            }
            if (retry != null) {
                writeField("retry", Long.valueOf(retry.toMillis()), sb);
            }
            if (comment != null) {
                sb.append(':').append(StringUtils.replace(comment, "\n", "\n:")).append("\n");
            }
            if (data != null) {
                sb.append("data:");
            }
            return Flux.concat(new Publisher[]{encodeText(sb, mediaType, factory), encodeData(data, valueType, mediaType, factory, hints), encodeText("\n", mediaType, factory)}).doOnDiscard(PooledDataBuffer.class, (v0) -> {
                DataBufferUtils.release(v0);
            });
        });
    }

    private void writeField(String fieldName, Object fieldValue, StringBuilder stringBuilder) {
        stringBuilder.append(fieldName);
        stringBuilder.append(':');
        stringBuilder.append(fieldValue.toString());
        stringBuilder.append("\n");
    }

    private <T> Flux<DataBuffer> encodeData(@Nullable T data, ResolvableType valueType, MediaType mediaType, DataBufferFactory factory, Map<String, Object> hints) {
        if (data == null) {
            return Flux.empty();
        }
        if (data instanceof String) {
            String text = (String) data;
            return Flux.from(encodeText(StringUtils.replace(text, "\n", "\ndata:") + "\n", mediaType, factory));
        } else if (this.encoder == null) {
            return Flux.error(new CodecException("No SSE encoder configured and the data is not String."));
        } else {
            return this.encoder.encode(Mono.just(data), factory, valueType, mediaType, hints).concatWith(encodeText("\n", mediaType, factory));
        }
    }

    private Mono<DataBuffer> encodeText(CharSequence text, MediaType mediaType, DataBufferFactory bufferFactory) {
        Assert.notNull(mediaType.getCharset(), "Expected MediaType with charset");
        byte[] bytes = text.toString().getBytes(mediaType.getCharset());
        return Mono.defer(() -> {
            return Mono.just(bufferFactory.allocateBuffer(bytes.length).write(bytes));
        });
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public Mono<Void> write(Publisher<? extends Object> publisher, ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        Map<String, Object> allHints = Hints.merge(hints, getEncodeHints(actualType, elementType, mediaType, request, response));
        return write(publisher, elementType, mediaType, response, allHints);
    }

    private Map<String, Object> getEncodeHints(ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response) {
        if (this.encoder instanceof HttpMessageEncoder) {
            HttpMessageEncoder<?> encoder = (HttpMessageEncoder) this.encoder;
            return encoder.getEncodeHints(actualType, elementType, mediaType, request, response);
        }
        return Hints.none();
    }
}