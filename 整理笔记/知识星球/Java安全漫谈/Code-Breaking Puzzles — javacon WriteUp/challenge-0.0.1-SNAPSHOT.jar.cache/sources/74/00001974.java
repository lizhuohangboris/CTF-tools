package org.springframework.boot.diagnostics;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/diagnostics/FailureAnalysis.class */
public class FailureAnalysis {
    private final String description;
    private final String action;
    private final Throwable cause;

    public FailureAnalysis(String description, String action, Throwable cause) {
        this.description = description;
        this.action = action;
        this.cause = cause;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAction() {
        return this.action;
    }

    public Throwable getCause() {
        return this.cause;
    }
}