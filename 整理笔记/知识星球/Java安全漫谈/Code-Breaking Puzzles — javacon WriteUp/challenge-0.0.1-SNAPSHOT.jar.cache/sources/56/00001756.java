package org.springframework.boot.autoconfigure.ldap;

import java.util.Collections;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@EnableConfigurationProperties({LdapProperties.class})
@Configuration
@ConditionalOnClass({ContextSource.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/ldap/LdapAutoConfiguration.class */
public class LdapAutoConfiguration {
    private final LdapProperties properties;
    private final Environment environment;

    public LdapAutoConfiguration(LdapProperties properties, Environment environment) {
        this.properties = properties;
        this.environment = environment;
    }

    @ConditionalOnMissingBean
    @Bean
    public LdapContextSource ldapContextSource() {
        LdapContextSource source = new LdapContextSource();
        source.setUserDn(this.properties.getUsername());
        source.setPassword(this.properties.getPassword());
        source.setAnonymousReadOnly(this.properties.getAnonymousReadOnly());
        source.setBase(this.properties.getBase());
        source.setUrls(this.properties.determineUrls(this.environment));
        source.setBaseEnvironmentProperties(Collections.unmodifiableMap(this.properties.getBaseEnvironment()));
        return source;
    }

    @ConditionalOnMissingBean({LdapOperations.class})
    @Bean
    public LdapTemplate ldapTemplate(ContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }
}