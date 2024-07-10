package org.springframework.remoting.httpinvoker;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/httpinvoker/HttpInvokerProxyFactoryBean.class */
public class HttpInvokerProxyFactoryBean extends HttpInvokerClientInterceptor implements FactoryBean<Object> {
    @Nullable
    private Object serviceProxy;

    @Override // org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor, org.springframework.remoting.support.UrlBasedRemoteAccessor, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Class<?> ifc = getServiceInterface();
        Assert.notNull(ifc, "Property 'serviceInterface' is required");
        this.serviceProxy = new ProxyFactory(ifc, this).getProxy(getBeanClassLoader());
    }

    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
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