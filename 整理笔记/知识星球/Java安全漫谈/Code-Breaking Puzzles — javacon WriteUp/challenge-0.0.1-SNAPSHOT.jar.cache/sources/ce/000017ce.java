package org.springframework.boot.autoconfigure.security.servlet;

import java.util.EnumSet;
import java.util.stream.Collectors;
import javax.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

@EnableConfigurationProperties({SecurityProperties.class})
@Configuration
@ConditionalOnClass({AbstractSecurityWebApplicationInitializer.class, SessionCreationPolicy.class})
@AutoConfigureAfter({SecurityAutoConfiguration.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/servlet/SecurityFilterAutoConfiguration.class */
public class SecurityFilterAutoConfiguration {
    private static final String DEFAULT_FILTER_NAME = "springSecurityFilterChain";

    @ConditionalOnBean(name = {DEFAULT_FILTER_NAME})
    @Bean
    public DelegatingFilterProxyRegistrationBean securityFilterChainRegistration(SecurityProperties securityProperties) {
        DelegatingFilterProxyRegistrationBean registration = new DelegatingFilterProxyRegistrationBean(DEFAULT_FILTER_NAME, new ServletRegistrationBean[0]);
        registration.setOrder(securityProperties.getFilter().getOrder());
        registration.setDispatcherTypes(getDispatcherTypes(securityProperties));
        return registration;
    }

    private EnumSet<DispatcherType> getDispatcherTypes(SecurityProperties securityProperties) {
        if (securityProperties.getFilter().getDispatcherTypes() == null) {
            return null;
        }
        return (EnumSet) securityProperties.getFilter().getDispatcherTypes().stream().map(type -> {
            return DispatcherType.valueOf(type.name());
        }).collect(Collectors.collectingAndThen(Collectors.toSet(), (v0) -> {
            return EnumSet.copyOf(v0);
        }));
    }
}