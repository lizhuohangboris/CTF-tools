package org.springframework.aop.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.Interceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.UnknownAdviceTypeException;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/ProxyFactoryBean.class */
public class ProxyFactoryBean extends ProxyCreatorSupport implements FactoryBean<Object>, BeanClassLoaderAware, BeanFactoryAware {
    public static final String GLOBAL_SUFFIX = "*";
    @Nullable
    private String[] interceptorNames;
    @Nullable
    private String targetName;
    @Nullable
    private transient BeanFactory beanFactory;
    @Nullable
    private Object singletonInstance;
    protected final Log logger = LogFactory.getLog(getClass());
    private boolean autodetectInterfaces = true;
    private boolean singleton = true;
    private AdvisorAdapterRegistry advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();
    private boolean freezeProxy = false;
    @Nullable
    private transient ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();
    private transient boolean classLoaderConfigured = false;
    private boolean advisorChainInitialized = false;

    public void setProxyInterfaces(Class<?>[] proxyInterfaces) throws ClassNotFoundException {
        setInterfaces(proxyInterfaces);
    }

    public void setInterceptorNames(String... interceptorNames) {
        this.interceptorNames = interceptorNames;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setAutodetectInterfaces(boolean autodetectInterfaces) {
        this.autodetectInterfaces = autodetectInterfaces;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public void setAdvisorAdapterRegistry(AdvisorAdapterRegistry advisorAdapterRegistry) {
        this.advisorAdapterRegistry = advisorAdapterRegistry;
    }

    @Override // org.springframework.aop.framework.ProxyConfig
    public void setFrozen(boolean frozen) {
        this.freezeProxy = frozen;
    }

    public void setProxyClassLoader(@Nullable ClassLoader classLoader) {
        this.proxyClassLoader = classLoader;
        this.classLoaderConfigured = classLoader != null;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        if (!this.classLoaderConfigured) {
            this.proxyClassLoader = classLoader;
        }
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        checkInterceptorNames();
    }

    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public Object getObject() throws BeansException {
        initializeAdvisorChain();
        if (isSingleton()) {
            return getSingletonInstance();
        }
        if (this.targetName == null) {
            this.logger.info("Using non-singleton proxies with singleton targets is often undesirable. Enable prototype proxies by setting the 'targetName' property.");
        }
        return newPrototypeInstance();
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        synchronized (this) {
            if (this.singletonInstance != null) {
                return this.singletonInstance.getClass();
            }
            Class<?>[] ifcs = getProxiedInterfaces();
            if (ifcs.length == 1) {
                return ifcs[0];
            }
            if (ifcs.length > 1) {
                return createCompositeInterface(ifcs);
            }
            if (this.targetName != null && this.beanFactory != null) {
                return this.beanFactory.getType(this.targetName);
            }
            return getTargetClass();
        }
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return this.singleton;
    }

    protected Class<?> createCompositeInterface(Class<?>[] interfaces) {
        return ClassUtils.createCompositeInterface(interfaces, this.proxyClassLoader);
    }

    private synchronized Object getSingletonInstance() {
        if (this.singletonInstance == null) {
            this.targetSource = freshTargetSource();
            if (this.autodetectInterfaces && getProxiedInterfaces().length == 0 && !isProxyTargetClass()) {
                Class<?> targetClass = getTargetClass();
                if (targetClass == null) {
                    throw new FactoryBeanNotInitializedException("Cannot determine target class for proxy");
                }
                setInterfaces(ClassUtils.getAllInterfacesForClass(targetClass, this.proxyClassLoader));
            }
            super.setFrozen(this.freezeProxy);
            this.singletonInstance = getProxy(createAopProxy());
        }
        return this.singletonInstance;
    }

    private synchronized Object newPrototypeInstance() {
        Class<?> targetClass;
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Creating copy of prototype ProxyFactoryBean config: " + this);
        }
        ProxyCreatorSupport copy = new ProxyCreatorSupport(getAopProxyFactory());
        TargetSource targetSource = freshTargetSource();
        copy.copyConfigurationFrom(this, targetSource, freshAdvisorChain());
        if (this.autodetectInterfaces && getProxiedInterfaces().length == 0 && !isProxyTargetClass() && (targetClass = targetSource.getTargetClass()) != null) {
            copy.setInterfaces(ClassUtils.getAllInterfacesForClass(targetClass, this.proxyClassLoader));
        }
        copy.setFrozen(this.freezeProxy);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Using ProxyCreatorSupport copy: " + copy);
        }
        return getProxy(copy.createAopProxy());
    }

    protected Object getProxy(AopProxy aopProxy) {
        return aopProxy.getProxy(this.proxyClassLoader);
    }

    private void checkInterceptorNames() {
        if (!ObjectUtils.isEmpty((Object[]) this.interceptorNames)) {
            String finalName = this.interceptorNames[this.interceptorNames.length - 1];
            if (this.targetName == null && this.targetSource == EMPTY_TARGET_SOURCE && !finalName.endsWith("*") && !isNamedBeanAnAdvisorOrAdvice(finalName)) {
                this.targetName = finalName;
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Bean with name '" + finalName + "' concluding interceptor chain is not an advisor class: treating it as a target or TargetSource");
                }
                String[] newNames = new String[this.interceptorNames.length - 1];
                System.arraycopy(this.interceptorNames, 0, newNames, 0, newNames.length);
                this.interceptorNames = newNames;
            }
        }
    }

    private boolean isNamedBeanAnAdvisorOrAdvice(String beanName) {
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        Class<?> namedBeanClass = this.beanFactory.getType(beanName);
        if (namedBeanClass != null) {
            return Advisor.class.isAssignableFrom(namedBeanClass) || Advice.class.isAssignableFrom(namedBeanClass);
        } else if (this.logger.isDebugEnabled()) {
            this.logger.debug("Could not determine type of bean with name '" + beanName + "' - assuming it is neither an Advisor nor an Advice");
            return false;
        } else {
            return false;
        }
    }

    private synchronized void initializeAdvisorChain() throws AopConfigException, BeansException {
        String[] strArr;
        Object advice;
        if (this.advisorChainInitialized) {
            return;
        }
        if (!ObjectUtils.isEmpty((Object[]) this.interceptorNames)) {
            if (this.beanFactory == null) {
                throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) - cannot resolve interceptor names " + Arrays.asList(this.interceptorNames));
            }
            if (this.interceptorNames[this.interceptorNames.length - 1].endsWith("*") && this.targetName == null && this.targetSource == EMPTY_TARGET_SOURCE) {
                throw new AopConfigException("Target required after globals");
            }
            for (String name : this.interceptorNames) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Configuring advisor or advice '" + name + "'");
                }
                if (name.endsWith("*")) {
                    if (!(this.beanFactory instanceof ListableBeanFactory)) {
                        throw new AopConfigException("Can only use global advisors or interceptors with a ListableBeanFactory");
                    }
                    addGlobalAdvisor((ListableBeanFactory) this.beanFactory, name.substring(0, name.length() - "*".length()));
                } else {
                    if (this.singleton || this.beanFactory.isSingleton(name)) {
                        advice = this.beanFactory.getBean(name);
                    } else {
                        advice = new PrototypePlaceholderAdvisor(name);
                    }
                    addAdvisorOnChainCreation(advice, name);
                }
            }
        }
        this.advisorChainInitialized = true;
    }

    private List<Advisor> freshAdvisorChain() {
        Advisor[] advisors = getAdvisors();
        List<Advisor> freshAdvisors = new ArrayList<>(advisors.length);
        for (Advisor advisor : advisors) {
            if (advisor instanceof PrototypePlaceholderAdvisor) {
                PrototypePlaceholderAdvisor pa = (PrototypePlaceholderAdvisor) advisor;
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Refreshing bean named '" + pa.getBeanName() + "'");
                }
                if (this.beanFactory == null) {
                    throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) - cannot resolve prototype advisor '" + pa.getBeanName() + "'");
                }
                Object bean = this.beanFactory.getBean(pa.getBeanName());
                Advisor refreshedAdvisor = namedBeanToAdvisor(bean);
                freshAdvisors.add(refreshedAdvisor);
            } else {
                freshAdvisors.add(advisor);
            }
        }
        return freshAdvisors;
    }

    private void addGlobalAdvisor(ListableBeanFactory beanFactory, String prefix) {
        String[] globalAdvisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, Advisor.class);
        String[] globalInterceptorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, Interceptor.class);
        List<Object> beans = new ArrayList<>(globalAdvisorNames.length + globalInterceptorNames.length);
        Map<Object, String> names = new HashMap<>(beans.size());
        for (String name : globalAdvisorNames) {
            Object bean = beanFactory.getBean(name);
            beans.add(bean);
            names.put(bean, name);
        }
        for (String name2 : globalInterceptorNames) {
            Object bean2 = beanFactory.getBean(name2);
            beans.add(bean2);
            names.put(bean2, name2);
        }
        AnnotationAwareOrderComparator.sort(beans);
        for (Object bean3 : beans) {
            String name3 = names.get(bean3);
            if (name3.startsWith(prefix)) {
                addAdvisorOnChainCreation(bean3, name3);
            }
        }
    }

    private void addAdvisorOnChainCreation(Object next, String name) {
        Advisor advisor = namedBeanToAdvisor(next);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Adding advisor with name '" + name + "'");
        }
        addAdvisor(advisor);
    }

    private TargetSource freshTargetSource() {
        if (this.targetName == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Not refreshing target: Bean name not specified in 'interceptorNames'.");
            }
            return this.targetSource;
        } else if (this.beanFactory == null) {
            throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) - cannot resolve target with name '" + this.targetName + "'");
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Refreshing target with name '" + this.targetName + "'");
            }
            Object target = this.beanFactory.getBean(this.targetName);
            return target instanceof TargetSource ? (TargetSource) target : new SingletonTargetSource(target);
        }
    }

    private Advisor namedBeanToAdvisor(Object next) {
        try {
            return this.advisorAdapterRegistry.wrap(next);
        } catch (UnknownAdviceTypeException ex) {
            throw new AopConfigException("Unknown advisor type " + next.getClass() + "; Can only include Advisor or Advice type beans in interceptorNames chain except for last entry,which may also be target or TargetSource", ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.aop.framework.ProxyCreatorSupport, org.springframework.aop.framework.AdvisedSupport
    public void adviceChanged() {
        super.adviceChanged();
        if (this.singleton) {
            this.logger.debug("Advice has changed; recaching singleton instance");
            synchronized (this) {
                this.singletonInstance = null;
            }
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.proxyClassLoader = ClassUtils.getDefaultClassLoader();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/ProxyFactoryBean$PrototypePlaceholderAdvisor.class */
    public static class PrototypePlaceholderAdvisor implements Advisor, Serializable {
        private final String beanName;
        private final String message;

        public PrototypePlaceholderAdvisor(String beanName) {
            this.beanName = beanName;
            this.message = "Placeholder for prototype Advisor/Advice with bean name '" + beanName + "'";
        }

        public String getBeanName() {
            return this.beanName;
        }

        @Override // org.springframework.aop.Advisor
        public Advice getAdvice() {
            throw new UnsupportedOperationException("Cannot invoke methods: " + this.message);
        }

        @Override // org.springframework.aop.Advisor
        public boolean isPerInstance() {
            throw new UnsupportedOperationException("Cannot invoke methods: " + this.message);
        }

        public String toString() {
            return this.message;
        }
    }
}