package org.springframework.web.context;

import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/ContextCleanupListener.class */
public class ContextCleanupListener implements ServletContextListener {
    private static final Log logger = LogFactory.getLog(ContextCleanupListener.class);

    @Override // javax.servlet.ServletContextListener
    public void contextInitialized(ServletContextEvent event) {
    }

    @Override // javax.servlet.ServletContextListener
    public void contextDestroyed(ServletContextEvent event) {
        cleanupAttributes(event.getServletContext());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void cleanupAttributes(ServletContext sc) {
        Enumeration<String> attrNames = sc.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attrName = attrNames.nextElement();
            if (attrName.startsWith("org.springframework.")) {
                Object attrValue = sc.getAttribute(attrName);
                if (attrValue instanceof DisposableBean) {
                    try {
                        ((DisposableBean) attrValue).destroy();
                    } catch (Throwable ex) {
                        logger.error("Couldn't invoke destroy method of attribute with name '" + attrName + "'", ex);
                    }
                }
            }
        }
    }
}