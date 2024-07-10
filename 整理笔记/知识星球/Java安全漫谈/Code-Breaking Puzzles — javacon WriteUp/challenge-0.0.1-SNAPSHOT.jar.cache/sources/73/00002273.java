package org.springframework.remoting.rmi;

import javax.naming.NamingException;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/rmi/JndiRmiProxyFactoryBean.class */
public class JndiRmiProxyFactoryBean extends JndiRmiClientInterceptor implements FactoryBean<Object>, BeanClassLoaderAware {
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private Object serviceProxy;

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.remoting.rmi.JndiRmiClientInterceptor, org.springframework.jndi.JndiObjectLocator, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        Class<?> ifc = getServiceInterface();
        Assert.notNull(ifc, "Property 'serviceInterface' is required");
        this.serviceProxy = new ProxyFactory(ifc, this).getProxy(this.beanClassLoader);
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Object getObject() {
        return this.serviceProxy;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return getServiceInterface();
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}