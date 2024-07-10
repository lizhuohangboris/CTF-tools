package org.springframework.boot.autoconfigure.orm.jpa;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/orm/jpa/EntityManagerFactoryBuilderCustomizer.class */
public interface EntityManagerFactoryBuilderCustomizer {
    void customize(EntityManagerFactoryBuilder builder);
}