package org.springframework.jmx.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.management.MBeanServer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/support/WebSphereMBeanServerFactoryBean.class */
public class WebSphereMBeanServerFactoryBean implements FactoryBean<MBeanServer>, InitializingBean {
    private static final String ADMIN_SERVICE_FACTORY_CLASS = "com.ibm.websphere.management.AdminServiceFactory";
    private static final String GET_MBEAN_FACTORY_METHOD = "getMBeanFactory";
    private static final String GET_MBEAN_SERVER_METHOD = "getMBeanServer";
    @Nullable
    private MBeanServer mbeanServer;

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws MBeanServerNotFoundException {
        try {
            Class<?> adminServiceClass = getClass().getClassLoader().loadClass(ADMIN_SERVICE_FACTORY_CLASS);
            Method getMBeanFactoryMethod = adminServiceClass.getMethod(GET_MBEAN_FACTORY_METHOD, new Class[0]);
            Object mbeanFactory = getMBeanFactoryMethod.invoke(null, new Object[0]);
            Method getMBeanServerMethod = mbeanFactory.getClass().getMethod(GET_MBEAN_SERVER_METHOD, new Class[0]);
            this.mbeanServer = (MBeanServer) getMBeanServerMethod.invoke(mbeanFactory, new Object[0]);
        } catch (ClassNotFoundException ex) {
            throw new MBeanServerNotFoundException("Could not find WebSphere's AdminServiceFactory class", ex);
        } catch (InvocationTargetException ex2) {
            throw new MBeanServerNotFoundException("WebSphere's AdminServiceFactory.getMBeanFactory/getMBeanServer method failed", ex2.getTargetException());
        } catch (Exception ex3) {
            throw new MBeanServerNotFoundException("Could not access WebSphere's AdminServiceFactory.getMBeanFactory/getMBeanServer method", ex3);
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public MBeanServer getObject() {
        return this.mbeanServer;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<? extends MBeanServer> getObjectType() {
        return this.mbeanServer != null ? this.mbeanServer.getClass() : MBeanServer.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}