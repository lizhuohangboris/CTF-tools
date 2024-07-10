package ch.qos.logback.classic.servlet;

import ch.qos.logback.classic.util.StatusViaSLF4JLoggerFactory;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/servlet/LogbackServletContainerInitializer.class */
public class LogbackServletContainerInitializer implements ServletContainerInitializer {
    @Override // javax.servlet.ServletContainerInitializer
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        if (isDisabledByConfiguration(ctx)) {
            StatusViaSLF4JLoggerFactory.addInfo("Due to deployment instructions will NOT register an instance of " + LogbackServletContextListener.class + " to the current web-app", this);
            return;
        }
        StatusViaSLF4JLoggerFactory.addInfo("Adding an instance of  " + LogbackServletContextListener.class + " to the current web-app", this);
        LogbackServletContextListener lscl = new LogbackServletContextListener();
        ctx.addListener((ServletContext) lscl);
    }

    boolean isDisabledByConfiguration(ServletContext ctx) {
        String disableAttributeStr = null;
        Object disableAttribute = ctx.getInitParameter(CoreConstants.DISABLE_SERVLET_CONTAINER_INITIALIZER_KEY);
        if (disableAttribute instanceof String) {
            disableAttributeStr = (String) disableAttribute;
        }
        if (OptionHelper.isEmpty(disableAttributeStr)) {
            disableAttributeStr = OptionHelper.getSystemProperty(CoreConstants.DISABLE_SERVLET_CONTAINER_INITIALIZER_KEY);
        }
        if (OptionHelper.isEmpty(disableAttributeStr)) {
            disableAttributeStr = OptionHelper.getEnv(CoreConstants.DISABLE_SERVLET_CONTAINER_INITIALIZER_KEY);
        }
        if (OptionHelper.isEmpty(disableAttributeStr)) {
            return false;
        }
        return disableAttributeStr.equalsIgnoreCase("true");
    }
}