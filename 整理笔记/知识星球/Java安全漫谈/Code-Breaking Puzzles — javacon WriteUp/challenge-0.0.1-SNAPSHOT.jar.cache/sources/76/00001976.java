package org.springframework.boot.diagnostics;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/diagnostics/FailureAnalyzer.class */
public interface FailureAnalyzer {
    FailureAnalysis analyze(Throwable failure);
}