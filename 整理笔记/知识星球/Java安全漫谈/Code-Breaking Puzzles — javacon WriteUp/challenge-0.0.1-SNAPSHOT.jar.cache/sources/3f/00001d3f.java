package org.springframework.context.support;

import java.io.IOException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/AbstractRefreshableApplicationContext.class */
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {
    @Nullable
    private Boolean allowBeanDefinitionOverriding;
    @Nullable
    private Boolean allowCircularReferences;
    @Nullable
    private DefaultListableBeanFactory beanFactory;
    private final Object beanFactoryMonitor;

    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory defaultListableBeanFactory) throws BeansException, IOException;

    public AbstractRefreshableApplicationContext() {
        this.beanFactoryMonitor = new Object();
    }

    public AbstractRefreshableApplicationContext(@Nullable ApplicationContext parent) {
        super(parent);
        this.beanFactoryMonitor = new Object();
    }

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = Boolean.valueOf(allowBeanDefinitionOverriding);
    }

    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.allowCircularReferences = Boolean.valueOf(allowCircularReferences);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext
    protected final void refreshBeanFactory() throws BeansException {
        if (hasBeanFactory()) {
            destroyBeans();
            closeBeanFactory();
        }
        try {
            DefaultListableBeanFactory beanFactory = createBeanFactory();
            beanFactory.setSerializationId(getId());
            customizeBeanFactory(beanFactory);
            loadBeanDefinitions(beanFactory);
            synchronized (this.beanFactoryMonitor) {
                this.beanFactory = beanFactory;
            }
        } catch (IOException ex) {
            throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.AbstractApplicationContext
    public void cancelRefresh(BeansException ex) {
        synchronized (this.beanFactoryMonitor) {
            if (this.beanFactory != null) {
                this.beanFactory.setSerializationId(null);
            }
        }
        super.cancelRefresh(ex);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext
    protected final void closeBeanFactory() {
        synchronized (this.beanFactoryMonitor) {
            if (this.beanFactory != null) {
                this.beanFactory.setSerializationId(null);
                this.beanFactory = null;
            }
        }
    }

    protected final boolean hasBeanFactory() {
        boolean z;
        synchronized (this.beanFactoryMonitor) {
            z = this.beanFactory != null;
        }
        return z;
    }

    @Override // org.springframework.context.support.AbstractApplicationContext, org.springframework.context.ConfigurableApplicationContext
    public final ConfigurableListableBeanFactory getBeanFactory() {
        DefaultListableBeanFactory defaultListableBeanFactory;
        synchronized (this.beanFactoryMonitor) {
            if (this.beanFactory == null) {
                throw new IllegalStateException("BeanFactory not initialized or already closed - call 'refresh' before accessing beans via the ApplicationContext");
            }
            defaultListableBeanFactory = this.beanFactory;
        }
        return defaultListableBeanFactory;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.AbstractApplicationContext
    public void assertBeanFactoryActive() {
    }

    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory(getInternalParentBeanFactory());
    }

    protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
        if (this.allowBeanDefinitionOverriding != null) {
            beanFactory.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding.booleanValue());
        }
        if (this.allowCircularReferences != null) {
            beanFactory.setAllowCircularReferences(this.allowCircularReferences.booleanValue());
        }
    }
}