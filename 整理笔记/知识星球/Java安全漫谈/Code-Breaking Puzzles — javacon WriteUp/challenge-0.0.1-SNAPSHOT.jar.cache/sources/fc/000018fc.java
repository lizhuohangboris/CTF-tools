package org.springframework.boot.context.properties;

import org.springframework.boot.context.properties.bind.BindHandler;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBindHandlerAdvisor.class */
public interface ConfigurationPropertiesBindHandlerAdvisor {
    BindHandler apply(BindHandler bindHandler);
}