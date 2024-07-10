package org.springframework.boot.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.type.classreading.ConcurrentReferenceCachingMetadataReaderFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/SharedMetadataReaderFactoryContextInitializer.class */
class SharedMetadataReaderFactoryContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    public static final String BEAN_NAME = "org.springframework.boot.autoconfigure.internalCachingMetadataReaderFactory";

    SharedMetadataReaderFactoryContextInitializer() {
    }

    @Override // org.springframework.context.ApplicationContextInitializer
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.addBeanFactoryPostProcessor(new CachingMetadataReaderFactoryPostProcessor());
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/SharedMetadataReaderFactoryContextInitializer$CachingMetadataReaderFactoryPostProcessor.class */
    private static class CachingMetadataReaderFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {
        private CachingMetadataReaderFactoryPostProcessor() {
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return Integer.MIN_VALUE;
        }

        @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        }

        @Override // org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            register(registry);
            configureConfigurationClassPostProcessor(registry);
        }

        private void register(BeanDefinitionRegistry registry) {
            BeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(SharedMetadataReaderFactoryBean.class, SharedMetadataReaderFactoryBean::new).getBeanDefinition();
            registry.registerBeanDefinition(SharedMetadataReaderFactoryContextInitializer.BEAN_NAME, definition);
        }

        private void configureConfigurationClassPostProcessor(BeanDefinitionRegistry registry) {
            try {
                BeanDefinition definition = registry.getBeanDefinition(AnnotationConfigUtils.CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME);
                definition.getPropertyValues().add("metadataReaderFactory", new RuntimeBeanReference(SharedMetadataReaderFactoryContextInitializer.BEAN_NAME));
            } catch (NoSuchBeanDefinitionException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/SharedMetadataReaderFactoryContextInitializer$SharedMetadataReaderFactoryBean.class */
    public static class SharedMetadataReaderFactoryBean implements FactoryBean<ConcurrentReferenceCachingMetadataReaderFactory>, BeanClassLoaderAware, ApplicationListener<ContextRefreshedEvent> {
        private ConcurrentReferenceCachingMetadataReaderFactory metadataReaderFactory;

        SharedMetadataReaderFactoryBean() {
        }

        @Override // org.springframework.beans.factory.BeanClassLoaderAware
        public void setBeanClassLoader(ClassLoader classLoader) {
            this.metadataReaderFactory = new ConcurrentReferenceCachingMetadataReaderFactory(classLoader);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.beans.factory.FactoryBean
        public ConcurrentReferenceCachingMetadataReaderFactory getObject() throws Exception {
            return this.metadataReaderFactory;
        }

        @Override // org.springframework.beans.factory.FactoryBean
        public Class<?> getObjectType() {
            return CachingMetadataReaderFactory.class;
        }

        @Override // org.springframework.beans.factory.FactoryBean
        public boolean isSingleton() {
            return true;
        }

        @Override // org.springframework.context.ApplicationListener
        public void onApplicationEvent(ContextRefreshedEvent event) {
            this.metadataReaderFactory.clearCache();
        }
    }
}