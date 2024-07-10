package org.springframework.boot.autoconfigure.groovy.template;

import groovy.text.markup.MarkupTemplateEngine;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import javax.annotation.PostConstruct;
import javax.servlet.Servlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.template.TemplateLocation;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.groovy.GroovyMarkupConfig;
import org.springframework.web.servlet.view.groovy.GroovyMarkupConfigurer;
import org.springframework.web.servlet.view.groovy.GroovyMarkupViewResolver;

@EnableConfigurationProperties({GroovyTemplateProperties.class})
@Configuration
@ConditionalOnClass({MarkupTemplateEngine.class})
@AutoConfigureAfter({WebMvcAutoConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/groovy/template/GroovyTemplateAutoConfiguration.class */
public class GroovyTemplateAutoConfiguration {
    private static final Log logger = LogFactory.getLog(GroovyTemplateAutoConfiguration.class);

    @Configuration
    @ConditionalOnClass({GroovyMarkupConfigurer.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/groovy/template/GroovyTemplateAutoConfiguration$GroovyMarkupConfiguration.class */
    public static class GroovyMarkupConfiguration {
        private final ApplicationContext applicationContext;
        private final GroovyTemplateProperties properties;
        private final MarkupTemplateEngine templateEngine;

        public GroovyMarkupConfiguration(ApplicationContext applicationContext, GroovyTemplateProperties properties, ObjectProvider<MarkupTemplateEngine> templateEngine) {
            this.applicationContext = applicationContext;
            this.properties = properties;
            this.templateEngine = templateEngine.getIfAvailable();
        }

        @PostConstruct
        public void checkTemplateLocationExists() {
            if (this.properties.isCheckTemplateLocation() && !isUsingGroovyAllJar()) {
                TemplateLocation location = new TemplateLocation(this.properties.getResourceLoaderPath());
                if (!location.exists(this.applicationContext)) {
                    GroovyTemplateAutoConfiguration.logger.warn("Cannot find template location: " + location + " (please add some templates, check your Groovy configuration, or set spring.groovy.template.check-template-location=false)");
                }
            }
        }

        private boolean isUsingGroovyAllJar() {
            try {
                ProtectionDomain domain = MarkupTemplateEngine.class.getProtectionDomain();
                CodeSource codeSource = domain.getCodeSource();
                if (codeSource != null) {
                    if (codeSource.getLocation().toString().contains("-all")) {
                        return true;
                    }
                    return false;
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }

        @ConditionalOnMissingBean({GroovyMarkupConfig.class})
        @ConfigurationProperties(prefix = "spring.groovy.template.configuration")
        @Bean
        public GroovyMarkupConfigurer groovyMarkupConfigurer() {
            GroovyMarkupConfigurer configurer = new GroovyMarkupConfigurer();
            configurer.setResourceLoaderPath(this.properties.getResourceLoaderPath());
            configurer.setCacheTemplates(this.properties.isCache());
            if (this.templateEngine != null) {
                configurer.setTemplateEngine(this.templateEngine);
            }
            return configurer;
        }
    }

    @Configuration
    @ConditionalOnClass({Servlet.class, LocaleContextHolder.class, UrlBasedViewResolver.class})
    @ConditionalOnProperty(name = {"spring.groovy.template.enabled"}, matchIfMissing = true)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/groovy/template/GroovyTemplateAutoConfiguration$GroovyWebConfiguration.class */
    public static class GroovyWebConfiguration {
        private final GroovyTemplateProperties properties;

        public GroovyWebConfiguration(GroovyTemplateProperties properties) {
            this.properties = properties;
        }

        @ConditionalOnMissingBean(name = {"groovyMarkupViewResolver"})
        @Bean
        public GroovyMarkupViewResolver groovyMarkupViewResolver() {
            GroovyMarkupViewResolver resolver = new GroovyMarkupViewResolver();
            this.properties.applyToMvcViewResolver(resolver);
            return resolver;
        }
    }
}