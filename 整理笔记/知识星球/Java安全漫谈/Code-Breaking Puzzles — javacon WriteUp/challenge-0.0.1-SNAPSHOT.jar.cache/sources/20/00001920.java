package org.springframework.boot.context.properties.bind;

import org.springframework.boot.context.properties.source.ConfigurationPropertyName;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/BindHandler.class */
public interface BindHandler {
    public static final BindHandler DEFAULT = new BindHandler() { // from class: org.springframework.boot.context.properties.bind.BindHandler.1
    };

    default <T> Bindable<T> onStart(ConfigurationPropertyName name, Bindable<T> target, BindContext context) {
        return target;
    }

    default Object onSuccess(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Object result) {
        return result;
    }

    default Object onFailure(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Exception error) throws Exception {
        throw error;
    }

    default void onFinish(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Object result) throws Exception {
    }
}