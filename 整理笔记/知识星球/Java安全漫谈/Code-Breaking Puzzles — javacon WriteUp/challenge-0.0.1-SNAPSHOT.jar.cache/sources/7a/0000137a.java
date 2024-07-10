package org.springframework.beans;

import java.beans.PropertyDescriptor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/BeanWrapper.class */
public interface BeanWrapper extends ConfigurablePropertyAccessor {
    void setAutoGrowCollectionLimit(int i);

    int getAutoGrowCollectionLimit();

    Object getWrappedInstance();

    Class<?> getWrappedClass();

    PropertyDescriptor[] getPropertyDescriptors();

    PropertyDescriptor getPropertyDescriptor(String str) throws InvalidPropertyException;
}