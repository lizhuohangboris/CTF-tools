package org.springframework.beans.factory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/BeanIsAbstractException.class */
public class BeanIsAbstractException extends BeanCreationException {
    public BeanIsAbstractException(String beanName) {
        super(beanName, "Bean definition is abstract");
    }
}