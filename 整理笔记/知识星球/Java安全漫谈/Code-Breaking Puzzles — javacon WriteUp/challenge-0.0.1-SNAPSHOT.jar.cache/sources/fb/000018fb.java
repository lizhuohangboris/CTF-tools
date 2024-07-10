package org.springframework.boot.context.properties;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBindException.class */
public class ConfigurationPropertiesBindException extends BeanCreationException {
    private final Class<?> beanType;
    private final ConfigurationProperties annotation;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigurationPropertiesBindException(String beanName, Object bean, ConfigurationProperties annotation, Exception cause) {
        super(beanName, getMessage(bean, annotation), cause);
        this.beanType = bean.getClass();
        this.annotation = annotation;
    }

    public Class<?> getBeanType() {
        return this.beanType;
    }

    public ConfigurationProperties getAnnotation() {
        return this.annotation;
    }

    private static String getMessage(Object bean, ConfigurationProperties annotation) {
        StringBuilder message = new StringBuilder();
        message.append("Could not bind properties to '" + ClassUtils.getShortName(bean.getClass()) + "' : ");
        message.append("prefix=").append(annotation.prefix());
        message.append(", ignoreInvalidFields=").append(annotation.ignoreInvalidFields());
        message.append(", ignoreUnknownFields=").append(annotation.ignoreUnknownFields());
        return message.toString();
    }
}