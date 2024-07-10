package org.springframework.boot.context.properties.bind;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/BeanBinder.class */
public interface BeanBinder {
    <T> T bind(ConfigurationPropertyName name, Bindable<T> target, Binder.Context context, BeanPropertyBinder propertyBinder);
}