package org.springframework.boot.context.properties;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBindingPostProcessorRegistrar.class */
public class ConfigurationPropertiesBindingPostProcessorRegistrar implements ImportBeanDefinitionRegistrar {
    @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(ConfigurationPropertiesBindingPostProcessor.BEAN_NAME)) {
            registerConfigurationPropertiesBindingPostProcessor(registry);
            registerConfigurationBeanFactoryMetadata(registry);
        }
    }

    private void registerConfigurationPropertiesBindingPostProcessor(BeanDefinitionRegistry registry) {
        GenericBeanDefinition definition = new GenericBeanDefinition();
        definition.setBeanClass(ConfigurationPropertiesBindingPostProcessor.class);
        definition.setRole(2);
        registry.registerBeanDefinition(ConfigurationPropertiesBindingPostProcessor.BEAN_NAME, definition);
    }

    private void registerConfigurationBeanFactoryMetadata(BeanDefinitionRegistry registry) {
        GenericBeanDefinition definition = new GenericBeanDefinition();
        definition.setBeanClass(ConfigurationBeanFactoryMetadata.class);
        definition.setRole(2);
        registry.registerBeanDefinition(ConfigurationBeanFactoryMetadata.BEAN_NAME, definition);
    }
}