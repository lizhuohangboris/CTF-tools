package org.springframework.boot.autoconfigure.data.rest;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

@EnableConfigurationProperties({RepositoryRestProperties.class})
@AutoConfigureAfter({HttpMessageConvertersAutoConfiguration.class, JacksonAutoConfiguration.class})
@ConditionalOnMissingBean({RepositoryRestMvcConfiguration.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({RepositoryRestMvcConfiguration.class})
@Configuration
@ConditionalOnClass({RepositoryRestMvcConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/rest/RepositoryRestMvcAutoConfiguration.class */
public class RepositoryRestMvcAutoConfiguration {
    @Bean
    public SpringBootRepositoryRestConfigurer springBootRepositoryRestConfigurer() {
        return new SpringBootRepositoryRestConfigurer();
    }
}