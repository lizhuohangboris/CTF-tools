package org.springframework.web.server.session;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/session/WebSessionManager.class */
public interface WebSessionManager {
    Mono<WebSession> getSession(ServerWebExchange serverWebExchange);
}