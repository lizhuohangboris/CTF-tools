package org.jboss.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/JBossLogRecord.class */
class JBossLogRecord extends LogRecord {
    private static final long serialVersionUID = 2492784413065296060L;
    private static final String LOGGER_CLASS_NAME = Logger.class.getName();
    private boolean resolved;
    private final String loggerClassName;

    JBossLogRecord(Level level, String msg) {
        super(level, msg);
        this.loggerClassName = LOGGER_CLASS_NAME;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JBossLogRecord(Level level, String msg, String loggerClassName) {
        super(level, msg);
        this.loggerClassName = loggerClassName;
    }

    @Override // java.util.logging.LogRecord
    public String getSourceClassName() {
        if (!this.resolved) {
            resolve();
        }
        return super.getSourceClassName();
    }

    @Override // java.util.logging.LogRecord
    public void setSourceClassName(String sourceClassName) {
        this.resolved = true;
        super.setSourceClassName(sourceClassName);
    }

    @Override // java.util.logging.LogRecord
    public String getSourceMethodName() {
        if (!this.resolved) {
            resolve();
        }
        return super.getSourceMethodName();
    }

    @Override // java.util.logging.LogRecord
    public void setSourceMethodName(String sourceMethodName) {
        this.resolved = true;
        super.setSourceMethodName(sourceMethodName);
    }

    private void resolve() {
        this.resolved = true;
        StackTraceElement[] stack = new Throwable().getStackTrace();
        boolean found = false;
        for (StackTraceElement element : stack) {
            String className = element.getClassName();
            if (found) {
                if (!this.loggerClassName.equals(className)) {
                    setSourceClassName(className);
                    setSourceMethodName(element.getMethodName());
                    return;
                }
            } else {
                found = this.loggerClassName.equals(className);
            }
        }
        setSourceClassName("<unknown>");
        setSourceMethodName("<unknown>");
    }

    protected Object writeReplace() {
        LogRecord replacement = new LogRecord(getLevel(), getMessage());
        replacement.setResourceBundle(getResourceBundle());
        replacement.setLoggerName(getLoggerName());
        replacement.setMillis(getMillis());
        replacement.setParameters(getParameters());
        replacement.setResourceBundleName(getResourceBundleName());
        replacement.setSequenceNumber(getSequenceNumber());
        replacement.setSourceClassName(getSourceClassName());
        replacement.setSourceMethodName(getSourceMethodName());
        replacement.setThreadID(getThreadID());
        replacement.setThrown(getThrown());
        return replacement;
    }
}