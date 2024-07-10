package org.apache.logging.slf4j;

import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerRegistry;
import org.slf4j.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-to-slf4j-2.11.1.jar:org/apache/logging/slf4j/SLF4JLoggerContext.class */
public class SLF4JLoggerContext implements LoggerContext {
    private final LoggerRegistry<ExtendedLogger> loggerRegistry = new LoggerRegistry<>();

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public Object getExternalContext() {
        return null;
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public ExtendedLogger getLogger(String name) {
        if (!this.loggerRegistry.hasLogger(name)) {
            this.loggerRegistry.putIfAbsent(name, null, new SLF4JLogger(name, LoggerFactory.getLogger(name)));
        }
        return this.loggerRegistry.getLogger(name);
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public ExtendedLogger getLogger(String name, MessageFactory messageFactory) {
        if (!this.loggerRegistry.hasLogger(name)) {
            this.loggerRegistry.putIfAbsent(name, null, new SLF4JLogger(name, messageFactory, LoggerFactory.getLogger(name)));
        }
        return this.loggerRegistry.getLogger(name);
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public boolean hasLogger(String name) {
        return this.loggerRegistry.hasLogger(name);
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public boolean hasLogger(String name, MessageFactory messageFactory) {
        return this.loggerRegistry.hasLogger(name, messageFactory);
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public boolean hasLogger(String name, Class<? extends MessageFactory> messageFactoryClass) {
        return this.loggerRegistry.hasLogger(name, messageFactoryClass);
    }
}