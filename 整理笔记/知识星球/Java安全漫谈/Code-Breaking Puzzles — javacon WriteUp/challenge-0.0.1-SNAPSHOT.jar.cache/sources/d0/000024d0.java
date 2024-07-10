package org.springframework.web.cors.reactive;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/cors/reactive/CorsWebFilter.class */
public class CorsWebFilter implements WebFilter {
    private final CorsConfigurationSource configSource;
    private final CorsProcessor processor;

    public CorsWebFilter(CorsConfigurationSource configSource) {
        this(configSource, new DefaultCorsProcessor());
    }

    public CorsWebFilter(CorsConfigurationSource configSource, CorsProcessor processor) {
        Assert.notNull(configSource, "CorsConfigurationSource must not be null");
        Assert.notNull(processor, "CorsProcessor must not be null");
        this.configSource = configSource;
        this.processor = processor;
    }

    @Override // org.springframework.web.server.WebFilter
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        CorsConfiguration corsConfiguration;
        ServerHttpRequest request = exchange.getRequest();
        if (CorsUtils.isCorsRequest(request) && (corsConfiguration = this.configSource.getCorsConfiguration(exchange)) != null) {
            boolean isValid = this.processor.process(corsConfiguration, exchange);
            if (!isValid || CorsUtils.isPreFlightRequest(request)) {
                return Mono.empty();
            }
        }
        return chain.filter(exchange);
    }
}