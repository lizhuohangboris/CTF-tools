package org.springframework.boot.web.embedded.undertow;

import java.io.File;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/undertow/ConfigurableUndertowWebServerFactory.class */
public interface ConfigurableUndertowWebServerFactory extends ConfigurableWebServerFactory {
    void addBuilderCustomizers(UndertowBuilderCustomizer... customizers);

    void addDeploymentInfoCustomizers(UndertowDeploymentInfoCustomizer... customizers);

    void setBufferSize(Integer bufferSize);

    void setIoThreads(Integer ioThreads);

    void setWorkerThreads(Integer workerThreads);

    void setUseDirectBuffers(Boolean useForwardHeaders);

    void setAccessLogDirectory(File accessLogDirectory);

    void setAccessLogPattern(String accessLogPattern);

    void setAccessLogPrefix(String accessLogPrefix);

    void setAccessLogSuffix(String accessLogSuffix);

    void setAccessLogEnabled(boolean accessLogEnabled);

    void setAccessLogRotate(boolean accessLogRotate);

    void setUseForwardHeaders(boolean useForwardHeaders);
}