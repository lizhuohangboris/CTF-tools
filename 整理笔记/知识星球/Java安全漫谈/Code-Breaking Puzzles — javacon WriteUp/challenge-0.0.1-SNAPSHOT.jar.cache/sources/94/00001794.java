package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.Map;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernatePropertiesCustomizer.class */
public interface HibernatePropertiesCustomizer {
    void customize(Map<String, Object> hibernateProperties);
}