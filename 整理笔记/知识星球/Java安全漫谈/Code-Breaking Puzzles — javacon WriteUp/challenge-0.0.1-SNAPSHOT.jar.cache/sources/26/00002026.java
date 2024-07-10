package org.springframework.http;

import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/ReactiveHttpOutputMessage.class */
public interface ReactiveHttpOutputMessage extends HttpMessage {
    DataBufferFactory bufferFactory();

    void beforeCommit(Supplier<? extends Mono<Void>> supplier);

    boolean isCommitted();

    Mono<Void> writeWith(Publisher<? extends DataBuffer> publisher);

    Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> publisher);

    Mono<Void> setComplete();
}