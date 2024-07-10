package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/BeanDefinitionReaderUtils.class */
public abstract class BeanDefinitionReaderUtils {
    public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";

    public static AbstractBeanDefinition createBeanDefinition(@Nullable String parentName, @Nullable String className, @Nullable ClassLoader classLoader) throws ClassNotFoundException {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setParentName(parentName);
        if (className != null) {
            if (classLoader != null) {
                bd.setBeanClass(ClassUtils.forName(className, classLoader));
            } else {
                bd.setBeanClassName(className);
            }
        }
        return bd;
    }

    public static String generateBeanName(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
        return generateBeanName(beanDefinition, registry, false);
    }

    public static String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry, boolean isInnerBean) throws BeanDefinitionStoreException {
        String generatedBeanName = definition.getBeanClassName();
        if (generatedBeanName == null) {
            if (definition.getParentName() != null) {
                generatedBeanName = definition.getParentName() + "$child";
            } else if (definition.getFactoryBeanName() != null) {
                generatedBeanName = definition.getFactoryBeanName() + "$created";
            }
        }
        if (!StringUtils.hasText(generatedBeanName)) {
            throw new BeanDefinitionStoreException("Unnamed bean definition specifies neither 'class' nor 'parent' nor 'factory-bean' - can't generate bean name");
        }
        if (isInnerBean) {
            String id = generatedBeanName + "#" + ObjectUtils.getIdentityHexString(definition);
            return id;
        }
        return uniqueBeanName(generatedBeanName, registry);
    }

    public static String uniqueBeanName(String beanName, BeanDefinitionRegistry registry) {
        String id = beanName;
        int counter = -1;
        while (true) {
            if (counter == -1 || registry.containsBeanDefinition(id)) {
                counter++;
                id = beanName + "#" + counter;
            } else {
                return id;
            }
        }
    }

    public static void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
        String beanName = definitionHolder.getBeanName();
        registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());
        String[] aliases = definitionHolder.getAliases();
        if (aliases != null) {
            for (String alias : aliases) {
                registry.registerAlias(beanName, alias);
            }
        }
    }

    public static String registerWithGeneratedName(AbstractBeanDefinition definition, BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
        String generatedName = generateBeanName(definition, registry, false);
        registry.registerBeanDefinition(generatedName, definition);
        return generatedName;
    }
}