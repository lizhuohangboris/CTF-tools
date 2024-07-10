package org.springframework.jndi.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ResolvableType;
import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.jndi.TypeMismatchNamingException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jndi/support/SimpleJndiBeanFactory.class */
public class SimpleJndiBeanFactory extends JndiLocatorSupport implements BeanFactory {
    private final Set<String> shareableResources = new HashSet();
    private final Map<String, Object> singletonObjects = new HashMap();
    private final Map<String, Class<?>> resourceTypes = new HashMap();

    public SimpleJndiBeanFactory() {
        setResourceRef(true);
    }

    public void addShareableResource(String shareableResource) {
        this.shareableResources.add(shareableResource);
    }

    public void setShareableResources(String... shareableResources) {
        Collections.addAll(this.shareableResources, shareableResources);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Object getBean(String name) throws BeansException {
        return getBean(name, Object.class);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        try {
            if (isSingleton(name)) {
                return (T) doGetSingleton(name, requiredType);
            }
            return (T) lookup(name, requiredType);
        } catch (NameNotFoundException e) {
            throw new NoSuchBeanDefinitionException(name, "not found in JNDI environment");
        } catch (TypeMismatchNamingException ex) {
            throw new BeanNotOfRequiredTypeException(name, ex.getRequiredType(), ex.getActualType());
        } catch (NamingException ex2) {
            throw new BeanDefinitionStoreException("JNDI environment", name, "JNDI lookup failed", ex2);
        }
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Object getBean(String name, @Nullable Object... args) throws BeansException {
        if (args != null) {
            throw new UnsupportedOperationException("SimpleJndiBeanFactory does not support explicit bean creation arguments");
        }
        return getBean(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return (T) getBean(requiredType.getSimpleName(), requiredType);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> requiredType, @Nullable Object... args) throws BeansException {
        if (args != null) {
            throw new UnsupportedOperationException("SimpleJndiBeanFactory does not support explicit bean creation arguments");
        }
        return (T) getBean(requiredType);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(final Class<T> requiredType) {
        return new ObjectProvider<T>() { // from class: org.springframework.jndi.support.SimpleJndiBeanFactory.1
            @Override // org.springframework.beans.factory.ObjectFactory
            public T getObject() throws BeansException {
                return (T) SimpleJndiBeanFactory.this.getBean(requiredType);
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public T getObject(Object... args) throws BeansException {
                return (T) SimpleJndiBeanFactory.this.getBean(requiredType, args);
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            @Nullable
            public T getIfAvailable() throws BeansException {
                try {
                    return (T) SimpleJndiBeanFactory.this.getBean(requiredType);
                } catch (NoUniqueBeanDefinitionException ex) {
                    throw ex;
                } catch (NoSuchBeanDefinitionException e) {
                    return null;
                }
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            @Nullable
            public T getIfUnique() throws BeansException {
                try {
                    return (T) SimpleJndiBeanFactory.this.getBean(requiredType);
                } catch (NoSuchBeanDefinitionException e) {
                    return null;
                }
            }
        };
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        throw new UnsupportedOperationException("SimpleJndiBeanFactory does not support resolution by ResolvableType");
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean containsBean(String name) {
        if (this.singletonObjects.containsKey(name) || this.resourceTypes.containsKey(name)) {
            return true;
        }
        try {
            doGetType(name);
            return true;
        } catch (NamingException e) {
            return false;
        }
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return this.shareableResources.contains(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return !this.shareableResources.contains(name);
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
    @Nullable
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        try {
            return doGetType(name);
        } catch (NamingException e) {
            return null;
        } catch (NameNotFoundException e2) {
            throw new NoSuchBeanDefinitionException(name, "not found in JNDI environment");
        }
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public String[] getAliases(String name) {
        return new String[0];
    }

    private <T> T doGetSingleton(String name, @Nullable Class<T> requiredType) throws NamingException {
        synchronized (this.singletonObjects) {
            if (this.singletonObjects.containsKey(name)) {
                T t = (T) this.singletonObjects.get(name);
                if (requiredType != null && !requiredType.isInstance(t)) {
                    throw new TypeMismatchNamingException(convertJndiName(name), requiredType, t != null ? t.getClass() : null);
                }
                return t;
            }
            T jndiObject = (T) lookup(name, requiredType);
            this.singletonObjects.put(name, jndiObject);
            return jndiObject;
        }
    }

    private Class<?> doGetType(String name) throws NamingException {
        if (isSingleton(name)) {
            return doGetSingleton(name, null).getClass();
        }
        synchronized (this.resourceTypes) {
            if (this.resourceTypes.containsKey(name)) {
                return this.resourceTypes.get(name);
            }
            Class<?> type = lookup(name, null).getClass();
            this.resourceTypes.put(name, type);
            return type;
        }
    }
}