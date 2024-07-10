package org.springframework.web.context.support;

import java.io.File;
import javax.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.WebUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/support/WebApplicationObjectSupport.class */
public abstract class WebApplicationObjectSupport extends ApplicationObjectSupport implements ServletContextAware {
    @Nullable
    private ServletContext servletContext;

    @Override // org.springframework.web.context.ServletContextAware
    public final void setServletContext(ServletContext servletContext) {
        if (servletContext != this.servletContext) {
            this.servletContext = servletContext;
            initServletContext(servletContext);
        }
    }

    @Override // org.springframework.context.support.ApplicationObjectSupport
    protected boolean isContextRequired() {
        return true;
    }

    @Override // org.springframework.context.support.ApplicationObjectSupport
    public void initApplicationContext(ApplicationContext context) {
        super.initApplicationContext(context);
        if (this.servletContext == null && (context instanceof WebApplicationContext)) {
            this.servletContext = ((WebApplicationContext) context).getServletContext();
            if (this.servletContext != null) {
                initServletContext(this.servletContext);
            }
        }
    }

    public void initServletContext(ServletContext servletContext) {
    }

    @Nullable
    public final WebApplicationContext getWebApplicationContext() throws IllegalStateException {
        ApplicationContext ctx = getApplicationContext();
        if (ctx instanceof WebApplicationContext) {
            return (WebApplicationContext) getApplicationContext();
        }
        if (isContextRequired()) {
            throw new IllegalStateException("WebApplicationObjectSupport instance [" + this + "] does not run in a WebApplicationContext but in: " + ctx);
        }
        return null;
    }

    @Nullable
    public final ServletContext getServletContext() throws IllegalStateException {
        if (this.servletContext != null) {
            return this.servletContext;
        }
        ServletContext servletContext = null;
        WebApplicationContext wac = getWebApplicationContext();
        if (wac != null) {
            servletContext = wac.getServletContext();
        }
        if (servletContext == null && isContextRequired()) {
            throw new IllegalStateException("WebApplicationObjectSupport instance [" + this + "] does not run within a ServletContext. Make sure the object is fully configured!");
        }
        return servletContext;
    }

    protected final File getTempDir() throws IllegalStateException {
        ServletContext servletContext = getServletContext();
        Assert.state(servletContext != null, "ServletContext is required");
        return WebUtils.getTempDir(servletContext);
    }
}