package org.springframework.web.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/ConfigurableWebApplicationContext.class */
public interface ConfigurableWebApplicationContext extends WebApplicationContext, ConfigurableApplicationContext {
    public static final String APPLICATION_CONTEXT_ID_PREFIX = WebApplicationContext.class.getName() + ":";
    public static final String SERVLET_CONFIG_BEAN_NAME = "servletConfig";

    void setServletContext(@Nullable ServletContext servletContext);

    void setServletConfig(@Nullable ServletConfig servletConfig);

    @Nullable
    ServletConfig getServletConfig();

    void setNamespace(@Nullable String str);

    @Nullable
    String getNamespace();

    void setConfigLocation(String str);

    void setConfigLocations(String... strArr);

    @Nullable
    String[] getConfigLocations();
}