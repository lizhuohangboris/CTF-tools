package org.apache.logging.log4j.simple;

import java.net.URI;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/simple/SimpleLoggerContextFactory.class */
public class SimpleLoggerContextFactory implements LoggerContextFactory {
    private static LoggerContext context = new SimpleLoggerContext();

    @Override // org.apache.logging.log4j.spi.LoggerContextFactory
    public LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext) {
        return context;
    }

    @Override // org.apache.logging.log4j.spi.LoggerContextFactory
    public LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext, URI configLocation, String name) {
        return context;
    }

    @Override // org.apache.logging.log4j.spi.LoggerContextFactory
    public void removeContext(LoggerContext removeContext) {
    }
}