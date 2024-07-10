package org.springframework.boot.autoconfigure.web.servlet;

import javax.servlet.ServletRequest;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.server.ErrorPageRegistrarBeanPostProcessor;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ObjectUtils;

@EnableConfigurationProperties({ServerProperties.class})
@Configuration
@ConditionalOnClass({ServletRequest.class})
@AutoConfigureOrder(Integer.MIN_VALUE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({BeanPostProcessorsRegistrar.class, ServletWebServerFactoryConfiguration.EmbeddedTomcat.class, ServletWebServerFactoryConfiguration.EmbeddedJetty.class, ServletWebServerFactoryConfiguration.EmbeddedUndertow.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryAutoConfiguration.class */
public class ServletWebServerFactoryAutoConfiguration {
    @Bean
    public ServletWebServerFactoryCustomizer servletWebServerFactoryCustomizer(ServerProperties serverProperties) {
        return new ServletWebServerFactoryCustomizer(serverProperties);
    }

    @ConditionalOnClass(name = {"org.apache.catalina.startup.Tomcat"})
    @Bean
    public TomcatServletWebServerFactoryCustomizer tomcatServletWebServerFactoryCustomizer(ServerProperties serverProperties) {
        return new TomcatServletWebServerFactoryCustomizer(serverProperties);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryAutoConfiguration$BeanPostProcessorsRegistrar.class */
    public static class BeanPostProcessorsRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware {
        private ConfigurableListableBeanFactory beanFactory;

        @Override // org.springframework.beans.factory.BeanFactoryAware
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            if (beanFactory instanceof ConfigurableListableBeanFactory) {
                this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
            }
        }

        @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            if (this.beanFactory == null) {
                return;
            }
            registerSyntheticBeanIfMissing(registry, "webServerFactoryCustomizerBeanPostProcessor", WebServerFactoryCustomizerBeanPostProcessor.class);
            registerSyntheticBeanIfMissing(registry, "errorPageRegistrarBeanPostProcessor", ErrorPageRegistrarBeanPostProcessor.class);
        }

        private void registerSyntheticBeanIfMissing(BeanDefinitionRegistry registry, String name, Class<?> beanClass) {
            if (ObjectUtils.isEmpty((Object[]) this.beanFactory.getBeanNamesForType(beanClass, true, false))) {
                RootBeanDefinition beanDefinition = new RootBeanDefinition(beanClass);
                beanDefinition.setSynthetic(true);
                registry.registerBeanDefinition(name, beanDefinition);
            }
        }
    }
}