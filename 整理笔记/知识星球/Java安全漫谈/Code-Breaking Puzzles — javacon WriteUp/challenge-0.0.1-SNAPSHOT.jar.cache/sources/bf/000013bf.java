package org.springframework.beans.factory;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/HierarchicalBeanFactory.class */
public interface HierarchicalBeanFactory extends BeanFactory {
    @Nullable
    BeanFactory getParentBeanFactory();

    boolean containsLocalBean(String str);
}