package org.springframework.boot.autoconfigure.condition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/BeanTypeRegistry.class */
final class BeanTypeRegistry implements SmartInitializingSingleton {
    static final String FACTORY_BEAN_OBJECT_TYPE = "factoryBeanObjectType";
    private final DefaultListableBeanFactory beanFactory;
    private final Map<String, ResolvableType> beanTypes = new HashMap();
    private final Map<String, RootBeanDefinition> beanDefinitions = new HashMap();
    private static final Log logger = LogFactory.getLog(BeanTypeRegistry.class);
    private static final String BEAN_NAME = BeanTypeRegistry.class.getName();

    /* JADX INFO: Access modifiers changed from: package-private */
    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/BeanTypeRegistry$TypeExtractor.class */
    public interface TypeExtractor {
        Class<?> getBeanType(ResolvableType type);
    }

    private BeanTypeRegistry(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Set<String> getNamesForType(Class<?> type, TypeExtractor typeExtractor) {
        updateTypesIfNecessary();
        return (Set) this.beanTypes.entrySet().stream().filter(entry -> {
            Class<?> beanType = extractType((ResolvableType) entry.getValue(), typeExtractor);
            return beanType != null && type.isAssignableFrom(beanType);
        }).map((v0) -> {
            return v0.getKey();
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Class<?> extractType(ResolvableType type, TypeExtractor extractor) {
        if (type != null) {
            return extractor.getBeanType(type);
        }
        return null;
    }

    public Set<String> getNamesForAnnotation(Class<? extends Annotation> annotation) {
        updateTypesIfNecessary();
        return (Set) this.beanTypes.entrySet().stream().filter(entry -> {
            return (entry.getValue() == null || AnnotationUtils.findAnnotation(((ResolvableType) entry.getValue()).resolve(), (Class<Annotation>) annotation) == null) ? false : true;
        }).map((v0) -> {
            return v0.getKey();
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override // org.springframework.beans.factory.SmartInitializingSingleton
    public void afterSingletonsInstantiated() {
        this.beanTypes.clear();
        this.beanDefinitions.clear();
    }

    private void updateTypesIfNecessary() {
        this.beanFactory.getBeanNamesIterator().forEachRemaining(this::updateTypesIfNecessary);
    }

    private void updateTypesIfNecessary(String name) {
        if (!this.beanTypes.containsKey(name)) {
            addBeanType(name);
        } else {
            updateBeanType(name);
        }
    }

    private void addBeanType(String name) {
        if (this.beanFactory.containsSingleton(name)) {
            this.beanTypes.put(name, getType(name, null));
        } else if (!this.beanFactory.isAlias(name)) {
            addBeanTypeForNonAliasDefinition(name);
        }
    }

    private void addBeanTypeForNonAliasDefinition(String name) {
        RootBeanDefinition definition = getBeanDefinition(name);
        if (definition != null) {
            addBeanTypeForNonAliasDefinition(name, definition);
        }
    }

    private void updateBeanType(String name) {
        RootBeanDefinition definition;
        RootBeanDefinition previous;
        if (!this.beanFactory.isAlias(name) && !this.beanFactory.containsSingleton(name) && (definition = getBeanDefinition(name)) != null && (previous = this.beanDefinitions.put(name, definition)) != null && !definition.equals(previous)) {
            addBeanTypeForNonAliasDefinition(name, definition);
        }
    }

    private RootBeanDefinition getBeanDefinition(String name) {
        try {
            return (RootBeanDefinition) this.beanFactory.getMergedBeanDefinition(name);
        } catch (BeanDefinitionStoreException ex) {
            logIgnoredError("unresolvable metadata in bean definition", name, ex);
            return null;
        }
    }

    private void addBeanTypeForNonAliasDefinition(String name, RootBeanDefinition definition) {
        try {
            if (!definition.isAbstract() && !requiresEagerInit(definition.getFactoryBeanName())) {
                ResolvableType factoryMethodReturnType = getFactoryMethodReturnType(definition);
                String factoryBeanName = BeanFactory.FACTORY_BEAN_PREFIX + name;
                if (this.beanFactory.isFactoryBean(factoryBeanName)) {
                    ResolvableType factoryBeanGeneric = getFactoryBeanGeneric(this.beanFactory, definition, factoryMethodReturnType);
                    this.beanTypes.put(name, factoryBeanGeneric);
                    this.beanTypes.put(factoryBeanName, getType(factoryBeanName, factoryMethodReturnType));
                } else {
                    this.beanTypes.put(name, getType(name, factoryMethodReturnType));
                }
            }
            this.beanDefinitions.put(name, definition);
        } catch (CannotLoadBeanClassException ex) {
            logIgnoredError("bean class loading failure for bean", name, ex);
        }
    }

    private boolean requiresEagerInit(String factoryBeanName) {
        return (factoryBeanName == null || !this.beanFactory.isFactoryBean(factoryBeanName) || this.beanFactory.containsSingleton(factoryBeanName)) ? false : true;
    }

    private ResolvableType getFactoryMethodReturnType(BeanDefinition definition) {
        try {
            if (StringUtils.hasLength(definition.getFactoryBeanName()) && StringUtils.hasLength(definition.getFactoryMethodName())) {
                Method method = getFactoryMethod(this.beanFactory, definition);
                ResolvableType type = method != null ? ResolvableType.forMethodReturnType(method) : null;
                return type;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private Method getFactoryMethod(ConfigurableListableBeanFactory beanFactory, BeanDefinition definition) throws Exception {
        if (definition instanceof AnnotatedBeanDefinition) {
            MethodMetadata factoryMethodMetadata = ((AnnotatedBeanDefinition) definition).getFactoryMethodMetadata();
            if (factoryMethodMetadata instanceof StandardMethodMetadata) {
                return ((StandardMethodMetadata) factoryMethodMetadata).getIntrospectedMethod();
            }
        }
        BeanDefinition factoryDefinition = beanFactory.getBeanDefinition(definition.getFactoryBeanName());
        Class<?> factoryClass = ClassUtils.forName(factoryDefinition.getBeanClassName(), beanFactory.getBeanClassLoader());
        return getFactoryMethod(definition, factoryClass);
    }

    private Method getFactoryMethod(BeanDefinition definition, Class<?> factoryClass) {
        Method[] candidateFactoryMethods;
        Method uniqueMethod = null;
        for (Method candidate : getCandidateFactoryMethods(definition, factoryClass)) {
            if (candidate.getName().equals(definition.getFactoryMethodName())) {
                if (uniqueMethod == null) {
                    uniqueMethod = candidate;
                } else if (!hasMatchingParameterTypes(candidate, uniqueMethod)) {
                    return null;
                }
            }
        }
        return uniqueMethod;
    }

    private Method[] getCandidateFactoryMethods(BeanDefinition definition, Class<?> factoryClass) {
        if (shouldConsiderNonPublicMethods(definition)) {
            return ReflectionUtils.getAllDeclaredMethods(factoryClass);
        }
        return factoryClass.getMethods();
    }

    private boolean shouldConsiderNonPublicMethods(BeanDefinition definition) {
        return (definition instanceof AbstractBeanDefinition) && ((AbstractBeanDefinition) definition).isNonPublicAccessAllowed();
    }

    private boolean hasMatchingParameterTypes(Method candidate, Method current) {
        return Arrays.equals(candidate.getParameterTypes(), current.getParameterTypes());
    }

    private void logIgnoredError(String message, String name, Exception ex) {
        if (logger.isDebugEnabled()) {
            logger.debug("Ignoring " + message + " '" + name + "'", ex);
        }
    }

    private ResolvableType getFactoryBeanGeneric(ConfigurableListableBeanFactory beanFactory, BeanDefinition definition, ResolvableType factoryMethodReturnType) {
        try {
            if (factoryMethodReturnType != null) {
                return getFactoryBeanType(definition, factoryMethodReturnType);
            }
            if (StringUtils.hasLength(definition.getBeanClassName())) {
                return getDirectFactoryBeanGeneric(beanFactory, definition);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private ResolvableType getDirectFactoryBeanGeneric(ConfigurableListableBeanFactory beanFactory, BeanDefinition definition) throws ClassNotFoundException, LinkageError {
        Class<?> factoryBeanClass = ClassUtils.forName(definition.getBeanClassName(), beanFactory.getBeanClassLoader());
        return getFactoryBeanType(definition, ResolvableType.forClass(factoryBeanClass));
    }

    private ResolvableType getFactoryBeanType(BeanDefinition definition, ResolvableType type) throws ClassNotFoundException, LinkageError {
        ResolvableType generic = type.as(FactoryBean.class).getGeneric(new int[0]);
        if ((generic == null || generic.resolve().equals(Object.class)) && definition.hasAttribute("factoryBeanObjectType")) {
            generic = getTypeFromAttribute(definition.getAttribute("factoryBeanObjectType"));
        }
        return generic;
    }

    private ResolvableType getTypeFromAttribute(Object attribute) throws ClassNotFoundException, LinkageError {
        if (attribute instanceof Class) {
            return ResolvableType.forClass((Class) attribute);
        }
        if (attribute instanceof String) {
            return ResolvableType.forClass(ClassUtils.forName((String) attribute, null));
        }
        return null;
    }

    private ResolvableType getType(String name, ResolvableType factoryMethodReturnType) {
        if (factoryMethodReturnType != null && !factoryMethodReturnType.resolve(Object.class).equals(Object.class)) {
            return factoryMethodReturnType;
        }
        Class<?> type = this.beanFactory.getType(name);
        if (type != null) {
            return ResolvableType.forClass(type);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BeanTypeRegistry get(ListableBeanFactory beanFactory) {
        Assert.isInstanceOf(DefaultListableBeanFactory.class, beanFactory);
        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) beanFactory;
        Assert.isTrue(listableBeanFactory.isAllowEagerClassLoading(), "Bean factory must allow eager class loading");
        if (!listableBeanFactory.containsLocalBean(BEAN_NAME)) {
            BeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(BeanTypeRegistry.class, () -> {
                return new BeanTypeRegistry((DefaultListableBeanFactory) beanFactory);
            }).getBeanDefinition();
            listableBeanFactory.registerBeanDefinition(BEAN_NAME, definition);
        }
        return (BeanTypeRegistry) listableBeanFactory.getBean(BEAN_NAME, BeanTypeRegistry.class);
    }
}