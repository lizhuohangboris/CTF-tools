package org.springframework.beans.factory.support;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/AbstractBeanDefinitionReader.class */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader, EnvironmentCapable {
    private final BeanDefinitionRegistry registry;
    @Nullable
    private ResourceLoader resourceLoader;
    @Nullable
    private ClassLoader beanClassLoader;
    private Environment environment;
    protected final Log logger = LogFactory.getLog(getClass());
    private BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        this.registry = registry;
        if (this.registry instanceof ResourceLoader) {
            this.resourceLoader = (ResourceLoader) this.registry;
        } else {
            this.resourceLoader = new PathMatchingResourcePatternResolver();
        }
        if (this.registry instanceof EnvironmentCapable) {
            this.environment = ((EnvironmentCapable) this.registry).getEnvironment();
        } else {
            this.environment = new StandardEnvironment();
        }
    }

    public final BeanDefinitionRegistry getBeanFactory() {
        return this.registry;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    public final BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }

    public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    @Nullable
    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    @Nullable
    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    public void setEnvironment(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
    }

    @Override // org.springframework.core.env.EnvironmentCapable
    public Environment getEnvironment() {
        return this.environment;
    }

    public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator != null ? beanNameGenerator : new DefaultBeanNameGenerator();
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    public BeanNameGenerator getBeanNameGenerator() {
        return this.beanNameGenerator;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    public int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException {
        Assert.notNull(resources, "Resource array must not be null");
        int count = 0;
        for (Resource resource : resources) {
            count += loadBeanDefinitions(resource);
        }
        return count;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    public int loadBeanDefinitions(String location) throws BeanDefinitionStoreException {
        return loadBeanDefinitions(location, null);
    }

    public int loadBeanDefinitions(String location, @Nullable Set<Resource> actualResources) throws BeanDefinitionStoreException {
        ResourceLoader resourceLoader = getResourceLoader();
        if (resourceLoader == null) {
            throw new BeanDefinitionStoreException("Cannot load bean definitions from location [" + location + "]: no ResourceLoader available");
        }
        if (resourceLoader instanceof ResourcePatternResolver) {
            try {
                Resource[] resources = ((ResourcePatternResolver) resourceLoader).getResources(location);
                int count = loadBeanDefinitions(resources);
                if (actualResources != null) {
                    Collections.addAll(actualResources, resources);
                }
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Loaded " + count + " bean definitions from location pattern [" + location + "]");
                }
                return count;
            } catch (IOException ex) {
                throw new BeanDefinitionStoreException("Could not resolve bean definition resource pattern [" + location + "]", ex);
            }
        }
        Resource resource = resourceLoader.getResource(location);
        int count2 = loadBeanDefinitions(resource);
        if (actualResources != null) {
            actualResources.add(resource);
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Loaded " + count2 + " bean definitions from location [" + location + "]");
        }
        return count2;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    public int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException {
        Assert.notNull(locations, "Location array must not be null");
        int count = 0;
        for (String location : locations) {
            count += loadBeanDefinitions(location);
        }
        return count;
    }
}