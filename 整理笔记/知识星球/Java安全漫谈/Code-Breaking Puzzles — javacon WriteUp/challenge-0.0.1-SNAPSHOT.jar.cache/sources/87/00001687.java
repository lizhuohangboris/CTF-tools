package org.springframework.boot.autoconfigure.gson;

import com.google.gson.GsonBuilder;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/gson/GsonBuilderCustomizer.class */
public interface GsonBuilderCustomizer {
    void customize(GsonBuilder gsonBuilder);
}