package org.springframework.boot.web.reactive.error;

import java.util.Map;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/reactive/error/ErrorAttributes.class */
public interface ErrorAttributes {
    Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace);

    Throwable getError(ServerRequest request);

    void storeErrorInformation(Throwable error, ServerWebExchange exchange);
}