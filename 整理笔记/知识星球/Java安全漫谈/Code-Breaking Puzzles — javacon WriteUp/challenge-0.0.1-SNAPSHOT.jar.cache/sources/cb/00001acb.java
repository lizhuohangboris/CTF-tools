package org.springframework.boot.web.reactive.function.client;

import org.springframework.web.reactive.function.client.WebClient;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/reactive/function/client/WebClientCustomizer.class */
public interface WebClientCustomizer {
    void customize(WebClient.Builder webClientBuilder);
}