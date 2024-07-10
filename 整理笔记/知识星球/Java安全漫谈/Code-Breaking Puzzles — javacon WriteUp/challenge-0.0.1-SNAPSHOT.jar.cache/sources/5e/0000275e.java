package org.springframework.web.util;

import java.beans.Introspector;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.springframework.beans.CachedIntrospectionResults;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/IntrospectorCleanupListener.class */
public class IntrospectorCleanupListener implements ServletContextListener {
    @Override // javax.servlet.ServletContextListener
    public void contextInitialized(ServletContextEvent event) {
        CachedIntrospectionResults.acceptClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Override // javax.servlet.ServletContextListener
    public void contextDestroyed(ServletContextEvent event) {
        CachedIntrospectionResults.clearClassLoader(Thread.currentThread().getContextClassLoader());
        Introspector.flushCaches();
    }
}