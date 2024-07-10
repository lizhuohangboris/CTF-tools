package org.springframework.beans.factory.config;

import java.lang.reflect.InvocationTargetException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/MethodInvokingBean.class */
public class MethodInvokingBean extends ArgumentConvertingMethodInvoker implements BeanClassLoaderAware, BeanFactoryAware, InitializingBean {
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    @Nullable
    private ConfigurableBeanFactory beanFactory;

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.util.MethodInvoker
    protected Class<?> resolveClassName(String className) throws ClassNotFoundException {
        return ClassUtils.forName(className, this.beanClassLoader);
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.beanFactory = (ConfigurableBeanFactory) beanFactory;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.support.ArgumentConvertingMethodInvoker
    public TypeConverter getDefaultTypeConverter() {
        if (this.beanFactory != null) {
            return this.beanFactory.getTypeConverter();
        }
        return super.getDefaultTypeConverter();
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        prepare();
        invokeWithTargetException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Object invokeWithTargetException() throws Exception {
        try {
            return invoke();
        } catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof Exception) {
                throw ((Exception) ex.getTargetException());
            }
            if (ex.getTargetException() instanceof Error) {
                throw ((Error) ex.getTargetException());
            }
            throw ex;
        }
    }
}