package org.springframework.boot.autoconfigure.web.reactive;

import java.time.Duration;
import java.util.Collection;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidatorAdapter;
import org.springframework.boot.autoconfigure.web.ConditionalOnEnabledResourceChain;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.format.WebConversionService;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.boot.web.reactive.filter.OrderedHiddenHttpMethodFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.util.ClassUtils;
import org.springframework.validation.Validator;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;
import org.springframework.web.reactive.config.ResourceHandlerRegistration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.reactive.result.view.ViewResolver;

@Configuration
@ConditionalOnClass({WebFluxConfigurer.class})
@AutoConfigureAfter({ReactiveWebServerFactoryAutoConfiguration.class, CodecsAutoConfiguration.class, ValidationAutoConfiguration.class})
@ConditionalOnMissingBean({WebFluxConfigurationSupport.class})
@AutoConfigureOrder(ConfigFileApplicationListener.DEFAULT_ORDER)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxAutoConfiguration.class */
public class WebFluxAutoConfiguration {
    @ConditionalOnMissingBean({HiddenHttpMethodFilter.class})
    @ConditionalOnProperty(prefix = "spring.webflux.hiddenmethod.filter", name = {"enabled"}, matchIfMissing = true)
    @Bean
    public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new OrderedHiddenHttpMethodFilter();
    }

    @EnableConfigurationProperties({ResourceProperties.class, WebFluxProperties.class})
    @Configuration
    @Import({EnableWebFluxConfiguration.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxAutoConfiguration$WebFluxConfig.class */
    public static class WebFluxConfig implements WebFluxConfigurer {
        private static final Log logger = LogFactory.getLog(WebFluxConfig.class);
        private final ResourceProperties resourceProperties;
        private final WebFluxProperties webFluxProperties;
        private final ListableBeanFactory beanFactory;
        private final ObjectProvider<HandlerMethodArgumentResolver> argumentResolvers;
        private final ObjectProvider<CodecCustomizer> codecCustomizers;
        private final ResourceHandlerRegistrationCustomizer resourceHandlerRegistrationCustomizer;
        private final ObjectProvider<ViewResolver> viewResolvers;

        public WebFluxConfig(ResourceProperties resourceProperties, WebFluxProperties webFluxProperties, ListableBeanFactory beanFactory, ObjectProvider<HandlerMethodArgumentResolver> resolvers, ObjectProvider<CodecCustomizer> codecCustomizers, ObjectProvider<ResourceHandlerRegistrationCustomizer> resourceHandlerRegistrationCustomizer, ObjectProvider<ViewResolver> viewResolvers) {
            this.resourceProperties = resourceProperties;
            this.webFluxProperties = webFluxProperties;
            this.beanFactory = beanFactory;
            this.argumentResolvers = resolvers;
            this.codecCustomizers = codecCustomizers;
            this.resourceHandlerRegistrationCustomizer = resourceHandlerRegistrationCustomizer.getIfAvailable();
            this.viewResolvers = viewResolvers;
        }

        public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
            Stream<HandlerMethodArgumentResolver> orderedStream = this.argumentResolvers.orderedStream();
            configurer.getClass();
            orderedStream.forEach(xva$0 -> {
                configurer.addCustomResolver(new HandlerMethodArgumentResolver[]{xva$0});
            });
        }

        public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
            this.codecCustomizers.orderedStream().forEach(customizer -> {
                customizer.customize(configurer);
            });
        }

        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            if (!this.resourceProperties.isAddMappings()) {
                logger.debug("Default resource handling disabled");
                return;
            }
            if (!registry.hasMappingForPattern("/webjars/**")) {
                ResourceHandlerRegistration registration = registry.addResourceHandler(new String[]{"/webjars/**"}).addResourceLocations(new String[]{"classpath:/META-INF/resources/webjars/"});
                configureResourceCaching(registration);
                customizeResourceHandlerRegistration(registration);
            }
            String staticPathPattern = this.webFluxProperties.getStaticPathPattern();
            if (!registry.hasMappingForPattern(staticPathPattern)) {
                ResourceHandlerRegistration registration2 = registry.addResourceHandler(new String[]{staticPathPattern}).addResourceLocations(this.resourceProperties.getStaticLocations());
                configureResourceCaching(registration2);
                customizeResourceHandlerRegistration(registration2);
            }
        }

        private void configureResourceCaching(ResourceHandlerRegistration registration) {
            Duration cachePeriod = this.resourceProperties.getCache().getPeriod();
            ResourceProperties.Cache.Cachecontrol cacheControl = this.resourceProperties.getCache().getCachecontrol();
            if (cachePeriod != null && cacheControl.getMaxAge() == null) {
                cacheControl.setMaxAge(cachePeriod);
            }
            registration.setCacheControl(cacheControl.toHttpCacheControl());
        }

        public void configureViewResolvers(ViewResolverRegistry registry) {
            Stream<ViewResolver> orderedStream = this.viewResolvers.orderedStream();
            registry.getClass();
            orderedStream.forEach(this::viewResolver);
        }

        public void addFormatters(FormatterRegistry registry) {
            for (Converter<?, ?> converter : getBeansOfType(Converter.class)) {
                registry.addConverter(converter);
            }
            for (GenericConverter converter2 : getBeansOfType(GenericConverter.class)) {
                registry.addConverter(converter2);
            }
            for (Formatter<?> formatter : getBeansOfType(Formatter.class)) {
                registry.addFormatter(formatter);
            }
        }

        private <T> Collection<T> getBeansOfType(Class<T> type) {
            return this.beanFactory.getBeansOfType(type).values();
        }

        private void customizeResourceHandlerRegistration(ResourceHandlerRegistration registration) {
            if (this.resourceHandlerRegistrationCustomizer != null) {
                this.resourceHandlerRegistrationCustomizer.customize(registration);
            }
        }
    }

    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxAutoConfiguration$EnableWebFluxConfiguration.class */
    public static class EnableWebFluxConfiguration extends DelegatingWebFluxConfiguration {
        private final WebFluxProperties webFluxProperties;
        private final WebFluxRegistrations webFluxRegistrations;

        public EnableWebFluxConfiguration(WebFluxProperties webFluxProperties, ObjectProvider<WebFluxRegistrations> webFluxRegistrations) {
            this.webFluxProperties = webFluxProperties;
            this.webFluxRegistrations = webFluxRegistrations.getIfUnique();
        }

        @Bean
        public FormattingConversionService webFluxConversionService() {
            WebConversionService conversionService = new WebConversionService(this.webFluxProperties.getDateFormat());
            addFormatters(conversionService);
            return conversionService;
        }

        @Bean
        public Validator webFluxValidator() {
            if (!ClassUtils.isPresent("javax.validation.Validator", getClass().getClassLoader())) {
                return super.webFluxValidator();
            }
            return ValidatorAdapter.get(getApplicationContext(), getValidator());
        }

        protected RequestMappingHandlerAdapter createRequestMappingHandlerAdapter() {
            if (this.webFluxRegistrations != null && this.webFluxRegistrations.getRequestMappingHandlerAdapter() != null) {
                return this.webFluxRegistrations.getRequestMappingHandlerAdapter();
            }
            return super.createRequestMappingHandlerAdapter();
        }

        protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
            if (this.webFluxRegistrations != null && this.webFluxRegistrations.getRequestMappingHandlerMapping() != null) {
                return this.webFluxRegistrations.getRequestMappingHandlerMapping();
            }
            return super.createRequestMappingHandlerMapping();
        }
    }

    @ConditionalOnEnabledResourceChain
    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxAutoConfiguration$ResourceChainCustomizerConfiguration.class */
    static class ResourceChainCustomizerConfiguration {
        ResourceChainCustomizerConfiguration() {
        }

        @Bean
        public ResourceChainResourceHandlerRegistrationCustomizer resourceHandlerRegistrationCustomizer() {
            return new ResourceChainResourceHandlerRegistrationCustomizer();
        }
    }
}