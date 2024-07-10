package org.springframework.http.codec.protobuf;

import com.google.protobuf.Message;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageEncoder;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/protobuf/ProtobufEncoder.class */
public class ProtobufEncoder extends ProtobufCodecSupport implements HttpMessageEncoder<Message> {
    private static final List<MediaType> streamingMediaTypes = (List) MIME_TYPES.stream().map(mimeType -> {
        return new MediaType(mimeType.getType(), mimeType.getSubtype(), Collections.singletonMap("delimited", "true"));
    }).collect(Collectors.toList());

    @Override // org.springframework.core.codec.Encoder
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return Message.class.isAssignableFrom(elementType.toClass()) && supportsMimeType(mimeType);
    }

    @Override // org.springframework.core.codec.Encoder
    public Flux<DataBuffer> encode(Publisher<? extends Message> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(inputStream).map(message -> {
            return encodeMessage(message, bufferFactory, !(inputStream instanceof Mono));
        });
    }

    private DataBuffer encodeMessage(Message message, DataBufferFactory bufferFactory, boolean streaming) {
        DataBuffer buffer = bufferFactory.allocateBuffer();
        OutputStream outputStream = buffer.asOutputStream();
        try {
            if (streaming) {
                message.writeDelimitedTo(outputStream);
            } else {
                message.writeTo(outputStream);
            }
            return buffer;
        } catch (IOException ex) {
            throw new IllegalStateException("Unexpected I/O error while writing to data buffer", ex);
        }
    }

    @Override // org.springframework.http.codec.HttpMessageEncoder
    public List<MediaType> getStreamingMediaTypes() {
        return streamingMediaTypes;
    }

    @Override // org.springframework.core.codec.Encoder
    public List<MimeType> getEncodableMimeTypes() {
        return getMimeTypes();
    }
}