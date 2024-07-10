package org.jboss.logging;

import java.text.MessageFormat;
import org.apache.log4j.Level;
import org.jboss.logging.Logger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/Log4jLogger.class */
final class Log4jLogger extends Logger {
    private static final long serialVersionUID = -5446154366955151335L;
    private final org.apache.log4j.Logger logger;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Log4jLogger(String name) {
        super(name);
        this.logger = org.apache.log4j.Logger.getLogger(name);
    }

    @Override // org.jboss.logging.BasicLogger
    public boolean isEnabled(Logger.Level level) {
        Level l = translate(level);
        return this.logger.isEnabledFor(l) && l.isGreaterOrEqual(this.logger.getEffectiveLevel());
    }

    @Override // org.jboss.logging.Logger
    protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
        Level translatedLevel = translate(level);
        if (this.logger.isEnabledFor(translatedLevel)) {
            try {
                this.logger.log(loggerClassName, translatedLevel, (parameters == null || parameters.length == 0) ? message : MessageFormat.format(String.valueOf(message), parameters), thrown);
            } catch (Throwable th) {
            }
        }
    }

    @Override // org.jboss.logging.Logger
    protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
        Level translatedLevel = translate(level);
        if (this.logger.isEnabledFor(translatedLevel)) {
            try {
                this.logger.log(loggerClassName, translatedLevel, parameters == null ? String.format(format, new Object[0]) : String.format(format, parameters), thrown);
            } catch (Throwable th) {
            }
        }
    }

    private static Level translate(Logger.Level level) {
        if (level == Logger.Level.TRACE) {
            return Level.TRACE;
        }
        if (level == Logger.Level.DEBUG) {
            return Level.DEBUG;
        }
        return infoOrHigher(level);
    }

    private static Level infoOrHigher(Logger.Level level) {
        if (level == Logger.Level.INFO) {
            return Level.INFO;
        }
        if (level == Logger.Level.WARN) {
            return Level.WARN;
        }
        if (level == Logger.Level.ERROR) {
            return Level.ERROR;
        }
        if (level == Logger.Level.FATAL) {
            return Level.FATAL;
        }
        return Level.ALL;
    }
}