package org.springframework.web.server;

import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/WebFilter.class */
public interface WebFilter {
    Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain);
}