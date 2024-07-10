package org.jboss.logging;

import java.util.logging.Level;
import org.jboss.logging.Logger;
import org.jboss.logmanager.ExtLogRecord;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/JBossLogManagerLogger.class */
final class JBossLogManagerLogger extends Logger {
    private static final long serialVersionUID = 7429618317727584742L;
    private final org.jboss.logmanager.Logger logger;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JBossLogManagerLogger(String name, org.jboss.logmanager.Logger logger) {
        super(name);
        this.logger = logger;
    }

    @Override // org.jboss.logging.BasicLogger
    public boolean isEnabled(Logger.Level level) {
        return this.logger.isLoggable(translate(level));
    }

    @Override // org.jboss.logging.Logger
    protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
        Level translatedLevel = translate(level);
        if (this.logger.isLoggable(translatedLevel)) {
            if (parameters == null) {
                this.logger.log(loggerClassName, translatedLevel, String.valueOf(message), thrown);
            } else {
                this.logger.log(loggerClassName, translatedLevel, String.valueOf(message), ExtLogRecord.FormatStyle.MESSAGE_FORMAT, parameters, thrown);
            }
        }
    }

    @Override // org.jboss.logging.Logger
    protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
        if (parameters == null) {
            this.logger.log(loggerClassName, translate(level), format, thrown);
        } else {
            this.logger.log(loggerClassName, translate(level), format, ExtLogRecord.FormatStyle.PRINTF, parameters, thrown);
        }
    }

    private static Level translate(Logger.Level level) {
        if (level == Logger.Level.TRACE) {
            return org.jboss.logmanager.Level.TRACE;
        }
        if (level == Logger.Level.DEBUG) {
            return org.jboss.logmanager.Level.DEBUG;
        }
        return infoOrHigher(level);
    }

    private static Level infoOrHigher(Logger.Level level) {
        if (level == Logger.Level.INFO) {
            return org.jboss.logmanager.Level.INFO;
        }
        if (level == Logger.Level.WARN) {
            return org.jboss.logmanager.Level.WARN;
        }
        if (level == Logger.Level.ERROR) {
            return org.jboss.logmanager.Level.ERROR;
        }
        if (level == Logger.Level.FATAL) {
            return org.jboss.logmanager.Level.FATAL;
        }
        return org.jboss.logmanager.Level.ALL;
    }
}