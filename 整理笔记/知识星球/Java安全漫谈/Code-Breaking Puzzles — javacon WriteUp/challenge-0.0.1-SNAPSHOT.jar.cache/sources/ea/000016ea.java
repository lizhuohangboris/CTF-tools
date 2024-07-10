package org.springframework.boot.autoconfigure.jdbc;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
@Import({DataSourceInitializerInvoker.class, Registrar.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceInitializationConfiguration.class */
class DataSourceInitializationConfiguration {
    DataSourceInitializationConfiguration() {
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceInitializationConfiguration$Registrar.class */
    static class Registrar implements ImportBeanDefinitionRegistrar {
        private static final String BEAN_NAME = "dataSourceInitializerPostProcessor";

        Registrar() {
        }

        @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            if (!registry.containsBeanDefinition(BEAN_NAME)) {
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClass(DataSourceInitializerPostProcessor.class);
                beanDefinition.setRole(2);
                beanDefinition.setSynthetic(true);
                registry.registerBeanDefinition(BEAN_NAME, beanDefinition);
            }
        }
    }
}