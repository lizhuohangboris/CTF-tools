package org.springframework.boot.autoconfigure.security.servlet;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@ConditionalOnClass({WebSecurityConfigurerAdapter.class})
@ConditionalOnMissingBean({WebSecurityConfigurerAdapter.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/servlet/SpringBootWebSecurityConfiguration.class */
public class SpringBootWebSecurityConfiguration {

    @Configuration
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/servlet/SpringBootWebSecurityConfiguration$DefaultConfigurerAdapter.class */
    static class DefaultConfigurerAdapter extends WebSecurityConfigurerAdapter {
        DefaultConfigurerAdapter() {
        }
    }
}