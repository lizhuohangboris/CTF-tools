package org.springframework.boot.autoconfigure.data.web;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableConfigurationProperties({SpringDataWebProperties.class})
@AutoConfigureAfter({RepositoryRestMvcAutoConfiguration.class})
@ConditionalOnMissingBean({PageableHandlerMethodArgumentResolver.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Configuration
@ConditionalOnClass({PageableHandlerMethodArgumentResolver.class, WebMvcConfigurer.class})
@EnableSpringDataWebSupport
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/web/SpringDataWebAutoConfiguration.class */
public class SpringDataWebAutoConfiguration {
    private final SpringDataWebProperties properties;

    public SpringDataWebAutoConfiguration(SpringDataWebProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer() {
        return resolver -> {
            SpringDataWebProperties.Pageable pageable = this.properties.getPageable();
            resolver.setPageParameterName(pageable.getPageParameter());
            resolver.setSizeParameterName(pageable.getSizeParameter());
            resolver.setOneIndexedParameters(pageable.isOneIndexedParameters());
            resolver.setPrefix(pageable.getPrefix());
            resolver.setQualifierDelimiter(pageable.getQualifierDelimiter());
            resolver.setFallbackPageable(PageRequest.of(0, pageable.getDefaultPageSize()));
            resolver.setMaxPageSize(pageable.getMaxPageSize());
        };
    }

    @ConditionalOnMissingBean
    @Bean
    public SortHandlerMethodArgumentResolverCustomizer sortCustomizer() {
        return resolver -> {
            resolver.setSortParameter(this.properties.getSort().getSortParameter());
        };
    }
}