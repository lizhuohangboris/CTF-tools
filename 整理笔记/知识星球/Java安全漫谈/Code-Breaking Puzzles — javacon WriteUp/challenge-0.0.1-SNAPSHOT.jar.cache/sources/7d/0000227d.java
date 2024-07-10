package org.springframework.remoting.rmi;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/rmi/RmiProxyFactoryBean.class */
public class RmiProxyFactoryBean extends RmiClientInterceptor implements FactoryBean<Object>, BeanClassLoaderAware {
    private Object serviceProxy;

    @Override // org.springframework.remoting.rmi.RmiClientInterceptor, org.springframework.remoting.support.UrlBasedRemoteAccessor, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Class<?> ifc = getServiceInterface();
        Assert.notNull(ifc, "Property 'serviceInterface' is required");
        this.serviceProxy = new ProxyFactory(ifc, this).getProxy(getBeanClassLoader());
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