package org.springframework.web.cors.reactive;

import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/cors/reactive/CorsConfigurationSource.class */
public interface CorsConfigurationSource {
    @Nullable
    CorsConfiguration getCorsConfiguration(ServerWebExchange serverWebExchange);
}