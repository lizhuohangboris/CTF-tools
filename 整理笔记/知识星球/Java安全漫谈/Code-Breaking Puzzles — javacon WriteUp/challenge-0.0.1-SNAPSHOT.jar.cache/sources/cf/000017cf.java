package org.springframework.boot.autoconfigure.security.servlet;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@ConditionalOnClass({RequestMatcher.class, DispatcherServlet.class})
@ConditionalOnBean({HandlerMappingIntrospector.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/servlet/SecurityRequestMatcherProviderAutoConfiguration.class */
public class SecurityRequestMatcherProviderAutoConfiguration {
    @Bean
    public RequestMatcherProvider requestMatcherProvider(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcherProvider(introspector);
    }
}