package org.springframework.boot.autoconfigure.web.reactive;

import org.springframework.web.reactive.config.ResourceHandlerRegistration;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/ResourceHandlerRegistrationCustomizer.class */
public interface ResourceHandlerRegistrationCustomizer {
    void customize(ResourceHandlerRegistration registration);
}