package org.springframework.web.context;

import javax.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/WebApplicationContext.class */
public interface WebApplicationContext extends ApplicationContext {
    public static final String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName() + ".ROOT";
    public static final String SCOPE_REQUEST = "request";
    public static final String SCOPE_SESSION = "session";
    public static final String SCOPE_APPLICATION = "application";
    public static final String SERVLET_CONTEXT_BEAN_NAME = "servletContext";
    public static final String CONTEXT_PARAMETERS_BEAN_NAME = "contextParameters";
    public static final String CONTEXT_ATTRIBUTES_BEAN_NAME = "contextAttributes";

    @Nullable
    ServletContext getServletContext();
}