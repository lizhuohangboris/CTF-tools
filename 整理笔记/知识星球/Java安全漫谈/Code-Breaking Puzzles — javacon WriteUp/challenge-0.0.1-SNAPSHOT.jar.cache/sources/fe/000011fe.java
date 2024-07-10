package org.jboss.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.MessageFormatMessageFactory;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.jboss.logging.Logger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/Log4j2Logger.class */
final class Log4j2Logger extends Logger {
    private static final long serialVersionUID = -2507841068232627725L;
    private final AbstractLogger logger;
    private final MessageFormatMessageFactory messageFactory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Log4j2Logger(String name) {
        super(name);
        org.apache.logging.log4j.Logger logger = LogManager.getLogger(name);
        if (!(logger instanceof AbstractLogger)) {
            throw new LoggingException("The logger for [" + name + "] does not extend AbstractLogger. Actual logger: " + logger.getClass().getName());
        }
        this.logger = (AbstractLogger) logger;
        this.messageFactory = new MessageFormatMessageFactory();
    }

    @Override // org.jboss.logging.BasicLogger
    public boolean isEnabled(Logger.Level level) {
        return this.logger.isEnabled(translate(level));
    }

    @Override // org.jboss.logging.Logger
    protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
        Level translatedLevel = translate(level);
        if (this.logger.isEnabled(translatedLevel)) {
            try {
                this.logger.logMessage(loggerClassName, translatedLevel, (Marker) null, (parameters == null || parameters.length == 0) ? this.messageFactory.newMessage(message) : this.messageFactory.newMessage(String.valueOf(message), parameters), thrown);
            } catch (Throwable th) {
            }
        }
    }

    @Override // org.jboss.logging.Logger
    protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
        Level translatedLevel = translate(level);
        if (this.logger.isEnabled(translatedLevel)) {
            try {
                this.logger.logMessage(loggerClassName, translatedLevel, (Marker) null, (org.apache.logging.log4j.message.Message) new StringFormattedMessage(format, parameters), thrown);
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