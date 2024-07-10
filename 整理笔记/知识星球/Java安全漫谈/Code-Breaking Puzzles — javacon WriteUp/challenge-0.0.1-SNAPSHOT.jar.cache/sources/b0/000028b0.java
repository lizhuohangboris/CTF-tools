package org.thymeleaf.spring5.context.webflux;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.thymeleaf.context.IContext;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/ISpringWebFluxContext.class */
public interface ISpringWebFluxContext extends IContext {
    ServerHttpRequest getRequest();

    ServerHttpResponse getResponse();

    Mono<WebSession> getSession();

    ServerWebExchange getExchange();
}