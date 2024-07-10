package org.springframework.boot.diagnostics;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/diagnostics/FailureAnalysisReporter.class */
public interface FailureAnalysisReporter {
    void report(FailureAnalysis analysis);
}