package org.springframework.boot.web.embedded.tomcat;

import org.apache.catalina.Context;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/tomcat/TomcatContextCustomizer.class */
public interface TomcatContextCustomizer {
    void customize(Context context);
}