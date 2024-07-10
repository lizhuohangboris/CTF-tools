package org.springframework.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/WebApplicationInitializer.class */
public interface WebApplicationInitializer {
    void onStartup(ServletContext servletContext) throws ServletException;
}