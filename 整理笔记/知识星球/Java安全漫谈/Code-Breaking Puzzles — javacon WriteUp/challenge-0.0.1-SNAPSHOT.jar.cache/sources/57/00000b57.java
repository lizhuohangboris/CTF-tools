package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.message.MessageFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/LoggerContext.class */
public interface LoggerContext {
    Object getExternalContext();

    ExtendedLogger getLogger(String str);

    ExtendedLogger getLogger(String str, MessageFactory messageFactory);

    boolean hasLogger(String str);

    boolean hasLogger(String str, MessageFactory messageFactory);

    boolean hasLogger(String str, Class<? extends MessageFactory> cls);
}