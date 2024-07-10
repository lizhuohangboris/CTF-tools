package org.springframework.boot.web.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/RegistrationBean.class */
public abstract class RegistrationBean implements ServletContextInitializer, Ordered {
    private static final Log logger = LogFactory.getLog(RegistrationBean.class);
    private int order = Integer.MAX_VALUE;
    private boolean enabled = true;

    protected abstract String getDescription();

    protected abstract void register(String description, ServletContext servletContext);

    @Override // org.springframework.boot.web.servlet.ServletContextInitializer
    public final void onStartup(ServletContext servletContext) throws ServletException {
        String description = getDescription();
        if (!isEnabled()) {
            logger.info(StringUtils.capitalize(description) + " was not registered (disabled)");
        } else {
            register(description, servletContext);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }
}