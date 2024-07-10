package org.springframework.boot.context.properties.bind;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/BeanPropertyBinder.class */
public interface BeanPropertyBinder {
    Object bindProperty(String propertyName, Bindable<?> target);
}