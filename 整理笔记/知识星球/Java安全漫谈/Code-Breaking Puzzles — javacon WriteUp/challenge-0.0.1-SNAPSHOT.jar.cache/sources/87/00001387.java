package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Method;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/ExtendedBeanInfoFactory.class */
public class ExtendedBeanInfoFactory implements BeanInfoFactory, Ordered {
    @Override // org.springframework.beans.BeanInfoFactory
    @Nullable
    public BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
        if (supports(beanClass)) {
            return new ExtendedBeanInfo(Introspector.getBeanInfo(beanClass));
        }
        return null;
    }

    private boolean supports(Class<?> beanClass) {
        Method[] methods;
        for (Method method : beanClass.getMethods()) {
            if (ExtendedBeanInfo.isCandidateWriteMethod(method)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}