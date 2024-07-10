package org.springframework.web.context;

import javax.servlet.ServletContext;
import org.springframework.beans.factory.Aware;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/ServletContextAware.class */
public interface ServletContextAware extends Aware {
    void setServletContext(ServletContext servletContext);
}