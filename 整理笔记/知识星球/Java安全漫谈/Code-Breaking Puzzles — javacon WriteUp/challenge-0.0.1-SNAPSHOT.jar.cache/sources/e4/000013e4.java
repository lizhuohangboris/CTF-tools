package org.springframework.beans.factory.config;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/AbstractFactoryBean.class */
public abstract class AbstractFactoryBean<T> implements FactoryBean<T>, BeanClassLoaderAware, BeanFactoryAware, InitializingBean, DisposableBean {
    @Nullable
    private BeanFactory beanFactory;
    @Nullable
    private T singletonInstance;
    @Nullable
    private T earlySingletonInstance;
    protected final Log logger = LogFactory.getLog(getClass());
    private boolean singleton = true;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private boolean initialized = false;

    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public abstract Class<?> getObjectType();

    protected abstract T createInstance() throws Exception;

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return this.singleton;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(@Nullable BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Nullable
    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public TypeConverter getBeanTypeConverter() {
        BeanFactory beanFactory = getBeanFactory();
        if (beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) beanFactory).getTypeConverter();
        }
        return new SimpleTypeConverter();
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        if (isSingleton()) {
            this.initialized = true;
            this.singletonInstance = createInstance();
            this.earlySingletonInstance = null;
        }
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public final T getObject() throws Exception {
        if (isSingleton()) {
            return this.initialized ? this.singletonInstance : getEarlySingletonInstance();
        }
        return createInstance();
    }

    private T getEarlySingletonInstance() throws Exception {
        Class<?>[] ifcs = getEarlySingletonInterfaces();
        if (ifcs == null) {
            throw new FactoryBeanNotInitializedException(getClass().getName() + " does not support circular references");
        }
        if (this.earlySingletonInstance == null) {
            this.earlySingletonInstance = (T) Proxy.newProxyInstance(this.beanClassLoader, ifcs, new EarlySingletonInvocationHandler());
        }
        return this.earlySingletonInstance;
    }

    @Nullable
    public T getSingletonInstance() throws IllegalStateException {
        Assert.state(this.initialized, "Singleton instance not initialized yet");
        return this.singletonInstance;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws Exception {
        if (isSingleton()) {
            destroyInstance(this.singletonInstance);
        }
    }

    @Nullable
    protected Class<?>[] getEarlySingletonInterfaces() {
        Class<?> type = getObjectType();
        if (type == null || !type.isInterface()) {
            return null;
        }
        return new Class[]{type};
    }

    protected void destroyInstance(@Nullable T instance) throws Exception {
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/AbstractFactoryBean$EarlySingletonInvocationHandler.class */
    public class EarlySingletonInvocationHandler implements InvocationHandler {
        private EarlySingletonInvocationHandler() {
            AbstractFactoryBean.this = r4;
        }

        @Override // java.lang.reflect.InvocationHandler
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (ReflectionUtils.isEqualsMethod(method)) {
                return Boolean.valueOf(proxy == args[0]);
            } else if (!ReflectionUtils.isHashCodeMethod(method)) {
                if (!AbstractFactoryBean.this.initialized && ReflectionUtils.isToStringMethod(method)) {
                    return "Early singleton proxy for interfaces " + ObjectUtils.nullSafeToString((Object[]) AbstractFactoryBean.this.getEarlySingletonInterfaces());
                }
                try {
                    return method.invoke(AbstractFactoryBean.this.getSingletonInstance(), args);
                } catch (InvocationTargetException ex) {
                    throw ex.getTargetException();
                }
            } else {
                return Integer.valueOf(System.identityHashCode(proxy));
            }
        }
    }
}