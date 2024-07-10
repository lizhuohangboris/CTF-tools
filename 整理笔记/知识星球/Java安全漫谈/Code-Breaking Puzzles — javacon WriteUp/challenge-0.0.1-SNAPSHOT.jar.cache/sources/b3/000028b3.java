package org.thymeleaf.spring5.context.webflux;

import java.util.Locale;
import java.util.Map;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.util.Validate;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/SpringWebFluxContext.class */
public class SpringWebFluxContext extends AbstractContext implements ISpringWebFluxContext {
    private final ServerWebExchange exchange;
    private final ReactiveAdapterRegistry reactiveAdapterRegistry;

    public SpringWebFluxContext(ServerWebExchange exchange) {
        this(exchange, null, null, null);
    }

    public SpringWebFluxContext(ServerWebExchange exchange, Locale locale) {
        this(exchange, null, locale, null);
    }

    public SpringWebFluxContext(ServerWebExchange exchange, Locale locale, Map<String, Object> variables) {
        this(exchange, null, locale, variables);
    }

    public SpringWebFluxContext(ServerWebExchange exchange, ReactiveAdapterRegistry reactiveAdapterRegistry, Locale locale, Map<String, Object> variables) {
        super(locale, variables);
        Validate.notNull(exchange, "ServerWebExchange cannot be null in Spring WebFlux contexts");
        this.exchange = exchange;
        this.reactiveAdapterRegistry = reactiveAdapterRegistry;
    }

    public ReactiveAdapterRegistry getReactiveAdapterRegistry() {
        return this.reactiveAdapterRegistry;
    }

    @Override // org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext
    public ServerHttpRequest getRequest() {
        return this.exchange.getRequest();
    }

    @Override // org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext
    public Mono<WebSession> getSession() {
        return this.exchange.getSession();
    }

    @Override // org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext
    public ServerHttpResponse getResponse() {
        return this.exchange.getResponse();
    }

    @Override // org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext
    public ServerWebExchange getExchange() {
        return this.exchange;
    }
}