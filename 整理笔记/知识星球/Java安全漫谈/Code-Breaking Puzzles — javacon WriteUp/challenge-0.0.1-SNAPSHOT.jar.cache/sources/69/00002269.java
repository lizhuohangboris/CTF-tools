package org.springframework.remoting.jaxws;

import javax.xml.ws.BindingProvider;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/jaxws/JaxWsPortProxyFactoryBean.class */
public class JaxWsPortProxyFactoryBean extends JaxWsPortClientInterceptor implements FactoryBean<Object> {
    @Nullable
    private Object serviceProxy;

    @Override // org.springframework.remoting.jaxws.JaxWsPortClientInterceptor, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Class<?> ifc = getServiceInterface();
        Assert.notNull(ifc, "Property 'serviceInterface' is required");
        ProxyFactory pf = new ProxyFactory();
        pf.addInterface(ifc);
        pf.addInterface(BindingProvider.class);
        pf.addAdvice(this);
        this.serviceProxy = pf.getProxy(getBeanClassLoader());
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