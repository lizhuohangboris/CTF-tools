package org.springframework.boot.web.embedded.tomcat;

import org.apache.catalina.connector.Connector;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/tomcat/TomcatConnectorCustomizer.class */
public interface TomcatConnectorCustomizer {
    void customize(Connector connector);
}