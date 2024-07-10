package org.springframework.http;

import java.io.File;
import java.nio.file.Path;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/ZeroCopyHttpOutputMessage.class */
public interface ZeroCopyHttpOutputMessage extends ReactiveHttpOutputMessage {
    Mono<Void> writeWith(Path path, long j, long j2);

    default Mono<Void> writeWith(File file, long position, long count) {
        return writeWith(file.toPath(), position, count);
    }
}