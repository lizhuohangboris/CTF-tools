package org.springframework.boot.autoconfigure.security.oauth2.resource.servlet;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@AutoConfigureBefore({SecurityAutoConfiguration.class})
@EnableConfigurationProperties({OAuth2ResourceServerProperties.class})
@Configuration
@ConditionalOnClass({JwtAuthenticationToken.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({OAuth2ResourceServerJwkConfiguration.class, OAuth2ResourceServerWebSecurityConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/servlet/OAuth2ResourceServerAutoConfiguration.class */
public class OAuth2ResourceServerAutoConfiguration {
}