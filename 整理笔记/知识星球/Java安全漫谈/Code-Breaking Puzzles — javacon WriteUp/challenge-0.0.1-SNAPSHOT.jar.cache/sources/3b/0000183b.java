package org.springframework.boot.autoconfigure.web;

import org.springframework.beans.factory.annotation.Value;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ErrorProperties.class */
public class ErrorProperties {
    private boolean includeException;
    @Value("${error.path:/error}")
    private String path = "/error";
    private IncludeStacktrace includeStacktrace = IncludeStacktrace.NEVER;
    private final Whitelabel whitelabel = new Whitelabel();

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ErrorProperties$IncludeStacktrace.class */
    public enum IncludeStacktrace {
        NEVER,
        ALWAYS,
        ON_TRACE_PARAM
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isIncludeException() {
        return this.includeException;
    }

    public void setIncludeException(boolean includeException) {
        this.includeException = includeException;
    }

    public IncludeStacktrace getIncludeStacktrace() {
        return this.includeStacktrace;
    }

    public void setIncludeStacktrace(IncludeStacktrace includeStacktrace) {
        this.includeStacktrace = includeStacktrace;
    }

    public Whitelabel getWhitelabel() {
        return this.whitelabel;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ErrorProperties$Whitelabel.class */
    public static class Whitelabel {
        private boolean enabled = true;

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}