package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/redis/LettuceClientConfigurationBuilderCustomizer.class */
public interface LettuceClientConfigurationBuilderCustomizer {
    void customize(LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigurationBuilder);
}