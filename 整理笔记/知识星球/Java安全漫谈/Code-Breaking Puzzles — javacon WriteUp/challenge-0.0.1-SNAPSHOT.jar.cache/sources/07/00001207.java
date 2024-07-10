package org.jboss.logging;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.logging.LogManager;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/LoggerProviders.class */
public final class LoggerProviders {
    static final String LOGGING_PROVIDER_KEY = "org.jboss.logging.provider";
    static final LoggerProvider PROVIDER = find();

    private static LoggerProvider find() {
        return findProvider();
    }

    private static LoggerProvider findProvider() {
        ClassLoader cl = LoggerProviders.class.getClassLoader();
        try {
            String loggerProvider = (String) AccessController.doPrivileged(new PrivilegedAction<String>() { // from class: org.jboss.logging.LoggerProviders.1
                @Override // java.security.PrivilegedAction
                public String run() {
                    return System.getProperty(LoggerProviders.LOGGING_PROVIDER_KEY);
                }
            });
            if (loggerProvider != null) {
                if ("jboss".equalsIgnoreCase(loggerProvider)) {
                    return tryJBossLogManager(cl, "system property");
                }
                if ("jdk".equalsIgnoreCase(loggerProvider)) {
                    return tryJDK("system property");
                }
                if ("log4j2".equalsIgnoreCase(loggerProvider)) {
                    return tryLog4j2(cl, "system property");
                }
                if ("log4j".equalsIgnoreCase(loggerProvider)) {
                    return tryLog4j(cl, "system property");
                }
                if ("slf4j".equalsIgnoreCase(loggerProvider)) {
                    return trySlf4j("system property");
                }
            }
        } catch (Throwable th) {
        }
        try {
            ServiceLoader<LoggerProvider> loader = ServiceLoader.load(LoggerProvider.class, cl);
            Iterator<LoggerProvider> iter = loader.iterator();
            while (iter.hasNext()) {
                try {
                    LoggerProvider provider = iter.next();
                    logProvider(provider, "service loader");
                    return provider;
                } catch (ServiceConfigurationError e) {
                }
            }
        } catch (Throwable th2) {
        }
        try {
            return tryJBossLogManager(cl, null);
        } catch (Throwable th3) {
            try {
                return tryLog4j2(cl, null);
            } catch (Throwable th4) {
                try {
                    return tryLog4j(cl, null);
                } catch (Throwable th5) {
                    try {
                        Class.forName("ch.qos.logback.classic.Logger", false, cl);
                        return trySlf4j(null);
                    } catch (Throwable th6) {
                        return tryJDK(null);
                    }
                }
            }
        }
    }

    private static JDKLoggerProvider tryJDK(String via) {
        JDKLoggerProvider provider = new JDKLoggerProvider();
        logProvider(provider, via);
        return provider;
    }

    private static LoggerProvider trySlf4j(String via) {
        LoggerProvider provider = new Slf4jLoggerProvider();
        logProvider(provider, via);
        return provider;
    }

    private static LoggerProvider tryLog4j2(ClassLoader cl, String via) throws ClassNotFoundException {
        Class.forName("org.apache.logging.log4j.Logger", true, cl);
        Class.forName("org.apache.logging.log4j.LogManager", true, cl);
        Class.forName("org.apache.logging.log4j.spi.AbstractLogger", true, cl);
        LoggerProvider provider = new Log4j2LoggerProvider();
        logProvider(provider, via);
        return provider;
    }

    private static LoggerProvider tryLog4j(ClassLoader cl, String via) throws ClassNotFoundException {
        Class.forName("org.apache.log4j.LogManager", true, cl);
        Class.forName("org.apache.log4j.config.PropertySetter", true, cl);
        LoggerProvider provider = new Log4jLoggerProvider();
        logProvider(provider, via);
        return provider;
    }

    private static LoggerProvider tryJBossLogManager(ClassLoader cl, String via) throws ClassNotFoundException {
        Class<?> cls = LogManager.getLogManager().getClass();
        if (cls == Class.forName("org.jboss.logmanager.LogManager", false, cl) && Class.forName("org.jboss.logmanager.Logger$AttachmentKey", true, cl).getClassLoader() == cls.getClassLoader()) {
            LoggerProvider provider = new JBossLogManagerProvider();
            logProvider(provider, via);
            return provider;
        }
        throw new IllegalStateException();
    }

    private static void logProvider(LoggerProvider provider, String via) {
        Logger logger = provider.getLogger(LoggerProviders.class.getPackage().getName());
        if (via == null) {
            logger.debugf("Logging Provider: %s", provider.getClass().getName());
        } else {
            logger.debugf("Logging Provider: %s found via %s", provider.getClass().getName(), via);
        }
    }

    private LoggerProviders() {
    }
}