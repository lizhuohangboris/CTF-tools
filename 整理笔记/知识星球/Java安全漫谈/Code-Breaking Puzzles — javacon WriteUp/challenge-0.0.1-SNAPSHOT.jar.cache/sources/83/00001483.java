package org.springframework.beans.factory.support;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/FactoryBeanRegistrySupport.class */
public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry {
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap(16);

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Class<?> getTypeForFactoryBean(FactoryBean<?> factoryBean) {
        try {
            if (System.getSecurityManager() != null) {
                factoryBean.getClass();
                return (Class) AccessController.doPrivileged(this::getObjectType, getAccessControlContext());
            }
            return factoryBean.getObjectType();
        } catch (Throwable ex) {
            this.logger.info("FactoryBean threw exception from getObjectType, despite the contract saying that it should return null if the type of its object cannot be determined yet", ex);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Object getCachedObjectForFactoryBean(String beanName) {
        return this.factoryBeanObjectCache.get(beanName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
        if (factory.isSingleton() && containsSingleton(beanName)) {
            synchronized (getSingletonMutex()) {
                Object object = this.factoryBeanObjectCache.get(beanName);
                if (object == null) {
                    object = doGetObjectFromFactoryBean(factory, beanName);
                    Object alreadyThere = this.factoryBeanObjectCache.get(beanName);
                    if (alreadyThere != null) {
                        object = alreadyThere;
                    } else {
                        if (shouldPostProcess) {
                            if (isSingletonCurrentlyInCreation(beanName)) {
                                return object;
                            }
                            beforeSingletonCreation(beanName);
                            try {
                                object = postProcessObjectFromFactoryBean(object, beanName);
                                afterSingletonCreation(beanName);
                            } catch (Throwable ex) {
                                throw new BeanCreationException(beanName, "Post-processing of FactoryBean's singleton object failed", ex);
                            }
                        }
                        if (containsSingleton(beanName)) {
                            this.factoryBeanObjectCache.put(beanName, object);
                        }
                    }
                }
                return object;
            }
        }
        Object object2 = doGetObjectFromFactoryBean(factory, beanName);
        if (shouldPostProcess) {
            try {
                object2 = postProcessObjectFromFactoryBean(object2, beanName);
            } catch (Throwable ex2) {
                throw new BeanCreationException(beanName, "Post-processing of FactoryBean's object failed", ex2);
            }
        }
        return object2;
    }

    private Object doGetObjectFromFactoryBean(FactoryBean<?> factory, String beanName) throws BeanCreationException {
        Object object;
        try {
            if (System.getSecurityManager() != null) {
                AccessControlContext acc = getAccessControlContext();
                try {
                    factory.getClass();
                    object = AccessController.doPrivileged(this::getObject, acc);
                } catch (PrivilegedActionException pae) {
                    throw pae.getException();
                }
            } else {
                object = factory.getObject();
            }
            if (object == null) {
                if (isSingletonCurrentlyInCreation(beanName)) {
                    throw new BeanCurrentlyInCreationException(beanName, "FactoryBean which is currently in creation returned null from getObject");
                }
                object = new NullBean();
            }
            return object;
        } catch (FactoryBeanNotInitializedException ex) {
            throw new BeanCurrentlyInCreationException(beanName, ex.toString());
        } catch (Throwable ex2) {
            throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex2);
        }
    }

    protected Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
        return object;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FactoryBean<?> getFactoryBean(String beanName, Object beanInstance) throws BeansException {
        if (!(beanInstance instanceof FactoryBean)) {
            throw new BeanCreationException(beanName, "Bean instance of type [" + beanInstance.getClass() + "] is not a FactoryBean");
        }
        return (FactoryBean) beanInstance;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
    public void removeSingleton(String beanName) {
        synchronized (getSingletonMutex()) {
            super.removeSingleton(beanName);
            this.factoryBeanObjectCache.remove(beanName);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
    public void clearSingletonCache() {
        synchronized (getSingletonMutex()) {
            super.clearSingletonCache();
            this.factoryBeanObjectCache.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AccessControlContext getAccessControlContext() {
        return AccessController.getContext();
    }
}