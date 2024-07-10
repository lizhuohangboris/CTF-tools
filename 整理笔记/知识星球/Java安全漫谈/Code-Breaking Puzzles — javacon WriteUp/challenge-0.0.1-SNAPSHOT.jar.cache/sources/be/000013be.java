package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/FactoryBeanNotInitializedException.class */
public class FactoryBeanNotInitializedException extends FatalBeanException {
    public FactoryBeanNotInitializedException() {
        super("FactoryBean is not fully initialized yet");
    }

    public FactoryBeanNotInitializedException(String msg) {
        super(msg);
    }
}