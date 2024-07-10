package org.springframework.boot.autoconfigure.webservices;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.webservices.WebServicesProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurationSupport;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;

@EnableConfigurationProperties({WebServicesProperties.class})
@Configuration
@ConditionalOnClass({MessageDispatcherServlet.class})
@AutoConfigureAfter({ServletWebServerFactoryAutoConfiguration.class})
@ConditionalOnMissingBean({WsConfigurationSupport.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/webservices/WebServicesAutoConfiguration.class */
public class WebServicesAutoConfiguration {
    private final WebServicesProperties properties;

    public WebServicesAutoConfiguration(WebServicesProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        String path = this.properties.getPath();
        String urlMapping = path + (path.endsWith("/") ? "*" : "/*");
        ServletRegistrationBean<MessageDispatcherServlet> registration = new ServletRegistrationBean<>(servlet, urlMapping);
        WebServicesProperties.Servlet servletProperties = this.properties.getServlet();
        registration.setLoadOnStartup(servletProperties.getLoadOnStartup());
        Map<String, String> init = servletProperties.getInit();
        registration.getClass();
        init.forEach(this::addInitParameter);
        return registration;
    }

    @Conditional({OnWsdlLocationsCondition.class})
    @Bean
    public static WsdlDefinitionBeanFactoryPostProcessor wsdlDefinitionBeanFactoryPostProcessor() {
        return new WsdlDefinitionBeanFactoryPostProcessor();
    }

    @Configuration
    @EnableWs
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/webservices/WebServicesAutoConfiguration$WsConfiguration.class */
    protected static class WsConfiguration {
        protected WsConfiguration() {
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/webservices/WebServicesAutoConfiguration$WsdlDefinitionBeanFactoryPostProcessor.class */
    private static class WsdlDefinitionBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
        private ApplicationContext applicationContext;

        private WsdlDefinitionBeanFactoryPostProcessor() {
        }

        @Override // org.springframework.context.ApplicationContextAware
        public void setApplicationContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override // org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            Binder binder = Binder.get(this.applicationContext.getEnvironment());
            List<String> wsdlLocations = (List) binder.bind("spring.webservices.wsdl-locations", Bindable.listOf(String.class)).orElse(Collections.emptyList());
            for (String wsdlLocation : wsdlLocations) {
                registerBeans(wsdlLocation, "*.wsdl", SimpleWsdl11Definition.class, SimpleWsdl11Definition::new, registry);
                registerBeans(wsdlLocation, "*.xsd", SimpleXsdSchema.class, SimpleXsdSchema::new, registry);
            }
        }

        @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        }

        private <T> void registerBeans(String location, String pattern, Class<T> type, Function<Resource, T> beanSupplier, BeanDefinitionRegistry registry) {
            Resource[] resources;
            for (Resource resource : getResources(location, pattern)) {
                BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(type, () -> {
                    return beanSupplier.apply(resource);
                }).getBeanDefinition();
                registry.registerBeanDefinition(StringUtils.stripFilenameExtension(resource.getFilename()), beanDefinition);
            }
        }

        private Resource[] getResources(String location, String pattern) {
            try {
                return this.applicationContext.getResources(ensureTrailingSlash(location) + pattern);
            } catch (IOException e) {
                return new Resource[0];
            }
        }

        private String ensureTrailingSlash(String path) {
            return path.endsWith("/") ? path : path + "/";
        }
    }
}