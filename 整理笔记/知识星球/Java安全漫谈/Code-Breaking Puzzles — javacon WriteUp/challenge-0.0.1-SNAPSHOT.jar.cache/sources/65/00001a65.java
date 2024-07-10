package org.springframework.boot.web.context;

import org.springframework.context.ConfigurableApplicationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/context/ConfigurableWebServerApplicationContext.class */
public interface ConfigurableWebServerApplicationContext extends ConfigurableApplicationContext, WebServerApplicationContext {
    void setServerNamespace(String serverNamespace);
}