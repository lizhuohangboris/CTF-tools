package org.springframework.boot.autoconfigure.security.oauth2.resource.servlet;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.IssuerUriCondition;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoderJwkSupport;

@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/servlet/OAuth2ResourceServerJwkConfiguration.class */
class OAuth2ResourceServerJwkConfiguration {
    private final OAuth2ResourceServerProperties properties;

    OAuth2ResourceServerJwkConfiguration(OAuth2ResourceServerProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = {"spring.security.oauth2.resourceserver.jwt.jwk-set-uri"})
    @Bean
    public JwtDecoder jwtDecoderByJwkKeySetUri() {
        return new NimbusJwtDecoderJwkSupport(this.properties.getJwt().getJwkSetUri());
    }

    @ConditionalOnMissingBean
    @Conditional({IssuerUriCondition.class})
    @Bean
    public JwtDecoder jwtDecoderByIssuerUri() {
        return JwtDecoders.fromOidcIssuerLocation(this.properties.getJwt().getIssuerUri());
    }
}