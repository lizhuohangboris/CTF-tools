package org.apache.catalina;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/AsyncDispatcher.class */
public interface AsyncDispatcher {
    void dispatch(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException;
}