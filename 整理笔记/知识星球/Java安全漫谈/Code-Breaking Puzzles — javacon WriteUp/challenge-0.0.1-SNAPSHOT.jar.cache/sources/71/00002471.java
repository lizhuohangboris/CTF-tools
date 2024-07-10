package org.springframework.web.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.lang.Nullable;
import org.springframework.web.WebApplicationInitializer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/AbstractContextLoaderInitializer.class */
public abstract class AbstractContextLoaderInitializer implements WebApplicationInitializer {
    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    protected abstract WebApplicationContext createRootApplicationContext();

    @Override // org.springframework.web.WebApplicationInitializer
    public void onStartup(ServletContext servletContext) throws ServletException {
        registerContextLoaderListener(servletContext);
    }

    protected void registerContextLoaderListener(ServletContext servletContext) {
        WebApplicationContext rootAppContext = createRootApplicationContext();
        if (rootAppContext != null) {
            ContextLoaderListener listener = new ContextLoaderListener(rootAppContext);
            listener.setContextInitializers(getRootApplicationContextInitializers());
            servletContext.addListener((ServletContext) listener);
            return;
        }
        this.logger.debug("No ContextLoaderListener registered, as createRootApplicationContext() did not return an application context");
    }

    @Nullable
    protected ApplicationContextInitializer<?>[] getRootApplicationContextInitializers() {
        return null;
    }
}