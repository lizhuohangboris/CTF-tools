package org.springframework.beans.factory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/BeanCreationNotAllowedException.class */
public class BeanCreationNotAllowedException extends BeanCreationException {
    public BeanCreationNotAllowedException(String beanName, String msg) {
        super(beanName, msg);
    }
}