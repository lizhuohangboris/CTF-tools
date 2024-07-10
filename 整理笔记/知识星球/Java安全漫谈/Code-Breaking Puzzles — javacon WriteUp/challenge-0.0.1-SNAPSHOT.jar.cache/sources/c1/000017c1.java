package org.springframework.boot.autoconfigure.security.reactive;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/reactive/PathRequest.class */
public final class PathRequest {
    private PathRequest() {
    }

    public static StaticResourceRequest toStaticResources() {
        return StaticResourceRequest.INSTANCE;
    }
}