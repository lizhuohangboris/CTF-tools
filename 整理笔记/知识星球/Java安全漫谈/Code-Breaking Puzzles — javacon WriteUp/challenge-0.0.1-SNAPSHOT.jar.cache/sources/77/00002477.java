package org.springframework.web.context;

import javax.servlet.ServletConfig;
import org.springframework.beans.factory.Aware;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/ServletConfigAware.class */
public interface ServletConfigAware extends Aware {
    void setServletConfig(ServletConfig servletConfig);
}