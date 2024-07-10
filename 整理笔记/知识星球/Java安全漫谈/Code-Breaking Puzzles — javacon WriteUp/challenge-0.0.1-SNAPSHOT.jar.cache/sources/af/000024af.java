package org.springframework.web.context.support;

import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.ServletContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.LiveBeansView;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/support/ServletContextLiveBeansView.class */
public class ServletContextLiveBeansView extends LiveBeansView {
    private final ServletContext servletContext;

    public ServletContextLiveBeansView(ServletContext servletContext) {
        Assert.notNull(servletContext, "ServletContext must not be null");
        this.servletContext = servletContext;
    }

    @Override // org.springframework.context.support.LiveBeansView
    protected Set<ConfigurableApplicationContext> findApplicationContexts() {
        Set<ConfigurableApplicationContext> contexts = new LinkedHashSet<>();
        Enumeration<String> attrNames = this.servletContext.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attrName = attrNames.nextElement();
            Object attrValue = this.servletContext.getAttribute(attrName);
            if (attrValue instanceof ConfigurableApplicationContext) {
                contexts.add((ConfigurableApplicationContext) attrValue);
            }
        }
        return contexts;
    }
}