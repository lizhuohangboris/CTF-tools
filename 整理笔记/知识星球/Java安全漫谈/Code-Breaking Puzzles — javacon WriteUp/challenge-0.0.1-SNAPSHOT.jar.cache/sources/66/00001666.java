package org.springframework.boot.autoconfigure.elasticsearch.rest;

import org.elasticsearch.client.RestClientBuilder;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/elasticsearch/rest/RestClientBuilderCustomizer.class */
public interface RestClientBuilderCustomizer {
    void customize(RestClientBuilder builder);
}