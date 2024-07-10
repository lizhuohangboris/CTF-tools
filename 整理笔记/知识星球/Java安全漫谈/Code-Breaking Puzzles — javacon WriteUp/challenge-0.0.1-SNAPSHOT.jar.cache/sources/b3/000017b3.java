package org.springframework.boot.autoconfigure.security.oauth2.client.servlet;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

@AutoConfigureBefore({SecurityAutoConfiguration.class})
@Configuration
@ConditionalOnClass({EnableWebSecurity.class, ClientRegistration.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({OAuth2ClientRegistrationRepositoryConfiguration.class, OAuth2WebSecurityConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/client/servlet/OAuth2ClientAutoConfiguration.class */
public class OAuth2ClientAutoConfiguration {
}