package org.springframework.boot.autoconfigure.security;

import java.util.Arrays;
import java.util.stream.Stream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/StaticResourceLocation.class */
public enum StaticResourceLocation {
    CSS("/css/**"),
    JAVA_SCRIPT("/js/**"),
    IMAGES("/images/**"),
    WEB_JARS("/webjars/**"),
    FAVICON("/**/favicon.ico");
    
    private final String[] patterns;

    StaticResourceLocation(String... patterns) {
        this.patterns = patterns;
    }

    public Stream<String> getPatterns() {
        return Arrays.stream(this.patterns);
    }
}