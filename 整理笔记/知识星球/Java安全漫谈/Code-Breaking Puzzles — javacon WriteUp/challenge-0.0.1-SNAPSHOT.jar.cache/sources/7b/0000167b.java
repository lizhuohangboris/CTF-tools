package org.springframework.boot.autoconfigure.freemarker;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfig;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerViewResolver;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@AutoConfigureAfter({WebFluxAutoConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/freemarker/FreeMarkerReactiveWebConfiguration.class */
class FreeMarkerReactiveWebConfiguration extends AbstractFreeMarkerConfiguration {
    FreeMarkerReactiveWebConfiguration(FreeMarkerProperties properties) {
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
        resolver.setPrefix(getProperties().getPrefix());
        resolver.setSuffix(getProperties().getSuffix());
        resolver.setRequestContextAttribute(getProperties().getRequestContextAttribute());
        resolver.setViewNames(getProperties().getViewNames());
        return resolver;
    }
}