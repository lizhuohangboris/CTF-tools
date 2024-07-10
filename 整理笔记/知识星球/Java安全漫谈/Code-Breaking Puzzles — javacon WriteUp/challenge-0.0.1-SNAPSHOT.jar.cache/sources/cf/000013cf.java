package org.springframework.beans.factory.annotation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/annotation/Autowire.class */
public enum Autowire {
    NO(0),
    BY_NAME(1),
    BY_TYPE(2);
    
    private final int value;

    Autowire(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public boolean isAutowire() {
        return this == BY_NAME || this == BY_TYPE;
    }
}