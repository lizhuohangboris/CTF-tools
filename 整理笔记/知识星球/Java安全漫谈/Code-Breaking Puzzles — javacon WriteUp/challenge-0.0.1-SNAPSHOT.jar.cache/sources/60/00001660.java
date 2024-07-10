package org.springframework.boot.autoconfigure.elasticsearch.jest;

import io.searchbox.client.config.HttpClientConfig;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/elasticsearch/jest/HttpClientConfigBuilderCustomizer.class */
public interface HttpClientConfigBuilderCustomizer {
    void customize(HttpClientConfig.Builder builder);
}