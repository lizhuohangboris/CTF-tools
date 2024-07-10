package org.apache.juli.logging;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.LogManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/juli/logging/LogFactory.class */
public class LogFactory {
    private static final LogFactory singleton = new LogFactory();
    private final Constructor<? extends Log> discoveredLogConstructor;

    private LogFactory() {
        ServiceLoader<Log> logLoader = ServiceLoader.load(Log.class);
        Constructor<? extends Log> m = null;
        Iterator<Log> it = logLoader.iterator();
        if (it.hasNext()) {
            Log log = it.next();
            try {
                m = log.getClass().getConstructor(String.class);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new Error(e);
            }
        }
        this.discoveredLogConstructor = m;
    }

    public Log getInstance(String name) throws LogConfigurationException {
        if (this.discoveredLogConstructor == null) {
            return DirectJDKLog.getInstance(name);
        }
        try {
            return this.discoveredLogConstructor.newInstance(name);
        } catch (IllegalArgumentException | ReflectiveOperationException e) {
            throw new LogConfigurationException(e);
        }
    }

    public Log getInstance(Class<?> clazz) throws LogConfigurationException {
        return getInstance(clazz.getName());
    }

    public static LogFactory getFactory() throws LogConfigurationException {
        return singleton;
    }

    public static Log getLog(Class<?> clazz) throws LogConfigurationException {
        return getFactory().getInstance(clazz);
    }

    public static Log getLog(String name) throws LogConfigurationException {
        return getFactory().getInstance(name);
    }

    public static void release(ClassLoader classLoader) {
        if (!LogManager.getLogManager().getClass().getName().equals("java.util.logging.LogManager")) {
            LogManager.getLogManager().reset();
        }
    }
}