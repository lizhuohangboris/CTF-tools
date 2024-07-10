package org.springframework.boot.autoconfigure.freemarker;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ConditionalOnEnabledResourceChain;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
@ConditionalOnClass({Servlet.class, FreeMarkerConfigurer.class})
@AutoConfigureAfter({WebMvcAutoConfiguration.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/freemarker/FreeMarkerServletWebConfiguration.class */
class FreeMarkerServletWebConfiguration extends AbstractFreeMarkerConfiguration {
    protected FreeMarkerServletWebConfiguration(FreeMarkerProperties properties) {
        super(properties);
    }

    @ConditionalOnMissingBean({FreeMarkerConfig.class})
    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        applyProperties(configurer);
        return configurer;
    }

    @Bean
    public freemarker.template.Configuration freeMarkerConfiguration(FreeMarkerConfig configurer) {
        return configurer.getConfiguration();
    }

    @ConditionalOnMissingBean(name = {"freeMarkerViewResolver"})
    @ConditionalOnProperty(name = {"spring.freemarker.enabled"}, matchIfMissing = true)
    @Bean
    public FreeMarkerViewResolver freeMarkerViewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        getProperties().applyToMvcViewResolver(resolver);
        return resolver;
    }

    @ConditionalOnMissingFilterBean({ResourceUrlEncodingFilter.class})
    @ConditionalOnEnabledResourceChain
    @Bean
    public FilterRegistrationBean<ResourceUrlEncodingFilter> resourceUrlEncodingFilter() {
        FilterRegistrationBean<ResourceUrlEncodingFilter> registration = new FilterRegistrationBean<>(new ResourceUrlEncodingFilter(), new ServletRegistrationBean[0]);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
        return registration;
    }
}