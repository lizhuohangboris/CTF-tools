package org.springframework.boot.web.codec;

import org.springframework.http.codec.CodecConfigurer;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/codec/CodecCustomizer.class */
public interface CodecCustomizer {
    void customize(CodecConfigurer configurer);
}