package org.springframework.boot.autoconfigure.security.oauth2.resource.reactive;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.IssuerUriCondition;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;

@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/reactive/ReactiveOAuth2ResourceServerJwkConfiguration.class */
class ReactiveOAuth2ResourceServerJwkConfiguration {
    private final OAuth2ResourceServerProperties properties;

    ReactiveOAuth2ResourceServerJwkConfiguration(OAuth2ResourceServerProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = {"spring.security.oauth2.resourceserver.jwt.jwk-set-uri"})
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return new NimbusReactiveJwtDecoder(this.properties.getJwt().getJwkSetUri());
    }

    @ConditionalOnMissingBean
    @Conditional({IssuerUriCondition.class})
    @Bean
    public ReactiveJwtDecoder jwtDecoderByIssuerUri() {
        return ReactiveJwtDecoders.fromOidcIssuerLocation(this.properties.getJwt().getIssuerUri());
    }
}