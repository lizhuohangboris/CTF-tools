package org.springframework.boot.autoconfigure.security.oauth2.client.servlet;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

@Configuration
@ConditionalOnBean({ClientRegistrationRepository.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/client/servlet/OAuth2WebSecurityConfiguration.class */
class OAuth2WebSecurityConfiguration {
    OAuth2WebSecurityConfiguration() {
    }

    @ConditionalOnMissingBean
    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @ConditionalOnMissingBean
    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
    }

    @ConditionalOnMissingBean({WebSecurityConfigurerAdapter.class})
    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/client/servlet/OAuth2WebSecurityConfiguration$OAuth2WebSecurityConfigurerAdapter.class */
    static class OAuth2WebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        OAuth2WebSecurityConfigurerAdapter() {
        }

        protected void configure(HttpSecurity http) throws Exception {
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) http.authorizeRequests().anyRequest()).authenticated().and().oauth2Login().and().oauth2Client();
        }
    }
}