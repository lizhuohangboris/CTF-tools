package org.springframework.boot.web.embedded.undertow;

import io.undertow.servlet.api.DeploymentInfo;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/undertow/UndertowDeploymentInfoCustomizer.class */
public interface UndertowDeploymentInfoCustomizer {
    void customize(DeploymentInfo deploymentInfo);
}