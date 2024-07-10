package org.springframework.http;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/ReactiveHttpInputMessage.class */
public interface ReactiveHttpInputMessage extends HttpMessage {
    Flux<DataBuffer> getBody();
}