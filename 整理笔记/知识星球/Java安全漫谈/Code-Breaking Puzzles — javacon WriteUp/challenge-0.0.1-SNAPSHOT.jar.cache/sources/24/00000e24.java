package org.apache.tomcat.websocket.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/server/WsContextListener.class */
public class WsContextListener implements ServletContextListener {
    @Override // javax.servlet.ServletContextListener
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        if (sc.getAttribute(Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE) == null) {
            WsSci.init(sce.getServletContext(), false);
        }
    }

    @Override // javax.servlet.ServletContextListener
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        Object obj = sc.getAttribute(Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE);
        if (obj instanceof WsServerContainer) {
            ((WsServerContainer) obj).destroy();
        }
    }
}