package org.springframework.boot.autoconfigure.security.oauth2.client.reactive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.boot.autoconfigure.security.oauth2.client.ClientsConfiguredCondition;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import reactor.core.publisher.Flux;

@AutoConfigureBefore({ReactiveSecurityAutoConfiguration.class})
@EnableConfigurationProperties({OAuth2ClientProperties.class})
@Configuration
@ConditionalOnClass({Flux.class, EnableWebFluxSecurity.class, ClientRegistration.class})
@Conditional({NonServletApplicationCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/client/reactive/ReactiveOAuth2ClientAutoConfiguration.class */
public class ReactiveOAuth2ClientAutoConfiguration {
    private final OAuth2ClientProperties properties;

    public ReactiveOAuth2ClientAutoConfiguration(OAuth2ClientProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean({ReactiveClientRegistrationRepository.class})
    @Conditional({ClientsConfiguredCondition.class})
    @Bean
    public InMemoryReactiveClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = new ArrayList<>((Collection<? extends ClientRegistration>) OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(this.properties).values());
        return new InMemoryReactiveClientRegistrationRepository(registrations);
    }

    @ConditionalOnMissingBean
    @ConditionalOnBean({ReactiveClientRegistrationRepository.class})
    @Bean
    public ReactiveOAuth2AuthorizedClientService authorizedClientService(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @ConditionalOnMissingBean
    @ConditionalOnBean({ReactiveOAuth2AuthorizedClientService.class})
    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository(ReactiveOAuth2AuthorizedClientService authorizedClientService) {
        return new AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository(authorizedClientService);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/client/reactive/ReactiveOAuth2ClientAutoConfiguration$NonServletApplicationCondition.class */
    static class NonServletApplicationCondition extends NoneNestedConditions {
        NonServletApplicationCondition() {
            super(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/client/reactive/ReactiveOAuth2ClientAutoConfiguration$NonServletApplicationCondition$ServletApplicationCondition.class */
        static class ServletApplicationCondition {
            ServletApplicationCondition() {
            }
        }
    }
}