package org.springframework.boot.web.embedded.jetty;

import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.webapp.WebAppContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/JettyEmbeddedWebAppContext.class */
class JettyEmbeddedWebAppContext extends WebAppContext {
    protected ServletHandler newServletHandler() {
        return new JettyEmbeddedServletHandler();
    }

    public void deferredInitialize() throws Exception {
        ((JettyEmbeddedServletHandler) getServletHandler()).deferredInitialize();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/JettyEmbeddedWebAppContext$JettyEmbeddedServletHandler.class */
    private static class JettyEmbeddedServletHandler extends ServletHandler {
        private JettyEmbeddedServletHandler() {
        }

        public void initialize() throws Exception {
        }

        public void deferredInitialize() throws Exception {
            super.initialize();
        }
    }
}