package org.springframework.boot.context.properties;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/EnableConfigurationPropertiesImportSelector.class */
class EnableConfigurationPropertiesImportSelector implements ImportSelector {
    private static final String[] IMPORTS = {ConfigurationPropertiesBeanRegistrar.class.getName(), ConfigurationPropertiesBindingPostProcessorRegistrar.class.getName()};

    EnableConfigurationPropertiesImportSelector() {
    }

    @Override // org.springframework.context.annotation.ImportSelector
    public String[] selectImports(AnnotationMetadata metadata) {
        return IMPORTS;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/EnableConfigurationPropertiesImportSelector$ConfigurationPropertiesBeanRegistrar.class */
    public static class ConfigurationPropertiesBeanRegistrar implements ImportBeanDefinitionRegistrar {
        @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            getTypes(metadata).forEach(type -> {
                register(registry, (ConfigurableListableBeanFactory) registry, type);
            });
        }

        private List<Class<?>> getTypes(AnnotationMetadata metadata) {
            MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(EnableConfigurationProperties.class.getName(), false);
            return collectClasses(attributes != null ? (List) attributes.get("value") : Collections.emptyList());
        }

        private List<Class<?>> collectClasses(List<?> values) {
            return (List) values.stream().flatMap(value -> {
                return Arrays.stream((Object[]) value);
            }).map(o -> {
                return (Class) o;
            }).filter(type -> {
                return Void.TYPE != type;
            }).collect(Collectors.toList());
        }

        private void register(BeanDefinitionRegistry registry, ConfigurableListableBeanFactory beanFactory, Class<?> type) {
            String name = getName(type);
            if (!containsBeanDefinition(beanFactory, name)) {
                registerBeanDefinition(registry, name, type);
            }
        }

        private String getName(Class<?> type) {
            ConfigurationProperties annotation = (ConfigurationProperties) AnnotationUtils.findAnnotation(type, (Class<Annotation>) ConfigurationProperties.class);
            String prefix = annotation != null ? annotation.prefix() : "";
            return StringUtils.hasText(prefix) ? prefix + "-" + type.getName() : type.getName();
        }

        private boolean containsBeanDefinition(ConfigurableListableBeanFactory beanFactory, String name) {
            if (beanFactory.containsBeanDefinition(name)) {
                return true;
            }
            BeanFactory parent = beanFactory.getParentBeanFactory();
            if (parent instanceof ConfigurableListableBeanFactory) {
                return containsBeanDefinition((ConfigurableListableBeanFactory) parent, name);
            }
            return false;
        }

        private void registerBeanDefinition(BeanDefinitionRegistry registry, String name, Class<?> type) {
            assertHasAnnotation(type);
            GenericBeanDefinition definition = new GenericBeanDefinition();
            definition.setBeanClass(type);
            registry.registerBeanDefinition(name, definition);
        }

        private void assertHasAnnotation(Class<?> type) {
            Assert.notNull(AnnotationUtils.findAnnotation(type, (Class<Annotation>) ConfigurationProperties.class), () -> {
                return "No " + ConfigurationProperties.class.getSimpleName() + " annotation found on  '" + type.getName() + "'.";
            });
        }
    }
}