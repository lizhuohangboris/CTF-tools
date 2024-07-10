package org.springframework.beans;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/NullValueInNestedPathException.class */
public class NullValueInNestedPathException extends InvalidPropertyException {
    public NullValueInNestedPathException(Class<?> beanClass, String propertyName) {
        super(beanClass, propertyName, "Value of nested property '" + propertyName + "' is null");
    }

    public NullValueInNestedPathException(Class<?> beanClass, String propertyName, String msg) {
        super(beanClass, propertyName, msg);
    }

    public NullValueInNestedPathException(Class<?> beanClass, String propertyName, String msg, Throwable cause) {
        super(beanClass, propertyName, msg, cause);
    }
}