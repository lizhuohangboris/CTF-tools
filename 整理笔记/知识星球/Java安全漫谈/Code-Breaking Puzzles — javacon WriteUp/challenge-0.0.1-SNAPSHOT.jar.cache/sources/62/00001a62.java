package org.springframework.boot.web.client;

import org.springframework.web.client.RestTemplate;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/client/RestTemplateCustomizer.class */
public interface RestTemplateCustomizer {
    void customize(RestTemplate restTemplate);
}