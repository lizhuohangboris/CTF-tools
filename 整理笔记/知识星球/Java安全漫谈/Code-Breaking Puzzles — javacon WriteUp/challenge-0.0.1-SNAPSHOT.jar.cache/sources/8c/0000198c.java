package org.springframework.boot.env;

import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/env/EnvironmentPostProcessor.class */
public interface EnvironmentPostProcessor {
    void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application);
}