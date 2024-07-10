package org.springframework.http.codec;

import java.util.List;
import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Encoder;
import org.springframework.core.codec.Hints;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/HttpMessageEncoder.class */
public interface HttpMessageEncoder<T> extends Encoder<T> {
    List<MediaType> getStreamingMediaTypes();

    default Map<String, Object> getEncodeHints(ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response) {
        return Hints.none();
    }
}