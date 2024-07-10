package org.springframework.http.server.reactive;

import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/HttpHandler.class */
public interface HttpHandler {
    Mono<Void> handle(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse);
}