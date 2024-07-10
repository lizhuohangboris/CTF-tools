package org.springframework.web.context.support;

import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/support/SpringBeanAutowiringSupport.class */
public abstract class SpringBeanAutowiringSupport {
    private static final Log logger = LogFactory.getLog(SpringBeanAutowiringSupport.class);

    public SpringBeanAutowiringSupport() {
        processInjectionBasedOnCurrentContext(this);
    }

    public static void processInjectionBasedOnCurrentContext(Object target) {
        Assert.notNull(target, "Target object must not be null");
        WebApplicationContext cc = ContextLoader.getCurrentWebApplicationContext();
        if (cc != null) {
            AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
            bpp.setBeanFactory(cc.getAutowireCapableBeanFactory());
            bpp.processInjection(target);
        } else if (logger.isDebugEnabled()) {
            logger.debug("Current WebApplicationContext is not available for processing of " + ClassUtils.getShortName(target.getClass()) + ": Make sure this class gets constructed in a Spring web application. Proceeding without injection.");
        }
    }

    public static void processInjectionBasedOnServletContext(Object target, ServletContext servletContext) {
        Assert.notNull(target, "Target object must not be null");
        WebApplicationContext cc = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(cc.getAutowireCapableBeanFactory());
        bpp.processInjection(target);
    }
}