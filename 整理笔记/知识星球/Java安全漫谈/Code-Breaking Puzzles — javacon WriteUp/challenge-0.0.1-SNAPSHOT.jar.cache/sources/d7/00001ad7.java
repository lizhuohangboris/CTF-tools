package org.springframework.boot.web.server;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/server/ErrorPageRegistry.class */
public interface ErrorPageRegistry {
    void addErrorPages(ErrorPage... errorPages);
}