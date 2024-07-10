package org.springframework.beans.factory.support;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/BeanDefinitionValueResolver.class */
public class BeanDefinitionValueResolver {
    private final AbstractBeanFactory beanFactory;
    private final String beanName;
    private final BeanDefinition beanDefinition;
    private final TypeConverter typeConverter;

    public BeanDefinitionValueResolver(AbstractBeanFactory beanFactory, String beanName, BeanDefinition beanDefinition, TypeConverter typeConverter) {
        this.beanFactory = beanFactory;
        this.beanName = beanName;
        this.beanDefinition = beanDefinition;
        this.typeConverter = typeConverter;
    }

    @Nullable
    public Object resolveValueIfNecessary(Object argName, @Nullable Object value) {
        if (value instanceof RuntimeBeanReference) {
            RuntimeBeanReference ref = (RuntimeBeanReference) value;
            return resolveReference(argName, ref);
        } else if (value instanceof RuntimeBeanNameReference) {
            String refName = String.valueOf(doEvaluate(((RuntimeBeanNameReference) value).getBeanName()));
            if (!this.beanFactory.containsBean(refName)) {
                throw new BeanDefinitionStoreException("Invalid bean name '" + refName + "' in bean reference for " + argName);
            }
            return refName;
        } else if (value instanceof BeanDefinitionHolder) {
            BeanDefinitionHolder bdHolder = (BeanDefinitionHolder) value;
            return resolveInnerBean(argName, bdHolder.getBeanName(), bdHolder.getBeanDefinition());
        } else if (value instanceof BeanDefinition) {
            BeanDefinition bd = (BeanDefinition) value;
            String innerBeanName = "(inner bean)#" + ObjectUtils.getIdentityHexString(bd);
            return resolveInnerBean(argName, innerBeanName, bd);
        } else if (value instanceof ManagedArray) {
            ManagedArray array = (ManagedArray) value;
            Class<?> elementType = array.resolvedElementType;
            if (elementType == null) {
                String elementTypeName = array.getElementTypeName();
                if (StringUtils.hasText(elementTypeName)) {
                    try {
                        elementType = ClassUtils.forName(elementTypeName, this.beanFactory.getBeanClassLoader());
                        array.resolvedElementType = elementType;
                    } catch (Throwable ex) {
                        throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Error resolving array type for " + argName, ex);
                    }
                } else {
                    elementType = Object.class;
                }
            }
            return resolveManagedArray(argName, (List) value, elementType);
        } else if (value instanceof ManagedList) {
            return resolveManagedList(argName, (List) value);
        } else {
            if (value instanceof ManagedSet) {
                return resolveManagedSet(argName, (Set) value);
            }
            if (value instanceof ManagedMap) {
                return resolveManagedMap(argName, (Map) value);
            }
            if (value instanceof ManagedProperties) {
                Properties original = (Properties) value;
                Properties copy = new Properties();
                original.forEach(propKey, propValue -> {
                    if (propKey instanceof TypedStringValue) {
                        propKey = evaluate((TypedStringValue) propKey);
                    }
                    if (propValue instanceof TypedStringValue) {
                        propValue = evaluate((TypedStringValue) propValue);
                    }
                    if (propKey == null || propValue == null) {
                        throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Error converting Properties key/value pair for " + argName + ": resolved to null");
                    }
                    copy.put(propKey, propValue);
                });
                return copy;
            } else if (value instanceof TypedStringValue) {
                TypedStringValue typedStringValue = (TypedStringValue) value;
                Object valueObject = evaluate(typedStringValue);
                try {
                    Class<?> resolvedTargetType = resolveTargetType(typedStringValue);
                    if (resolvedTargetType != null) {
                        return this.typeConverter.convertIfNecessary(valueObject, resolvedTargetType);
                    }
                    return valueObject;
                } catch (Throwable ex2) {
                    throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Error converting typed String value for " + argName, ex2);
                }
            } else if (value instanceof NullBean) {
                return null;
            } else {
                return evaluate(value);
            }
        }
    }

    @Nullable
    protected Object evaluate(TypedStringValue value) {
        Object result = doEvaluate(value.getValue());
        if (!ObjectUtils.nullSafeEquals(result, value.getValue())) {
            value.setDynamic();
        }
        return result;
    }

    @Nullable
    protected Object evaluate(@Nullable Object value) {
        if (value instanceof String) {
            return doEvaluate((String) value);
        }
        if (value instanceof String[]) {
            String[] values = (String[]) value;
            boolean actuallyResolved = false;
            Object[] resolvedValues = new Object[values.length];
            for (int i = 0; i < values.length; i++) {
                String originalValue = values[i];
                Object resolvedValue = doEvaluate(originalValue);
                if (resolvedValue != originalValue) {
                    actuallyResolved = true;
                }
                resolvedValues[i] = resolvedValue;
            }
            return actuallyResolved ? resolvedValues : values;
        }
        return value;
    }

    @Nullable
    private Object doEvaluate(@Nullable String value) {
        return this.beanFactory.evaluateBeanDefinitionString(value, this.beanDefinition);
    }

    @Nullable
    protected Class<?> resolveTargetType(TypedStringValue value) throws ClassNotFoundException {
        if (value.hasTargetType()) {
            return value.getTargetType();
        }
        return value.resolveTargetType(this.beanFactory.getBeanClassLoader());
    }

    @Nullable
    private Object resolveInnerBean(Object argName, String innerBeanName, BeanDefinition innerBd) {
        RootBeanDefinition mbd = null;
        try {
            mbd = this.beanFactory.getMergedBeanDefinition(innerBeanName, innerBd, this.beanDefinition);
            String actualInnerBeanName = innerBeanName;
            if (mbd.isSingleton()) {
                actualInnerBeanName = adaptInnerBeanName(innerBeanName);
            }
            this.beanFactory.registerContainedBean(actualInnerBeanName, this.beanName);
            String[] dependsOn = mbd.getDependsOn();
            if (dependsOn != null) {
                for (String dependsOnBean : dependsOn) {
                    this.beanFactory.registerDependentBean(dependsOnBean, actualInnerBeanName);
                    this.beanFactory.getBean(dependsOnBean);
                }
            }
            Object innerBean = this.beanFactory.createBean(actualInnerBeanName, mbd, null);
            if (innerBean instanceof FactoryBean) {
                boolean synthetic = mbd.isSynthetic();
                innerBean = this.beanFactory.getObjectFromFactoryBean((FactoryBean) innerBean, actualInnerBeanName, !synthetic);
            }
            if (innerBean instanceof NullBean) {
                innerBean = null;
            }
            return innerBean;
        } catch (BeansException ex) {
            throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Cannot create inner bean '" + innerBeanName + "' " + ((mbd == null || mbd.getBeanClassName() == null) ? "" : "of type [" + mbd.getBeanClassName() + "] ") + "while setting " + argName, ex);
        }
    }

    private String adaptInnerBeanName(String innerBeanName) {
        String actualInnerBeanName = innerBeanName;
        int counter = 0;
        while (this.beanFactory.isBeanNameInUse(actualInnerBeanName)) {
            counter++;
            actualInnerBeanName = innerBeanName + "#" + counter;
        }
        return actualInnerBeanName;
    }

    @Nullable
    private Object resolveReference(Object argName, RuntimeBeanReference ref) {
        Object bean;
        try {
            String refName = String.valueOf(doEvaluate(ref.getBeanName()));
            if (ref.isToParent()) {
                if (this.beanFactory.getParentBeanFactory() == null) {
                    throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Can't resolve reference to bean '" + refName + "' in parent factory: no parent factory available");
                }
                bean = this.beanFactory.getParentBeanFactory().getBean(refName);
            } else {
                bean = this.beanFactory.getBean(refName);
                this.beanFactory.registerDependentBean(refName, this.beanName);
            }
            if (bean instanceof NullBean) {
                bean = null;
            }
            return bean;
        } catch (BeansException ex) {
            throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Cannot resolve reference to bean '" + ref.getBeanName() + "' while setting " + argName, ex);
        }
    }

    private Object resolveManagedArray(Object argName, List<?> ml, Class<?> elementType) {
        Object resolved = Array.newInstance(elementType, ml.size());
        for (int i = 0; i < ml.size(); i++) {
            Array.set(resolved, i, resolveValueIfNecessary(new KeyedArgName(argName, Integer.valueOf(i)), ml.get(i)));
        }
        return resolved;
    }

    private List<?> resolveManagedList(Object argName, List<?> ml) {
        List<Object> resolved = new ArrayList<>(ml.size());
        for (int i = 0; i < ml.size(); i++) {
            resolved.add(resolveValueIfNecessary(new KeyedArgName(argName, Integer.valueOf(i)), ml.get(i)));
        }
        return resolved;
    }

    private Set<?> resolveManagedSet(Object argName, Set<?> ms) {
        Set<Object> resolved = new LinkedHashSet<>(ms.size());
        int i = 0;
        for (Object m : ms) {
            resolved.add(resolveValueIfNecessary(new KeyedArgName(argName, Integer.valueOf(i)), m));
            i++;
        }
        return resolved;
    }

    private Map<?, ?> resolveManagedMap(Object argName, Map<?, ?> mm) {
        Map<Object, Object> resolved = new LinkedHashMap<>(mm.size());
        mm.forEach(key, value -> {
            Object resolvedKey = resolveValueIfNecessary(argName, key);
            Object resolvedValue = resolveValueIfNecessary(new KeyedArgName(argName, key), value);
            resolved.put(resolvedKey, resolvedValue);
        });
        return resolved;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/BeanDefinitionValueResolver$KeyedArgName.class */
    public static class KeyedArgName {
        private final Object argName;
        private final Object key;

        public KeyedArgName(Object argName, Object key) {
            this.argName = argName;
            this.key = key;
        }

        public String toString() {
            return this.argName + " with key " + PropertyAccessor.PROPERTY_KEY_PREFIX + this.key + "]";
        }
    }
}