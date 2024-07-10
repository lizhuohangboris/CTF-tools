package org.springframework.web.cors.reactive;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/cors/reactive/UrlBasedCorsConfigurationSource.class */
public class UrlBasedCorsConfigurationSource implements CorsConfigurationSource {
    private final Map<PathPattern, CorsConfiguration> corsConfigurations;
    private final PathPatternParser patternParser;

    public UrlBasedCorsConfigurationSource() {
        this(new PathPatternParser());
    }

    public UrlBasedCorsConfigurationSource(PathPatternParser patternParser) {
        this.corsConfigurations = new LinkedHashMap();
        this.patternParser = patternParser;
    }

    public void setCorsConfigurations(@Nullable Map<String, CorsConfiguration> corsConfigurations) {
        this.corsConfigurations.clear();
        if (corsConfigurations != null) {
            corsConfigurations.forEach(this::registerCorsConfiguration);
        }
    }

    public void registerCorsConfiguration(String path, CorsConfiguration config) {
        this.corsConfigurations.put(this.patternParser.parse(path), config);
    }

    @Override // org.springframework.web.cors.reactive.CorsConfigurationSource
    @Nullable
    public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
        PathContainer lookupPath = exchange.getRequest().getPath().pathWithinApplication();
        return (CorsConfiguration) this.corsConfigurations.entrySet().stream().filter(entry -> {
            return ((PathPattern) entry.getKey()).matches(lookupPath);
        }).map((v0) -> {
            return v0.getValue();
        }).findFirst().orElse(null);
    }
}