package org.springframework.boot.autoconfigure.web.reactive;

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
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.util.ObjectUtils;

@EnableConfigurationProperties({ServerProperties.class})
@Configuration
@ConditionalOnClass({ReactiveHttpInputMessage.class})
@AutoConfigureOrder(Integer.MIN_VALUE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Import({BeanPostProcessorsRegistrar.class, ReactiveWebServerFactoryConfiguration.EmbeddedTomcat.class, ReactiveWebServerFactoryConfiguration.EmbeddedJetty.class, ReactiveWebServerFactoryConfiguration.EmbeddedUndertow.class, ReactiveWebServerFactoryConfiguration.EmbeddedNetty.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryAutoConfiguration.class */
public class ReactiveWebServerFactoryAutoConfiguration {
    @Bean
    public ReactiveWebServerFactoryCustomizer reactiveWebServerFactoryCustomizer(ServerProperties serverProperties) {
        return new ReactiveWebServerFactoryCustomizer(serverProperties);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryAutoConfiguration$BeanPostProcessorsRegistrar.class */
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