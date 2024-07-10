package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/BeanInfoFactory.class */
public interface BeanInfoFactory {
    @Nullable
    BeanInfo getBeanInfo(Class<?> cls) throws IntrospectionException;
}