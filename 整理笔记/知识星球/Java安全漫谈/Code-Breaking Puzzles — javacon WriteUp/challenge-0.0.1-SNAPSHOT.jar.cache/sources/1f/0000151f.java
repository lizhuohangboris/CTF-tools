package org.springframework.boot.autoconfigure;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/AbstractDependsOnBeanFactoryPostProcessor.class */
public abstract class AbstractDependsOnBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    private final Class<?> beanClass;
    private final Class<? extends FactoryBean<?>> factoryBeanClass;
    private final String[] dependsOn;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractDependsOnBeanFactoryPostProcessor(Class<?> beanClass, Class<? extends FactoryBean<?>> factoryBeanClass, String... dependsOn) {
        this.beanClass = beanClass;
        this.factoryBeanClass = factoryBeanClass;
        this.dependsOn = dependsOn;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractDependsOnBeanFactoryPostProcessor(Class<?> beanClass, String... dependsOn) {
        this(beanClass, null, dependsOn);
    }

    @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        String[] strArr;
        for (String beanName : getBeanNames(beanFactory)) {
            BeanDefinition definition = getBeanDefinition(beanName, beanFactory);
            String[] dependencies = definition.getDependsOn();
            for (String bean : this.dependsOn) {
                dependencies = StringUtils.addStringToArray(dependencies, bean);
            }
            definition.setDependsOn(dependencies);
        }
    }

    private Iterable<String> getBeanNames(ListableBeanFactory beanFactory) {
        String[] beanNamesForTypeIncludingAncestors;
        Set<String> names = new HashSet<>();
        names.addAll(Arrays.asList(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, this.beanClass, true, false)));
        if (this.factoryBeanClass != null) {
            for (String factoryBeanName : BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, this.factoryBeanClass, true, false)) {
                names.add(BeanFactoryUtils.transformedBeanName(factoryBeanName));
            }
        }
        return names;
    }

    private static BeanDefinition getBeanDefinition(String beanName, ConfigurableListableBeanFactory beanFactory) {
        try {
            return beanFactory.getBeanDefinition(beanName);
        } catch (NoSuchBeanDefinitionException ex) {
            BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();
            if (parentBeanFactory instanceof ConfigurableListableBeanFactory) {
                return getBeanDefinition(beanName, (ConfigurableListableBeanFactory) parentBeanFactory);
            }
            throw ex;
        }
    }
}