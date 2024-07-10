package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.message.MessageFactory;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/LoggerContextKey.class */
public class LoggerContextKey {
    public static String create(String name) {
        return create(name, AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS);
    }

    public static String create(String name, MessageFactory messageFactory) {
        return create(name, (Class<? extends MessageFactory>) (messageFactory != null ? messageFactory.getClass() : AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS));
    }

    public static String create(String name, Class<? extends MessageFactory> messageFactoryClass) {
        Class<? extends MessageFactory> mfClass = messageFactoryClass != null ? messageFactoryClass : AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS;
        return name + "." + mfClass.getName();
    }
}