package org.springframework.boot.web.embedded.undertow;

import io.undertow.Undertow;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/undertow/UndertowBuilderCustomizer.class */
public interface UndertowBuilderCustomizer {
    void customize(Undertow.Builder builder);
}