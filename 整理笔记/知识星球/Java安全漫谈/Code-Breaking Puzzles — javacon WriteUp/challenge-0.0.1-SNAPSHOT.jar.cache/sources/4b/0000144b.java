package org.springframework.beans.factory.serviceloader;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import org.springframework.beans.factory.BeanClassLoaderAware;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/serviceloader/ServiceListFactoryBean.class */
public class ServiceListFactoryBean extends AbstractServiceLoaderBasedFactoryBean implements BeanClassLoaderAware {
    @Override // org.springframework.beans.factory.serviceloader.AbstractServiceLoaderBasedFactoryBean
    protected Object getObjectToExpose(ServiceLoader<?> serviceLoader) {
        List<Object> result = new LinkedList<>();
        Iterator<?> it = serviceLoader.iterator();
        while (it.hasNext()) {
            Object loaderObject = it.next();
            result.add(loaderObject);
        }
        return result;
    }

    @Override // org.springframework.beans.factory.config.AbstractFactoryBean, org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return List.class;
    }
}