package org.springframework.boot.autoconfigure.security.oauth2.resource.reactive;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;

@AutoConfigureBefore({ReactiveSecurityAutoConfiguration.class})
@EnableConfigurationProperties({OAuth2ResourceServerProperties.class})
@Configuration
@ConditionalOnClass({EnableWebFluxSecurity.class, BearerTokenAuthenticationToken.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Import({ReactiveOAuth2ResourceServerJwkConfiguration.class, ReactiveOAuth2ResourceServerWebSecurityConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/reactive/ReactiveOAuth2ResourceServerAutoConfiguration.class */
public class ReactiveOAuth2ResourceServerAutoConfiguration {
}