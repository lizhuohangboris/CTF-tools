package ch.qos.logback.classic.selector.servlet;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.selector.ContextSelector;
import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import ch.qos.logback.classic.util.JNDIUtil;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/selector/servlet/ContextDetachingSCL.class */
public class ContextDetachingSCL implements ServletContextListener {
    @Override // javax.servlet.ServletContextListener
    public void contextInitialized(ServletContextEvent arg0) {
    }

    @Override // javax.servlet.ServletContextListener
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        String loggerContextName = null;
        try {
            Context ctx = JNDIUtil.getInitialContext();
            loggerContextName = JNDIUtil.lookup(ctx, ClassicConstants.JNDI_CONTEXT_NAME);
        } catch (NamingException e) {
        }
        if (loggerContextName != null) {
            System.out.println("About to detach context named " + loggerContextName);
            ContextSelector selector = ContextSelectorStaticBinder.getSingleton().getContextSelector();
            if (selector == null) {
                System.out.println("Selector is null, cannot detach context. Skipping.");
                return;
            }
            LoggerContext context = selector.getLoggerContext(loggerContextName);
            if (context != null) {
                Logger logger = context.getLogger("ROOT");
                logger.warn("Stopping logger context " + loggerContextName);
                selector.detachLoggerContext(loggerContextName);
                context.stop();
                return;
            }
            System.out.println("No context named " + loggerContextName + " was found.");
        }
    }
}