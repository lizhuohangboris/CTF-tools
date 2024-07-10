package org.apache.logging.slf4j;

import java.net.URI;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-to-slf4j-2.11.1.jar:org/apache/logging/slf4j/SLF4JLoggerContextFactory.class */
public class SLF4JLoggerContextFactory implements LoggerContextFactory {
    private static final StatusLogger LOGGER = StatusLogger.getLogger();
    private static LoggerContext context = new SLF4JLoggerContext();

    public SLF4JLoggerContextFactory() {
        boolean misconfigured = false;
        try {
            LoaderUtil.loadClass("org.slf4j.helpers.Log4jLoggerFactory");
            misconfigured = true;
        } catch (ClassNotFoundException e) {
            LOGGER.debug("org.slf4j.helpers.Log4jLoggerFactory is not on classpath. Good!");
        }
        if (misconfigured) {
            throw new IllegalStateException("slf4j-impl jar is mutually exclusive with log4j-to-slf4j jar (the first routes calls from SLF4J to Log4j, the second from Log4j to SLF4J)");
        }
    }

    @Override // org.apache.logging.log4j.spi.LoggerContextFactory
    public LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext) {
        return context;
    }

    @Override // org.apache.logging.log4j.spi.LoggerContextFactory
    public LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext, URI configLocation, String name) {
        return context;
    }

    @Override // org.apache.logging.log4j.spi.LoggerContextFactory
    public void removeContext(LoggerContext ignored) {
    }
}