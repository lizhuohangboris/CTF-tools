package org.springframework.boot.autoconfigure.security.oauth2.resource.servlet;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@ConditionalOnMissingBean({WebSecurityConfigurerAdapter.class})
@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/servlet/OAuth2ResourceServerWebSecurityConfiguration.class */
class OAuth2ResourceServerWebSecurityConfiguration {
    OAuth2ResourceServerWebSecurityConfiguration() {
    }

    @Configuration
    @ConditionalOnBean({JwtDecoder.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/servlet/OAuth2ResourceServerWebSecurityConfiguration$OAuth2WebSecurityConfigurerAdapter.class */
    static class OAuth2WebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        OAuth2WebSecurityConfigurerAdapter() {
        }

        protected void configure(HttpSecurity http) throws Exception {
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) http.authorizeRequests().anyRequest()).authenticated().and().oauth2ResourceServer().jwt();
        }
    }
}