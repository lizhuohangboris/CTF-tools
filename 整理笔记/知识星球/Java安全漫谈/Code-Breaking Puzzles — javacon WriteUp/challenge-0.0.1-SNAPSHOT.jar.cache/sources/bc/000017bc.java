package org.springframework.boot.autoconfigure.security.oauth2.resource.reactive;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@ConditionalOnBean({ReactiveJwtDecoder.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/reactive/ReactiveOAuth2ResourceServerWebSecurityConfiguration.class */
class ReactiveOAuth2ResourceServerWebSecurityConfiguration {
    private final ReactiveJwtDecoder jwtDecoder;

    ReactiveOAuth2ResourceServerWebSecurityConfiguration(ReactiveJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @ConditionalOnMissingBean
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange().anyExchange().authenticated().and().oauth2ResourceServer().jwt().jwtDecoder(this.jwtDecoder);
        return http.build();
    }
}