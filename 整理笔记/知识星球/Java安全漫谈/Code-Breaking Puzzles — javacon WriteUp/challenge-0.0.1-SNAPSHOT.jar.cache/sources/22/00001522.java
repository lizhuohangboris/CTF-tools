package org.springframework.boot.autoconfigure;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/AutoConfigurationImportFilter.class */
public interface AutoConfigurationImportFilter {
    boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata);
}