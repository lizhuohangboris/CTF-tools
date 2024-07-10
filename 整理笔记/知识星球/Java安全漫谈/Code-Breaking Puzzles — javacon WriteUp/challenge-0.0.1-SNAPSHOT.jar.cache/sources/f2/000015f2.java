package org.springframework.boot.autoconfigure.data;

import java.lang.annotation.Annotation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.config.RepositoryConfigurationDelegate;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.util.Streamable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/AbstractRepositoryConfigurationSourceSupport.class */
public abstract class AbstractRepositoryConfigurationSourceSupport implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    private ResourceLoader resourceLoader;
    private BeanFactory beanFactory;
    private Environment environment;

    protected abstract Class<? extends Annotation> getAnnotation();

    protected abstract Class<?> getConfiguration();

    protected abstract RepositoryConfigurationExtension getRepositoryConfigurationExtension();

    @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        new RepositoryConfigurationDelegate(getConfigurationSource(registry), this.resourceLoader, this.environment).registerRepositoriesIn(registry, getRepositoryConfigurationExtension());
    }

    private AnnotationRepositoryConfigurationSource getConfigurationSource(BeanDefinitionRegistry beanDefinitionRegistry) {
        StandardAnnotationMetadata metadata = new StandardAnnotationMetadata(getConfiguration(), true);
        return new AnnotationRepositoryConfigurationSource(metadata, getAnnotation(), this.resourceLoader, this.environment, beanDefinitionRegistry) { // from class: org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport.1
            public Streamable<String> getBasePackages() {
                return AbstractRepositoryConfigurationSourceSupport.this.getBasePackages();
            }

            public BootstrapMode getBootstrapMode() {
                return AbstractRepositoryConfigurationSourceSupport.this.getBootstrapMode();
            }
        };
    }

    protected Streamable<String> getBasePackages() {
        return Streamable.of(AutoConfigurationPackages.get(this.beanFactory));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BootstrapMode getBootstrapMode() {
        return BootstrapMode.DEFAULT;
    }

    @Override // org.springframework.context.ResourceLoaderAware
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.context.EnvironmentAware
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}