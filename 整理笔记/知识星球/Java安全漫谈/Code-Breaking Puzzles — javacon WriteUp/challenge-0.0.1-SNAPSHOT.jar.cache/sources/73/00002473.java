package org.springframework.web.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/ConfigurableWebEnvironment.class */
public interface ConfigurableWebEnvironment extends ConfigurableEnvironment {
    void initPropertySources(@Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig);
}