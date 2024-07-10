package org.springframework.boot.web.embedded.jetty;

import javax.servlet.ServletException;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/ServletContextInitializerConfiguration.class */
public class ServletContextInitializerConfiguration extends AbstractConfiguration {
    private final ServletContextInitializer[] initializers;

    public ServletContextInitializerConfiguration(ServletContextInitializer... initializers) {
        Assert.notNull(initializers, "Initializers must not be null");
        this.initializers = initializers;
    }

    public void configure(WebAppContext context) throws Exception {
        context.addBean(new Initializer(context), true);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/ServletContextInitializerConfiguration$Initializer.class */
    private class Initializer extends AbstractLifeCycle {
        private final WebAppContext context;

        Initializer(WebAppContext context) {
            this.context = context;
        }

        protected void doStart() throws Exception {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(this.context.getClassLoader());
            try {
                callInitializers();
                Thread.currentThread().setContextClassLoader(classLoader);
            } catch (Throwable th) {
                Thread.currentThread().setContextClassLoader(classLoader);
                throw th;
            }
        }

        private void callInitializers() throws ServletException {
            ServletContextInitializer[] servletContextInitializerArr;
            try {
                setExtendedListenerTypes(true);
                for (ServletContextInitializer initializer : ServletContextInitializerConfiguration.this.initializers) {
                    initializer.onStartup(this.context.getServletContext());
                }
            } finally {
                setExtendedListenerTypes(false);
            }
        }

        private void setExtendedListenerTypes(boolean extended) {
            try {
                this.context.getServletContext().setExtendedListenerTypes(extended);
            } catch (NoSuchMethodError e) {
            }
        }
    }
}