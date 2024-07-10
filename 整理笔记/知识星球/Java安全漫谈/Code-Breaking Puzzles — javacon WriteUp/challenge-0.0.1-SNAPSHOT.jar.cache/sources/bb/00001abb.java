package org.springframework.boot.web.reactive.context;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/reactive/context/AnnotationConfigReactiveWebApplicationContext.class */
public class AnnotationConfigReactiveWebApplicationContext extends AbstractRefreshableConfigApplicationContext implements ConfigurableReactiveWebApplicationContext, AnnotationConfigRegistry {
    private BeanNameGenerator beanNameGenerator;
    private ScopeMetadataResolver scopeMetadataResolver;
    private final Set<Class<?>> annotatedClasses = new LinkedHashSet();
    private final Set<String> basePackages = new LinkedHashSet();

    @Override // org.springframework.context.support.AbstractApplicationContext
    protected ConfigurableEnvironment createEnvironment() {
        return new StandardReactiveWebEnvironment();
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

    protected BeanNameGenerator getBeanNameGenerator() {
        return this.beanNameGenerator;
    }

    public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
        this.scopeMetadataResolver = scopeMetadataResolver;
    }

    protected ScopeMetadataResolver getScopeMetadataResolver() {
        return this.scopeMetadataResolver;
    }

    @Override // org.springframework.context.annotation.AnnotationConfigRegistry
    public void register(Class<?>... annotatedClasses) {
        Assert.notEmpty(annotatedClasses, "At least one annotated class must be specified");
        this.annotatedClasses.addAll(Arrays.asList(annotatedClasses));
    }

    @Override // org.springframework.context.annotation.AnnotationConfigRegistry
    public void scan(String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        this.basePackages.addAll(Arrays.asList(basePackages));
    }

    @Override // org.springframework.context.support.AbstractRefreshableApplicationContext
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        AnnotatedBeanDefinitionReader reader = getAnnotatedBeanDefinitionReader(beanFactory);
        ClassPathBeanDefinitionScanner scanner = getClassPathBeanDefinitionScanner(beanFactory);
        applyBeanNameGenerator(beanFactory, reader, scanner);
        applyScopeMetadataResolver(reader, scanner);
        loadBeanDefinitions(reader, scanner);
    }

    private void applyBeanNameGenerator(DefaultListableBeanFactory beanFactory, AnnotatedBeanDefinitionReader reader, ClassPathBeanDefinitionScanner scanner) {
        BeanNameGenerator beanNameGenerator = getBeanNameGenerator();
        if (beanNameGenerator != null) {
            reader.setBeanNameGenerator(beanNameGenerator);
            scanner.setBeanNameGenerator(beanNameGenerator);
            beanFactory.registerSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, beanNameGenerator);
        }
    }

    private void applyScopeMetadataResolver(AnnotatedBeanDefinitionReader reader, ClassPathBeanDefinitionScanner scanner) {
        ScopeMetadataResolver scopeMetadataResolver = getScopeMetadataResolver();
        if (scopeMetadataResolver != null) {
            reader.setScopeMetadataResolver(scopeMetadataResolver);
            scanner.setScopeMetadataResolver(scopeMetadataResolver);
        }
    }

    private void loadBeanDefinitions(AnnotatedBeanDefinitionReader reader, ClassPathBeanDefinitionScanner scanner) throws LinkageError {
        if (!this.annotatedClasses.isEmpty()) {
            registerAnnotatedClasses(reader);
        }
        if (!this.basePackages.isEmpty()) {
            scanBasePackages(scanner);
        }
        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            registerConfigLocations(reader, scanner, configLocations);
        }
    }

    private void registerAnnotatedClasses(AnnotatedBeanDefinitionReader reader) {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Registering annotated classes: [" + StringUtils.collectionToCommaDelimitedString(this.annotatedClasses) + "]");
        }
        reader.register(ClassUtils.toClassArray(this.annotatedClasses));
    }

    private void scanBasePackages(ClassPathBeanDefinitionScanner scanner) {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Scanning base packages: [" + StringUtils.collectionToCommaDelimitedString(this.basePackages) + "]");
        }
        scanner.scan(StringUtils.toStringArray(this.basePackages));
    }

    private void registerConfigLocations(AnnotatedBeanDefinitionReader reader, ClassPathBeanDefinitionScanner scanner, String[] configLocations) throws LinkageError {
        for (String configLocation : configLocations) {
            try {
                register(reader, configLocation);
            } catch (ClassNotFoundException ex) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Could not load class for config location [" + configLocation + "] - trying package scan. " + ex);
                }
                int count = scanner.scan(configLocation);
                if (this.logger.isInfoEnabled()) {
                    logScanResult(configLocation, count);
                }
            }
        }
    }

    private void register(AnnotatedBeanDefinitionReader reader, String configLocation) throws ClassNotFoundException, LinkageError {
        Class<?> clazz = ClassUtils.forName(configLocation, getClassLoader());
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Successfully resolved class for [" + configLocation + "]");
        }
        reader.register(clazz);
    }

    private void logScanResult(String configLocation, int count) {
        if (count == 0) {
            this.logger.info("No annotated classes found for specified class/package [" + configLocation + "]");
        } else {
            this.logger.info("Found " + count + " annotated classes in package [" + configLocation + "]");
        }
    }

    protected AnnotatedBeanDefinitionReader getAnnotatedBeanDefinitionReader(DefaultListableBeanFactory beanFactory) {
        return new AnnotatedBeanDefinitionReader(beanFactory, getEnvironment());
    }

    protected ClassPathBeanDefinitionScanner getClassPathBeanDefinitionScanner(DefaultListableBeanFactory beanFactory) {
        return new ClassPathBeanDefinitionScanner(beanFactory, true, getEnvironment());
    }

    @Override // org.springframework.core.io.DefaultResourceLoader
    protected Resource getResourceByPath(String path) {
        return new FilteredReactiveWebContextResource(path);
    }
}