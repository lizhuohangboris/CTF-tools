package org.springframework.boot.web.embedded.tomcat;

import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/tomcat/TomcatStarter.class */
class TomcatStarter implements ServletContainerInitializer {
    private static final Log logger = LogFactory.getLog(TomcatStarter.class);
    private final ServletContextInitializer[] initializers;
    private volatile Exception startUpException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TomcatStarter(ServletContextInitializer[] initializers) {
        this.initializers = initializers;
    }

    @Override // javax.servlet.ServletContainerInitializer
    public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
        ServletContextInitializer[] servletContextInitializerArr;
        try {
            for (ServletContextInitializer initializer : this.initializers) {
                initializer.onStartup(servletContext);
            }
        } catch (Exception ex) {
            this.startUpException = ex;
            if (logger.isErrorEnabled()) {
                logger.error("Error starting Tomcat context. Exception: " + ex.getClass().getName() + ". Message: " + ex.getMessage());
            }
        }
    }

    public Exception getStartUpException() {
        return this.startUpException;
    }
}