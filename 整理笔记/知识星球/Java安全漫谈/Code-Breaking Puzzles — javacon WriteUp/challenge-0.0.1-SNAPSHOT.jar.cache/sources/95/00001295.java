package org.springframework.aop.aspectj.annotation;

import java.io.Serializable;
import org.springframework.beans.factory.BeanFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/annotation/PrototypeAspectInstanceFactory.class */
public class PrototypeAspectInstanceFactory extends BeanFactoryAspectInstanceFactory implements Serializable {
    public PrototypeAspectInstanceFactory(BeanFactory beanFactory, String name) {
        super(beanFactory, name);
        if (!beanFactory.isPrototype(name)) {
            throw new IllegalArgumentException("Cannot use PrototypeAspectInstanceFactory with bean named '" + name + "': not a prototype");
        }
    }
}