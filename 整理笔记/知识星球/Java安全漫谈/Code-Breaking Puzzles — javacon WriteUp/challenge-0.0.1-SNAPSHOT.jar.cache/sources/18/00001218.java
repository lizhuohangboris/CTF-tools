package org.jboss.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.text.MessageFormat;
import org.jboss.logging.Logger;
import org.slf4j.spi.LocationAwareLogger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/Slf4jLocationAwareLogger.class */
final class Slf4jLocationAwareLogger extends Logger {
    private static final long serialVersionUID = 8685757928087758380L;
    private static final Object[] EMPTY = new Object[0];
    private static final boolean POST_1_6;
    private static final Method LOG_METHOD;
    private final LocationAwareLogger logger;

    static {
        Method[] methods = LocationAwareLogger.class.getDeclaredMethods();
        Method logMethod = null;
        boolean post16 = false;
        for (Method method : methods) {
            if (method.getName().equals("log")) {
                logMethod = method;
                Class<?>[] parameterTypes = method.getParameterTypes();
                post16 = parameterTypes.length == 6;
            }
        }
        if (logMethod == null) {
            throw new NoSuchMethodError("Cannot find LocationAwareLogger.log() method");
        }
        POST_1_6 = post16;
        LOG_METHOD = logMethod;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Slf4jLocationAwareLogger(String name, LocationAwareLogger logger) {
        super(name);
        this.logger = logger;
    }

    @Override // org.jboss.logging.BasicLogger
    public boolean isEnabled(Logger.Level level) {
        if (level != null) {
            switch (level) {
                case FATAL:
                    return this.logger.isErrorEnabled();
                case ERROR:
                    return this.logger.isErrorEnabled();
                case WARN:
                    return this.logger.isWarnEnabled();
                case INFO:
                    return this.logger.isInfoEnabled();
                case DEBUG:
                    return this.logger.isDebugEnabled();
                case TRACE:
                    return this.logger.isTraceEnabled();
                default:
                    return true;
            }
        }
        return true;
    }

    @Override // org.jboss.logging.Logger
    protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
        if (isEnabled(level)) {
            String text = (parameters == null || parameters.length == 0) ? String.valueOf(message) : MessageFormat.format(String.valueOf(message), parameters);
            doLog(this.logger, loggerClassName, translate(level), text, thrown);
        }
    }

    @Override // org.jboss.logging.Logger
    protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
        if (isEnabled(level)) {
            String text = parameters == null ? String.format(format, new Object[0]) : String.format(format, parameters);
            doLog(this.logger, loggerClassName, translate(level), text, thrown);
        }
    }

    private static void doLog(LocationAwareLogger logger, String className, int level, String text, Throwable thrown) {
        try {
            if (POST_1_6) {
                LOG_METHOD.invoke(logger, null, className, Integer.valueOf(level), text, EMPTY, thrown);
            } else {
                LOG_METHOD.invoke(logger, null, className, Integer.valueOf(level), text, thrown);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        } catch (InvocationTargetException e2) {
            try {
                throw e2.getCause();
            } catch (Error er) {
                throw er;
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Throwable throwable) {
                throw new UndeclaredThrowableException(throwable);
            }
        }
    }

    private static int translate(Logger.Level level) {
        if (level != null) {
            switch (level) {
                case FATAL:
                case ERROR:
                    return 40;
                case WARN:
                    return 30;
                case INFO:
                    return 20;
                case DEBUG:
                    return 10;
                case TRACE:
                    return 0;
                default:
                    return 0;
            }
        }
        return 0;
    }
}