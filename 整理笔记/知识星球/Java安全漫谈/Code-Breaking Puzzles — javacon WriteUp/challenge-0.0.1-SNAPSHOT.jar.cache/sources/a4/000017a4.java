package org.springframework.boot.autoconfigure.reactor.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.reactor")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/reactor/core/ReactorCoreProperties.class */
public class ReactorCoreProperties {
    private final StacktraceMode stacktraceMode = new StacktraceMode();

    public StacktraceMode getStacktraceMode() {
        return this.stacktraceMode;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/reactor/core/ReactorCoreProperties$StacktraceMode.class */
    public static class StacktraceMode {
        private boolean enabled;

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}