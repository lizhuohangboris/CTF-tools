package org.springframework.http.codec.multipart;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/multipart/Part.class */
public interface Part {
    String name();

    HttpHeaders headers();

    Flux<DataBuffer> content();
}