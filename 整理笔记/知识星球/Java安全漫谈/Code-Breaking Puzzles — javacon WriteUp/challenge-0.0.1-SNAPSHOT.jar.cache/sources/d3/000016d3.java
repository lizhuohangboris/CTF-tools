package org.springframework.boot.autoconfigure.jackson;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jackson/Jackson2ObjectMapperBuilderCustomizer.class */
public interface Jackson2ObjectMapperBuilderCustomizer {
    void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder);
}