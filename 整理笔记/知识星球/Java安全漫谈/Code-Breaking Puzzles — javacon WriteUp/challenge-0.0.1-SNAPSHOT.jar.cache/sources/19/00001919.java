package org.springframework.boot.context.properties.bind;

import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/BindContext.class */
public interface BindContext {
    Binder getBinder();

    int getDepth();

    Iterable<ConfigurationPropertySource> getSources();

    ConfigurationProperty getConfigurationProperty();
}