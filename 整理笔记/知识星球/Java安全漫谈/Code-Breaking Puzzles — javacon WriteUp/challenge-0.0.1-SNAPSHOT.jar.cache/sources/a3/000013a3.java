package org.springframework.beans;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/SimpleTypeConverter.class */
public class SimpleTypeConverter extends TypeConverterSupport {
    public SimpleTypeConverter() {
        this.typeConverterDelegate = new TypeConverterDelegate(this);
        registerDefaultEditors();
    }
}