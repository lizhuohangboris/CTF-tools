package org.springframework.boot.web.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/ServletContextInitializer.class */
public interface ServletContextInitializer {
    void onStartup(ServletContext servletContext) throws ServletException;
}