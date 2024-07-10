package org.springframework.boot.autoconfigure.template;

import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/template/TemplateAvailabilityProvider.class */
public interface TemplateAvailabilityProvider {
    boolean isTemplateAvailable(String view, Environment environment, ClassLoader classLoader, ResourceLoader resourceLoader);
}