package org.springframework.boot;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/ExitCodeExceptionMapper.class */
public interface ExitCodeExceptionMapper {
    int getExitCode(Throwable exception);
}