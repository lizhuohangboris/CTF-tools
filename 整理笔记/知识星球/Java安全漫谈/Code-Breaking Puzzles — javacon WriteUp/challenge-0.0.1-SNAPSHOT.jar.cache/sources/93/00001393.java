package org.springframework.beans;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/PropertyAccessorFactory.class */
public final class PropertyAccessorFactory {
    private PropertyAccessorFactory() {
    }

    public static BeanWrapper forBeanPropertyAccess(Object target) {
        return new BeanWrapperImpl(target);
    }

    public static ConfigurablePropertyAccessor forDirectFieldAccess(Object target) {
        return new DirectFieldAccessor(target);
    }
}