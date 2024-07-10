package org.springframework.aop.framework.autoproxy.target;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.autoproxy.TargetSourceCreator;
import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/autoproxy/target/AbstractBeanFactoryBasedTargetSourceCreator.class */
public abstract class AbstractBeanFactoryBasedTargetSourceCreator implements TargetSourceCreator, BeanFactoryAware, DisposableBean {
    private ConfigurableBeanFactory beanFactory;
    protected final Log logger = LogFactory.getLog(getClass());
    private final Map<String, DefaultListableBeanFactory> internalBeanFactories = new HashMap();

    @Nullable
    protected abstract AbstractBeanFactoryBasedTargetSource createBeanFactoryBasedTargetSource(Class<?> cls, String str);

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public final void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalStateException("Cannot do auto-TargetSource creation with a BeanFactory that doesn't implement ConfigurableBeanFactory: " + beanFactory.getClass());
        }
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Override // org.springframework.aop.framework.autoproxy.TargetSourceCreator
    @Nullable
    public final TargetSource getTargetSource(Class<?> beanClass, String beanName) {
        AbstractBeanFactoryBasedTargetSource targetSource = createBeanFactoryBasedTargetSource(beanClass, beanName);
        if (targetSource == null) {
            return null;
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Configuring AbstractBeanFactoryBasedTargetSource: " + targetSource);
        }
        DefaultListableBeanFactory internalBeanFactory = getInternalBeanFactoryForBean(beanName);
        BeanDefinition bd = this.beanFactory.getMergedBeanDefinition(beanName);
        GenericBeanDefinition bdCopy = new GenericBeanDefinition(bd);
        if (isPrototypeBased()) {
            bdCopy.setScope("prototype");
        }
        internalBeanFactory.registerBeanDefinition(beanName, bdCopy);
        targetSource.setTargetBeanName(beanName);
        targetSource.setBeanFactory(internalBeanFactory);
        return targetSource;
    }

    protected DefaultListableBeanFactory getInternalBeanFactoryForBean(String beanName) {
        DefaultListableBeanFactory defaultListableBeanFactory;
        synchronized (this.internalBeanFactories) {
            DefaultListableBeanFactory internalBeanFactory = this.internalBeanFactories.get(beanName);
            if (internalBeanFactory == null) {
                internalBeanFactory = buildInternalBeanFactory(this.beanFactory);
                this.internalBeanFactories.put(beanName, internalBeanFactory);
            }
            defaultListableBeanFactory = internalBeanFactory;
        }
        return defaultListableBeanFactory;
    }

    protected DefaultListableBeanFactory buildInternalBeanFactory(ConfigurableBeanFactory containingFactory) {
        DefaultListableBeanFactory internalBeanFactory = new DefaultListableBeanFactory(containingFactory);
        internalBeanFactory.copyConfigurationFrom(containingFactory);
        internalBeanFactory.getBeanPostProcessors().removeIf(beanPostProcessor -> {
            return beanPostProcessor instanceof AopInfrastructureBean;
        });
        return internalBeanFactory;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        synchronized (this.internalBeanFactories) {
            for (DefaultListableBeanFactory bf : this.internalBeanFactories.values()) {
                bf.destroySingletons();
            }
        }
    }

    protected boolean isPrototypeBased() {
        return true;
    }
}