package org.springframework.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsProcessor;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.DefaultCorsProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/CorsFilter.class */
public class CorsFilter extends OncePerRequestFilter {
    private final CorsConfigurationSource configSource;
    private CorsProcessor processor = new DefaultCorsProcessor();

    public CorsFilter(CorsConfigurationSource configSource) {
        Assert.notNull(configSource, "CorsConfigurationSource must not be null");
        this.configSource = configSource;
    }

    public void setCorsProcessor(CorsProcessor processor) {
        Assert.notNull(processor, "CorsProcessor must not be null");
        this.processor = processor;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CorsConfiguration corsConfiguration;
        if (CorsUtils.isCorsRequest(request) && (corsConfiguration = this.configSource.getCorsConfiguration(request)) != null) {
            boolean isValid = this.processor.processRequest(corsConfiguration, request, response);
            if (!isValid || CorsUtils.isPreFlightRequest(request)) {
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}