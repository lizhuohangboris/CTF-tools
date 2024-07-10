package org.springframework.boot.autoconfigure.freemarker;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@Configuration
@ConditionalOnNotWebApplication
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/freemarker/FreeMarkerNonWebConfiguration.class */
class FreeMarkerNonWebConfiguration extends AbstractFreeMarkerConfiguration {
    FreeMarkerNonWebConfiguration(FreeMarkerProperties properties) {
        super(properties);
    }

    @ConditionalOnMissingBean
    @Bean
    public FreeMarkerConfigurationFactoryBean freeMarkerConfiguration() {
        FreeMarkerConfigurationFactoryBean freeMarkerFactoryBean = new FreeMarkerConfigurationFactoryBean();
        applyProperties(freeMarkerFactoryBean);
        return freeMarkerFactoryBean;
    }
}