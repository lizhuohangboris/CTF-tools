package org.springframework.boot.autoconfigure.integration;

import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.IntegrationComponentScanRegistrar;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfigurationScanRegistrar.class */
class IntegrationAutoConfigurationScanRegistrar extends IntegrationComponentScanRegistrar implements BeanFactoryAware {
    private BeanFactory beanFactory;

    IntegrationAutoConfigurationScanRegistrar() {
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, final BeanDefinitionRegistry registry) {
        super.registerBeanDefinitions(new StandardAnnotationMetadata(IntegrationComponentScanConfiguration.class, true), registry);
    }

    protected Collection<String> getBasePackages(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (AutoConfigurationPackages.has(this.beanFactory)) {
            return AutoConfigurationPackages.get(this.beanFactory);
        }
        return Collections.emptyList();
    }

    @IntegrationComponentScan
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfigurationScanRegistrar$IntegrationComponentScanConfiguration.class */
    private static class IntegrationComponentScanConfiguration {
        private IntegrationComponentScanConfiguration() {
        }
    }
}