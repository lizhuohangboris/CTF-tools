package org.springframework.boot.autoconfigure.security.oauth2.client.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.ClientsConfiguredCondition;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@EnableConfigurationProperties({OAuth2ClientProperties.class})
@Configuration
@Conditional({ClientsConfiguredCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/client/servlet/OAuth2ClientRegistrationRepositoryConfiguration.class */
class OAuth2ClientRegistrationRepositoryConfiguration {
    private final OAuth2ClientProperties properties;

    OAuth2ClientRegistrationRepositoryConfiguration(OAuth2ClientProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean({ClientRegistrationRepository.class})
    @Bean
    public InMemoryClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = new ArrayList<>((Collection<? extends ClientRegistration>) OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(this.properties).values());
        return new InMemoryClientRegistrationRepository(registrations);
    }
}