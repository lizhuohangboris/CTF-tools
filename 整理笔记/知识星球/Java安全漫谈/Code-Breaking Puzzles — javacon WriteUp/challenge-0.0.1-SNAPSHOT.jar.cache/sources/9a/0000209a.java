package org.springframework.http.codec;

import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/HttpMessageDecoder.class */
public interface HttpMessageDecoder<T> extends Decoder<T> {
    Map<String, Object> getDecodeHints(ResolvableType resolvableType, ResolvableType resolvableType2, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse);
}