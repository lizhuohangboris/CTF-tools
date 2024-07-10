package org.springframework.beans.factory.support;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanIsNotAFactoryException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.core.OrderComparator;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/StaticListableBeanFactory.class */
public class StaticListableBeanFactory implements ListableBeanFactory {
    private final Map<String, Object> beans;

    public StaticListableBeanFactory() {
        this.beans = new LinkedHashMap();
    }

    public StaticListableBeanFactory(Map<String, Object> beans) {
        Assert.notNull(beans, "Beans Map must not be null");
        this.beans = beans;
    }

    public void addBean(String name, Object bean) {
        this.beans.put(name, bean);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Object getBean(String name) throws BeansException {
        String beanName = BeanFactoryUtils.transformedBeanName(name);
        Object bean = this.beans.get(beanName);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(beanName, "Defined beans are [" + StringUtils.collectionToCommaDelimitedString(this.beans.keySet()) + "]");
        }
        if (BeanFactoryUtils.isFactoryDereference(name) && !(bean instanceof FactoryBean)) {
            throw new BeanIsNotAFactoryException(beanName, bean.getClass());
        }
        if ((bean instanceof FactoryBean) && !BeanFactoryUtils.isFactoryDereference(name)) {
            try {
                Object exposedObject = ((FactoryBean) bean).getObject();
                if (exposedObject == null) {
                    throw new BeanCreationException(beanName, "FactoryBean exposed null object");
                }
                return exposedObject;
            } catch (Exception ex) {
                throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
            }
        }
        return bean;
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(String name, @Nullable Class<T> requiredType) throws BeansException {
        T t = (T) getBean(name);
        if (requiredType != null && !requiredType.isInstance(t)) {
            throw new BeanNotOfRequiredTypeException(name, requiredType, t.getClass());
        }
        return t;
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Object getBean(String name, Object... args) throws BeansException {
        if (!ObjectUtils.isEmpty(args)) {
            throw new UnsupportedOperationException("StaticListableBeanFactory does not support explicit bean creation arguments");
        }
        return getBean(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        String[] beanNames = getBeanNamesForType((Class<?>) requiredType);
        if (beanNames.length == 1) {
            return (T) getBean(beanNames[0], requiredType);
        }
        if (beanNames.length > 1) {
            throw new NoUniqueBeanDefinitionException((Class<?>) requiredType, beanNames);
        }
        throw new NoSuchBeanDefinitionException((Class<?>) requiredType);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        if (!ObjectUtils.isEmpty(args)) {
            throw new UnsupportedOperationException("StaticListableBeanFactory does not support explicit bean creation arguments");
        }
        return (T) getBean(requiredType);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) throws BeansException {
        return getBeanProvider(ResolvableType.forRawClass(requiredType));
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType) {
        return new ObjectProvider<T>() { // from class: org.springframework.beans.factory.support.StaticListableBeanFactory.1
            @Override // org.springframework.beans.factory.ObjectFactory
            public T getObject() throws BeansException {
                String[] beanNames = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNames.length == 1) {
                    return (T) StaticListableBeanFactory.this.getBean(beanNames[0], requiredType);
                }
                if (beanNames.length > 1) {
                    throw new NoUniqueBeanDefinitionException(requiredType, beanNames);
                }
                throw new NoSuchBeanDefinitionException(requiredType);
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public T getObject(Object... args) throws BeansException {
                String[] beanNames = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNames.length == 1) {
                    return (T) StaticListableBeanFactory.this.getBean(beanNames[0], args);
                }
                if (beanNames.length > 1) {
                    throw new NoUniqueBeanDefinitionException(requiredType, beanNames);
                }
                throw new NoSuchBeanDefinitionException(requiredType);
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            @Nullable
            public T getIfAvailable() throws BeansException {
                String[] beanNames = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNames.length == 1) {
                    return (T) StaticListableBeanFactory.this.getBean(beanNames[0]);
                }
                if (beanNames.length > 1) {
                    throw new NoUniqueBeanDefinitionException(requiredType, beanNames);
                }
                return null;
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            @Nullable
            public T getIfUnique() throws BeansException {
                String[] beanNames = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNames.length == 1) {
                    return (T) StaticListableBeanFactory.this.getBean(beanNames[0]);
                }
                return null;
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public Stream<T> stream() {
                return Arrays.stream(StaticListableBeanFactory.this.getBeanNamesForType(requiredType)).map(name -> {
                    return StaticListableBeanFactory.this.getBean(name);
                });
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public Stream<T> orderedStream() {
                return stream().sorted(OrderComparator.INSTANCE);
            }
        };
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean containsBean(String name) {
        return this.beans.containsKey(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        Object bean = getBean(name);
        return (bean instanceof FactoryBean) && ((FactoryBean) bean).isSingleton();
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        Object bean = getBean(name);
        return ((bean instanceof SmartFactoryBean) && ((SmartFactoryBean) bean).isPrototype()) || ((bean instanceof FactoryBean) && !((FactoryBean) bean).isSingleton());
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        Class<?> type = getType(name);
        return type != null && typeToMatch.isAssignableFrom(type);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isTypeMatch(String name, @Nullable Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        Class<?> type = getType(name);
        return typeToMatch == null || (type != null && typeToMatch.isAssignableFrom(type));
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        String beanName = BeanFactoryUtils.transformedBeanName(name);
        Object bean = this.beans.get(beanName);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(beanName, "Defined beans are [" + StringUtils.collectionToCommaDelimitedString(this.beans.keySet()) + "]");
        }
        if ((bean instanceof FactoryBean) && !BeanFactoryUtils.isFactoryDereference(name)) {
            return ((FactoryBean) bean).getObjectType();
        }
        return bean.getClass();
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public String[] getAliases(String name) {
        return new String[0];
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public boolean containsBeanDefinition(String name) {
        return this.beans.containsKey(name);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public int getBeanDefinitionCount() {
        return this.beans.size();
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public String[] getBeanDefinitionNames() {
        return StringUtils.toStringArray(this.beans.keySet());
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable ResolvableType type) {
        Class<?> resolved;
        boolean isFactoryType = false;
        if (type != null && (resolved = type.resolve()) != null && FactoryBean.class.isAssignableFrom(resolved)) {
            isFactoryType = true;
        }
        List<String> matches = new ArrayList<>();
        for (Map.Entry<String, Object> entry : this.beans.entrySet()) {
            String name = entry.getKey();
            Object beanInstance = entry.getValue();
            if ((beanInstance instanceof FactoryBean) && !isFactoryType) {
                Class<?> objectType = ((FactoryBean) beanInstance).getObjectType();
                if (objectType != null && (type == null || type.isAssignableFrom(objectType))) {
                    matches.add(name);
                }
            } else if (type == null || type.isInstance(beanInstance)) {
                matches.add(name);
            }
        }
        return StringUtils.toStringArray(matches);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable Class<?> type) {
        return getBeanNamesForType(ResolvableType.forClass(type));
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return getBeanNamesForType(ResolvableType.forClass(type));
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
        return getBeansOfType(type, true, true);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        boolean isFactoryType = type != null && FactoryBean.class.isAssignableFrom(type);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Map.Entry<String, Object> entry : this.beans.entrySet()) {
            String beanName = entry.getKey();
            Object beanInstance = entry.getValue();
            if ((beanInstance instanceof FactoryBean) && !isFactoryType) {
                FactoryBean<?> factory = (FactoryBean) beanInstance;
                Class<?> objectType = factory.getObjectType();
                if (includeNonSingletons || factory.isSingleton()) {
                    if (objectType != null && (type == null || type.isAssignableFrom(objectType))) {
                        linkedHashMap.put(beanName, getBean(beanName, type));
                    }
                }
            } else if (type == null || type.isInstance(beanInstance)) {
                if (isFactoryType) {
                    beanName = BeanFactory.FACTORY_BEAN_PREFIX + beanName;
                }
                linkedHashMap.put(beanName, beanInstance);
            }
        }
        return linkedHashMap;
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        List<String> results = new ArrayList<>();
        for (String beanName : this.beans.keySet()) {
            if (findAnnotationOnBean(beanName, annotationType) != null) {
                results.add(beanName);
            }
        }
        return StringUtils.toStringArray(results);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        Map<String, Object> results = new LinkedHashMap<>();
        for (String beanName : this.beans.keySet()) {
            if (findAnnotationOnBean(beanName, annotationType) != null) {
                results.put(beanName, getBean(beanName));
            }
        }
        return results;
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
        Class<?> beanType = getType(beanName);
        if (beanType != null) {
            return (A) AnnotationUtils.findAnnotation(beanType, (Class<Annotation>) annotationType);
        }
        return null;
    }
}