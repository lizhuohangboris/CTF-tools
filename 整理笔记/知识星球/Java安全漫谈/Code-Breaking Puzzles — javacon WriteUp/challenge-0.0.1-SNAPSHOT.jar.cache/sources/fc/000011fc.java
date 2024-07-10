package org.jboss.logging;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.jboss.logging.Logger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/JDKLogger.class */
final class JDKLogger extends Logger {
    private static final long serialVersionUID = 2563174097983721393L;
    private final transient java.util.logging.Logger logger;

    public JDKLogger(String name) {
        super(name);
        this.logger = java.util.logging.Logger.getLogger(name);
    }

    @Override // org.jboss.logging.Logger
    protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
        if (isEnabled(level)) {
            try {
                JBossLogRecord rec = new JBossLogRecord(translate(level), String.valueOf(message), loggerClassName);
                if (thrown != null) {
                    rec.setThrown(thrown);
                }
                rec.setLoggerName(getName());
                rec.setParameters(parameters);
                rec.setResourceBundleName(this.logger.getResourceBundleName());
                rec.setResourceBundle(this.logger.getResourceBundle());
                this.logger.log(rec);
            } catch (Throwable th) {
            }
        }
    }

    @Override // org.jboss.logging.Logger
    protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
        if (isEnabled(level)) {
            try {
                ResourceBundle resourceBundle = this.logger.getResourceBundle();
                if (resourceBundle != null) {
                    try {
                        format = resourceBundle.getString(format);
                    } catch (MissingResourceException e) {
                    }
                }
                String msg = parameters == null ? String.format(format, new Object[0]) : String.format(format, parameters);
                JBossLogRecord rec = new JBossLogRecord(translate(level), msg, loggerClassName);
                if (thrown != null) {
                    rec.setThrown(thrown);
                }
                rec.setLoggerName(getName());
                rec.setResourceBundleName(this.logger.getResourceBundleName());
                rec.setResourceBundle(null);
                rec.setParameters(null);
                this.logger.log(rec);
            } catch (Throwable th) {
            }
        }
    }

    private static Level translate(Logger.Level level) {
        if (level == Logger.Level.TRACE) {
            return JDKLevel.TRACE;
        }
        if (level == Logger.Level.DEBUG) {
            return JDKLevel.DEBUG;
        }
        return infoOrHigher(level);
    }

    private static Level infoOrHigher(Logger.Level level) {
        if (level == Logger.Level.INFO) {
            return JDKLevel.INFO;
        }
        if (level == Logger.Level.WARN) {
            return JDKLevel.WARN;
        }
        if (level == Logger.Level.ERROR) {
            return JDKLevel.ERROR;
        }
        if (level == Logger.Level.FATAL) {
            return JDKLevel.FATAL;
        }
        return JDKLevel.ALL;
    }

    @Override // org.jboss.logging.BasicLogger
    public boolean isEnabled(Logger.Level level) {
        return this.logger.isLoggable(translate(level));
    }
}