package org.springframework.boot;

import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/WebApplicationType.class */
public enum WebApplicationType {
    NONE,
    SERVLET,
    REACTIVE;
    
    private static final String[] SERVLET_INDICATOR_CLASSES = {"javax.servlet.Servlet", "org.springframework.web.context.ConfigurableWebApplicationContext"};
    private static final String WEBMVC_INDICATOR_CLASS = "org.springframework.web.servlet.DispatcherServlet";
    private static final String WEBFLUX_INDICATOR_CLASS = "org.springframework.web.reactive.DispatcherHandler";
    private static final String JERSEY_INDICATOR_CLASS = "org.glassfish.jersey.servlet.ServletContainer";
    private static final String SERVLET_APPLICATION_CONTEXT_CLASS = "org.springframework.web.context.WebApplicationContext";
    private static final String REACTIVE_APPLICATION_CONTEXT_CLASS = "org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext";

    /* JADX INFO: Access modifiers changed from: package-private */
    public static WebApplicationType deduceFromClasspath() {
        String[] strArr;
        if (ClassUtils.isPresent(WEBFLUX_INDICATOR_CLASS, null) && !ClassUtils.isPresent(WEBMVC_INDICATOR_CLASS, null) && !ClassUtils.isPresent(JERSEY_INDICATOR_CLASS, null)) {
            return REACTIVE;
        }
        for (String className : SERVLET_INDICATOR_CLASSES) {
            if (!ClassUtils.isPresent(className, null)) {
                return NONE;
            }
        }
        return SERVLET;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static WebApplicationType deduceFromApplicationContext(Class<?> applicationContextClass) {
        if (isAssignable(SERVLET_APPLICATION_CONTEXT_CLASS, applicationContextClass)) {
            return SERVLET;
        }
        if (isAssignable(REACTIVE_APPLICATION_CONTEXT_CLASS, applicationContextClass)) {
            return REACTIVE;
        }
        return NONE;
    }

    private static boolean isAssignable(String target, Class<?> type) {
        try {
            return ClassUtils.resolveClassName(target, null).isAssignableFrom(type);
        } catch (Throwable th) {
            return false;
        }
    }
}