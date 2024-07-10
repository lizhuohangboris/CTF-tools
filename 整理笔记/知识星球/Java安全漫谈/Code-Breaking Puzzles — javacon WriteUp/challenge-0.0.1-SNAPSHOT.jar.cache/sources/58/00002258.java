package org.springframework.remoting.caucho;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/caucho/HessianProxyFactoryBean.class */
public class HessianProxyFactoryBean extends HessianClientInterceptor implements FactoryBean<Object> {
    @Nullable
    private Object serviceProxy;

    @Override // org.springframework.remoting.caucho.HessianClientInterceptor, org.springframework.remoting.support.UrlBasedRemoteAccessor, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy(getBeanClassLoader());
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