package org.springframework.web.server.handler;

import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/handler/WebHandlerDecorator.class */
public class WebHandlerDecorator implements WebHandler {
    private final WebHandler delegate;

    public WebHandlerDecorator(WebHandler delegate) {
        Assert.notNull(delegate, "'delegate' must not be null");
        this.delegate = delegate;
    }

    public WebHandler getDelegate() {
        return this.delegate;
    }

    @Override // org.springframework.web.server.WebHandler
    public Mono<Void> handle(ServerWebExchange exchange) {
        return this.delegate.handle(exchange);
    }

    public String toString() {
        return getClass().getSimpleName() + " [delegate=" + this.delegate + "]";
    }
}