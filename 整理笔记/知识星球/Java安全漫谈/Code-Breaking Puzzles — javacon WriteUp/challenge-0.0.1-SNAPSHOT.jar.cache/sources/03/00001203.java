package org.jboss.logging;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/Logger.class */
public abstract class Logger implements Serializable, BasicLogger {
    private static final long serialVersionUID = 4232175575988879434L;
    private static final String FQCN = Logger.class.getName();
    private final String name;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/Logger$Level.class */
    public enum Level {
        FATAL,
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE
    }

    protected abstract void doLog(Level level, String str, Object obj, Object[] objArr, Throwable th);

    protected abstract void doLogf(Level level, String str, String str2, Object[] objArr, Throwable th);

    /* JADX INFO: Access modifiers changed from: protected */
    public Logger(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override // org.jboss.logging.BasicLogger
    public boolean isTraceEnabled() {
        return isEnabled(Level.TRACE);
    }

    @Override // org.jboss.logging.BasicLogger
    public void trace(Object message) {
        doLog(Level.TRACE, FQCN, message, null, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void trace(Object message, Throwable t) {
        doLog(Level.TRACE, FQCN, message, null, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void trace(String loggerFqcn, Object message, Throwable t) {
        doLog(Level.TRACE, loggerFqcn, message, null, t);
    }

    @Deprecated
    public void trace(Object message, Object[] params) {
        doLog(Level.TRACE, FQCN, message, params, null);
    }

    @Deprecated
    public void trace(Object message, Object[] params, Throwable t) {
        doLog(Level.TRACE, FQCN, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void trace(String loggerFqcn, Object message, Object[] params, Throwable t) {
        doLog(Level.TRACE, loggerFqcn, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(String format, Object... params) {
        doLog(Level.TRACE, FQCN, format, params, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(String format, Object param1) {
        if (isEnabled(Level.TRACE)) {
            doLog(Level.TRACE, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(String format, Object param1, Object param2) {
        if (isEnabled(Level.TRACE)) {
            doLog(Level.TRACE, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.TRACE)) {
            doLog(Level.TRACE, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(Throwable t, String format, Object... params) {
        doLog(Level.TRACE, FQCN, format, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(Throwable t, String format, Object param1) {
        if (isEnabled(Level.TRACE)) {
            doLog(Level.TRACE, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.TRACE)) {
            doLog(Level.TRACE, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.TRACE)) {
            doLog(Level.TRACE, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, Object... params) {
        doLogf(Level.TRACE, FQCN, format, params, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, Object param1) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, Object param1, Object param2) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, Object... params) {
        doLogf(Level.TRACE, FQCN, format, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, Object param1) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, int arg) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Integer.valueOf(arg)}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, int arg1, int arg2) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Integer.valueOf(arg1), Integer.valueOf(arg2)}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, int arg1, Object arg2) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Integer.valueOf(arg1), arg2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, int arg1, int arg2, int arg3) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Integer.valueOf(arg1), Integer.valueOf(arg2), Integer.valueOf(arg3)}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, int arg1, int arg2, Object arg3) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Integer.valueOf(arg1), Integer.valueOf(arg2), arg3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, int arg1, Object arg2, Object arg3) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Integer.valueOf(arg1), arg2, arg3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, int arg) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Integer.valueOf(arg)}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, int arg1, int arg2) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Integer.valueOf(arg1), Integer.valueOf(arg2)}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, int arg1, Object arg2) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Integer.valueOf(arg1), arg2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, int arg1, int arg2, int arg3) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Integer.valueOf(arg1), Integer.valueOf(arg2), Integer.valueOf(arg3)}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, int arg1, int arg2, Object arg3) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Integer.valueOf(arg1), Integer.valueOf(arg2), arg3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, int arg1, Object arg2, Object arg3) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Integer.valueOf(arg1), arg2, arg3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, long arg) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Long.valueOf(arg)}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, long arg1, long arg2) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Long.valueOf(arg1), Long.valueOf(arg2)}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, long arg1, Object arg2) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Long.valueOf(arg1), arg2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, long arg1, long arg2, long arg3) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Long.valueOf(arg1), Long.valueOf(arg2), Long.valueOf(arg3)}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, long arg1, long arg2, Object arg3) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Long.valueOf(arg1), Long.valueOf(arg2), arg3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, long arg1, Object arg2, Object arg3) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Long.valueOf(arg1), arg2, arg3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, long arg) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Long.valueOf(arg)}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, long arg1, long arg2) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Long.valueOf(arg1), Long.valueOf(arg2)}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, long arg1, Object arg2) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Long.valueOf(arg1), arg2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, long arg1, long arg2, long arg3) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Long.valueOf(arg1), Long.valueOf(arg2), Long.valueOf(arg3)}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, long arg1, long arg2, Object arg3) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Long.valueOf(arg1), Long.valueOf(arg2), arg3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, long arg1, Object arg2, Object arg3) {
        if (isEnabled(Level.TRACE)) {
            doLogf(Level.TRACE, FQCN, format, new Object[]{Long.valueOf(arg1), arg2, arg3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public boolean isDebugEnabled() {
        return isEnabled(Level.DEBUG);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debug(Object message) {
        doLog(Level.DEBUG, FQCN, message, null, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debug(Object message, Throwable t) {
        doLog(Level.DEBUG, FQCN, message, null, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debug(String loggerFqcn, Object message, Throwable t) {
        doLog(Level.DEBUG, loggerFqcn, message, null, t);
    }

    @Deprecated
    public void debug(Object message, Object[] params) {
        doLog(Level.DEBUG, FQCN, message, params, null);
    }

    @Deprecated
    public void debug(Object message, Object[] params, Throwable t) {
        doLog(Level.DEBUG, FQCN, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debug(String loggerFqcn, Object message, Object[] params, Throwable t) {
        doLog(Level.DEBUG, loggerFqcn, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(String format, Object... params) {
        doLog(Level.DEBUG, FQCN, format, params, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(String format, Object param1) {
        if (isEnabled(Level.DEBUG)) {
            doLog(Level.DEBUG, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(String format, Object param1, Object param2) {
        if (isEnabled(Level.DEBUG)) {
            doLog(Level.DEBUG, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.DEBUG)) {
            doLog(Level.DEBUG, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(Throwable t, String format, Object... params) {
        doLog(Level.DEBUG, FQCN, format, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(Throwable t, String format, Object param1) {
        if (isEnabled(Level.DEBUG)) {
            doLog(Level.DEBUG, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.DEBUG)) {
            doLog(Level.DEBUG, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.DEBUG)) {
            doLog(Level.DEBUG, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, Object... params) {
        doLogf(Level.DEBUG, FQCN, format, params, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, Object param1) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, Object param1, Object param2) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, Object... params) {
        doLogf(Level.DEBUG, FQCN, format, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, Object param1) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, int arg) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Integer.valueOf(arg)}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, int arg1, int arg2) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Integer.valueOf(arg1), Integer.valueOf(arg2)}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, int arg1, Object arg2) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Integer.valueOf(arg1), arg2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, int arg1, int arg2, int arg3) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Integer.valueOf(arg1), Integer.valueOf(arg2), Integer.valueOf(arg3)}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, int arg1, int arg2, Object arg3) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Integer.valueOf(arg1), Integer.valueOf(arg2), arg3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, int arg1, Object arg2, Object arg3) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Integer.valueOf(arg1), arg2, arg3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, int arg) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Integer.valueOf(arg)}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, int arg1, int arg2) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Integer.valueOf(arg1), Integer.valueOf(arg2)}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, int arg1, Object arg2) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Integer.valueOf(arg1), arg2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, int arg1, int arg2, int arg3) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Integer.valueOf(arg1), Integer.valueOf(arg2), Integer.valueOf(arg3)}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, int arg1, int arg2, Object arg3) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Integer.valueOf(arg1), Integer.valueOf(arg2), arg3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, int arg1, Object arg2, Object arg3) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Integer.valueOf(arg1), arg2, arg3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, long arg) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Long.valueOf(arg)}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, long arg1, long arg2) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Long.valueOf(arg1), Long.valueOf(arg2)}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, long arg1, Object arg2) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Long.valueOf(arg1), arg2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, long arg1, long arg2, long arg3) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Long.valueOf(arg1), Long.valueOf(arg2), Long.valueOf(arg3)}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, long arg1, long arg2, Object arg3) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Long.valueOf(arg1), Long.valueOf(arg2), arg3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, long arg1, Object arg2, Object arg3) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Long.valueOf(arg1), arg2, arg3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, long arg) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Long.valueOf(arg)}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, long arg1, long arg2) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Long.valueOf(arg1), Long.valueOf(arg2)}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, long arg1, Object arg2) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Long.valueOf(arg1), arg2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, long arg1, long arg2, long arg3) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Long.valueOf(arg1), Long.valueOf(arg2), Long.valueOf(arg3)}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, long arg1, long arg2, Object arg3) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Long.valueOf(arg1), Long.valueOf(arg2), arg3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, long arg1, Object arg2, Object arg3) {
        if (isEnabled(Level.DEBUG)) {
            doLogf(Level.DEBUG, FQCN, format, new Object[]{Long.valueOf(arg1), arg2, arg3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public boolean isInfoEnabled() {
        return isEnabled(Level.INFO);
    }

    @Override // org.jboss.logging.BasicLogger
    public void info(Object message) {
        doLog(Level.INFO, FQCN, message, null, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void info(Object message, Throwable t) {
        doLog(Level.INFO, FQCN, message, null, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void info(String loggerFqcn, Object message, Throwable t) {
        doLog(Level.INFO, loggerFqcn, message, null, t);
    }

    @Deprecated
    public void info(Object message, Object[] params) {
        doLog(Level.INFO, FQCN, message, params, null);
    }

    @Deprecated
    public void info(Object message, Object[] params, Throwable t) {
        doLog(Level.INFO, FQCN, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void info(String loggerFqcn, Object message, Object[] params, Throwable t) {
        doLog(Level.INFO, loggerFqcn, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(String format, Object... params) {
        doLog(Level.INFO, FQCN, format, params, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(String format, Object param1) {
        if (isEnabled(Level.INFO)) {
            doLog(Level.INFO, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(String format, Object param1, Object param2) {
        if (isEnabled(Level.INFO)) {
            doLog(Level.INFO, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.INFO)) {
            doLog(Level.INFO, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(Throwable t, String format, Object... params) {
        doLog(Level.INFO, FQCN, format, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(Throwable t, String format, Object param1) {
        if (isEnabled(Level.INFO)) {
            doLog(Level.INFO, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.INFO)) {
            doLog(Level.INFO, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.INFO)) {
            doLog(Level.INFO, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(String format, Object... params) {
        doLogf(Level.INFO, FQCN, format, params, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(String format, Object param1) {
        if (isEnabled(Level.INFO)) {
            doLogf(Level.INFO, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(String format, Object param1, Object param2) {
        if (isEnabled(Level.INFO)) {
            doLogf(Level.INFO, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.INFO)) {
            doLogf(Level.INFO, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(Throwable t, String format, Object... params) {
        doLogf(Level.INFO, FQCN, format, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(Throwable t, String format, Object param1) {
        if (isEnabled(Level.INFO)) {
            doLogf(Level.INFO, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.INFO)) {
            doLogf(Level.INFO, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.INFO)) {
            doLogf(Level.INFO, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void warn(Object message) {
        doLog(Level.WARN, FQCN, message, null, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warn(Object message, Throwable t) {
        doLog(Level.WARN, FQCN, message, null, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warn(String loggerFqcn, Object message, Throwable t) {
        doLog(Level.WARN, loggerFqcn, message, null, t);
    }

    @Deprecated
    public void warn(Object message, Object[] params) {
        doLog(Level.WARN, FQCN, message, params, null);
    }

    @Deprecated
    public void warn(Object message, Object[] params, Throwable t) {
        doLog(Level.WARN, FQCN, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warn(String loggerFqcn, Object message, Object[] params, Throwable t) {
        doLog(Level.WARN, loggerFqcn, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(String format, Object... params) {
        doLog(Level.WARN, FQCN, format, params, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(String format, Object param1) {
        if (isEnabled(Level.WARN)) {
            doLog(Level.WARN, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(String format, Object param1, Object param2) {
        if (isEnabled(Level.WARN)) {
            doLog(Level.WARN, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.WARN)) {
            doLog(Level.WARN, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(Throwable t, String format, Object... params) {
        doLog(Level.WARN, FQCN, format, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(Throwable t, String format, Object param1) {
        if (isEnabled(Level.WARN)) {
            doLog(Level.WARN, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.WARN)) {
            doLog(Level.WARN, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.WARN)) {
            doLog(Level.WARN, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(String format, Object... params) {
        doLogf(Level.WARN, FQCN, format, params, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(String format, Object param1) {
        if (isEnabled(Level.WARN)) {
            doLogf(Level.WARN, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(String format, Object param1, Object param2) {
        if (isEnabled(Level.WARN)) {
            doLogf(Level.WARN, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.WARN)) {
            doLogf(Level.WARN, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(Throwable t, String format, Object... params) {
        doLogf(Level.WARN, FQCN, format, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(Throwable t, String format, Object param1) {
        if (isEnabled(Level.WARN)) {
            doLogf(Level.WARN, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.WARN)) {
            doLogf(Level.WARN, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.WARN)) {
            doLogf(Level.WARN, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void error(Object message) {
        doLog(Level.ERROR, FQCN, message, null, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void error(Object message, Throwable t) {
        doLog(Level.ERROR, FQCN, message, null, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void error(String loggerFqcn, Object message, Throwable t) {
        doLog(Level.ERROR, loggerFqcn, message, null, t);
    }

    @Deprecated
    public void error(Object message, Object[] params) {
        doLog(Level.ERROR, FQCN, message, params, null);
    }

    @Deprecated
    public void error(Object message, Object[] params, Throwable t) {
        doLog(Level.ERROR, FQCN, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void error(String loggerFqcn, Object message, Object[] params, Throwable t) {
        doLog(Level.ERROR, loggerFqcn, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(String format, Object... params) {
        doLog(Level.ERROR, FQCN, format, params, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(String format, Object param1) {
        if (isEnabled(Level.ERROR)) {
            doLog(Level.ERROR, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(String format, Object param1, Object param2) {
        if (isEnabled(Level.ERROR)) {
            doLog(Level.ERROR, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.ERROR)) {
            doLog(Level.ERROR, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(Throwable t, String format, Object... params) {
        doLog(Level.ERROR, FQCN, format, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(Throwable t, String format, Object param1) {
        if (isEnabled(Level.ERROR)) {
            doLog(Level.ERROR, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.ERROR)) {
            doLog(Level.ERROR, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.ERROR)) {
            doLog(Level.ERROR, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(String format, Object... params) {
        doLogf(Level.ERROR, FQCN, format, params, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(String format, Object param1) {
        if (isEnabled(Level.ERROR)) {
            doLogf(Level.ERROR, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(String format, Object param1, Object param2) {
        if (isEnabled(Level.ERROR)) {
            doLogf(Level.ERROR, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.ERROR)) {
            doLogf(Level.ERROR, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(Throwable t, String format, Object... params) {
        doLogf(Level.ERROR, FQCN, format, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(Throwable t, String format, Object param1) {
        if (isEnabled(Level.ERROR)) {
            doLogf(Level.ERROR, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.ERROR)) {
            doLogf(Level.ERROR, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.ERROR)) {
            doLogf(Level.ERROR, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatal(Object message) {
        doLog(Level.FATAL, FQCN, message, null, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatal(Object message, Throwable t) {
        doLog(Level.FATAL, FQCN, message, null, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatal(String loggerFqcn, Object message, Throwable t) {
        doLog(Level.FATAL, loggerFqcn, message, null, t);
    }

    @Deprecated
    public void fatal(Object message, Object[] params) {
        doLog(Level.FATAL, FQCN, message, params, null);
    }

    @Deprecated
    public void fatal(Object message, Object[] params, Throwable t) {
        doLog(Level.FATAL, FQCN, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatal(String loggerFqcn, Object message, Object[] params, Throwable t) {
        doLog(Level.FATAL, loggerFqcn, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(String format, Object... params) {
        doLog(Level.FATAL, FQCN, format, params, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(String format, Object param1) {
        if (isEnabled(Level.FATAL)) {
            doLog(Level.FATAL, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(String format, Object param1, Object param2) {
        if (isEnabled(Level.FATAL)) {
            doLog(Level.FATAL, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.FATAL)) {
            doLog(Level.FATAL, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(Throwable t, String format, Object... params) {
        doLog(Level.FATAL, FQCN, format, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(Throwable t, String format, Object param1) {
        if (isEnabled(Level.FATAL)) {
            doLog(Level.FATAL, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.FATAL)) {
            doLog(Level.FATAL, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.FATAL)) {
            doLog(Level.FATAL, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(String format, Object... params) {
        doLogf(Level.FATAL, FQCN, format, params, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(String format, Object param1) {
        if (isEnabled(Level.FATAL)) {
            doLogf(Level.FATAL, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(String format, Object param1, Object param2) {
        if (isEnabled(Level.FATAL)) {
            doLogf(Level.FATAL, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.FATAL)) {
            doLogf(Level.FATAL, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(Throwable t, String format, Object... params) {
        doLogf(Level.FATAL, FQCN, format, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(Throwable t, String format, Object param1) {
        if (isEnabled(Level.FATAL)) {
            doLogf(Level.FATAL, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.FATAL)) {
            doLogf(Level.FATAL, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.FATAL)) {
            doLogf(Level.FATAL, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void log(Level level, Object message) {
        doLog(level, FQCN, message, null, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void log(Level level, Object message, Throwable t) {
        doLog(level, FQCN, message, null, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void log(Level level, String loggerFqcn, Object message, Throwable t) {
        doLog(level, loggerFqcn, message, null, t);
    }

    @Deprecated
    public void log(Level level, Object message, Object[] params) {
        doLog(level, FQCN, message, params, null);
    }

    @Deprecated
    public void log(Level level, Object message, Object[] params, Throwable t) {
        doLog(level, FQCN, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void log(String loggerFqcn, Level level, Object message, Object[] params, Throwable t) {
        doLog(level, loggerFqcn, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Level level, String format, Object... params) {
        doLog(level, FQCN, format, params, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Level level, String format, Object param1) {
        if (isEnabled(level)) {
            doLog(level, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Level level, String format, Object param1, Object param2) {
        if (isEnabled(level)) {
            doLog(level, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Level level, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(level)) {
            doLog(level, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Level level, Throwable t, String format, Object... params) {
        doLog(level, FQCN, format, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Level level, Throwable t, String format, Object param1) {
        if (isEnabled(level)) {
            doLog(level, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Level level, Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(level)) {
            doLog(level, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(level)) {
            doLog(level, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object... params) {
        doLog(level, loggerFqcn, format, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1) {
        if (isEnabled(level)) {
            doLog(level, loggerFqcn, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(level)) {
            doLog(level, loggerFqcn, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(level)) {
            doLog(level, loggerFqcn, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Level level, String format, Object... params) {
        doLogf(level, FQCN, format, params, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Level level, String format, Object param1) {
        if (isEnabled(level)) {
            doLogf(level, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Level level, String format, Object param1, Object param2) {
        if (isEnabled(level)) {
            doLogf(level, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Level level, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(level)) {
            doLogf(level, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Level level, Throwable t, String format, Object... params) {
        doLogf(level, FQCN, format, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Level level, Throwable t, String format, Object param1) {
        if (isEnabled(level)) {
            doLogf(level, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Level level, Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(level)) {
            doLogf(level, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(level)) {
            doLogf(level, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1) {
        if (isEnabled(level)) {
            doLogf(level, loggerFqcn, format, new Object[]{param1}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(level)) {
            doLogf(level, loggerFqcn, format, new Object[]{param1, param2}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(level)) {
            doLogf(level, loggerFqcn, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object... params) {
        doLogf(level, loggerFqcn, format, params, t);
    }

    protected final Object writeReplace() {
        return new SerializedLogger(this.name);
    }

    public static Logger getLogger(String name) {
        return LoggerProviders.PROVIDER.getLogger(name);
    }

    public static Logger getLogger(String name, String suffix) {
        return getLogger((name == null || name.length() == 0) ? suffix : name + "." + suffix);
    }

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(Class<?> clazz, String suffix) {
        return getLogger(clazz.getName(), suffix);
    }

    public static <T> T getMessageLogger(Class<T> type, String category) {
        return (T) getMessageLogger(type, category, LoggingLocale.getLocale());
    }

    public static <T> T getMessageLogger(final Class<T> type, final String category, final Locale locale) {
        return (T) AccessController.doPrivileged(new PrivilegedAction<T>() { // from class: org.jboss.logging.Logger.1
            @Override // java.security.PrivilegedAction
            public T run() {
                String language = locale.getLanguage();
                String country = locale.getCountry();
                String variant = locale.getVariant();
                Class cls = null;
                ClassLoader classLoader = type.getClassLoader();
                String typeName = type.getName();
                if (variant != null && variant.length() > 0) {
                    try {
                        cls = Class.forName(Logger.join(typeName, "$logger", language, country, variant), true, classLoader).asSubclass(type);
                    } catch (ClassNotFoundException e) {
                    }
                }
                if (cls == null && country != null && country.length() > 0) {
                    try {
                        cls = Class.forName(Logger.join(typeName, "$logger", language, country, null), true, classLoader).asSubclass(type);
                    } catch (ClassNotFoundException e2) {
                    }
                }
                if (cls == null && language != null && language.length() > 0) {
                    try {
                        cls = Class.forName(Logger.join(typeName, "$logger", language, null, null), true, classLoader).asSubclass(type);
                    } catch (ClassNotFoundException e3) {
                    }
                }
                if (cls == null) {
                    try {
                        cls = Class.forName(Logger.join(typeName, "$logger", null, null, null), true, classLoader).asSubclass(type);
                    } catch (ClassNotFoundException e4) {
                        throw new IllegalArgumentException("Invalid logger " + type + " (implementation not found in " + classLoader + ")");
                    }
                }
                try {
                    Constructor<? extends T> constructor = cls.getConstructor(Logger.class);
                    try {
                        return constructor.newInstance(Logger.getLogger(category));
                    } catch (IllegalAccessException e5) {
                        throw new IllegalArgumentException("Logger implementation " + cls + " could not be instantiated", e5);
                    } catch (InstantiationException e6) {
                        throw new IllegalArgumentException("Logger implementation " + cls + " could not be instantiated", e6);
                    } catch (InvocationTargetException e7) {
                        throw new IllegalArgumentException("Logger implementation " + cls + " could not be instantiated", e7.getCause());
                    }
                } catch (NoSuchMethodException e8) {
                    throw new IllegalArgumentException("Logger implementation " + cls + " has no matching constructor");
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String join(String interfaceName, String a, String b, String c, String d) {
        StringBuilder build = new StringBuilder();
        build.append(interfaceName).append('_').append(a);
        if (b != null && b.length() > 0) {
            build.append('_');
            build.append(b);
        }
        if (c != null && c.length() > 0) {
            build.append('_');
            build.append(c);
        }
        if (d != null && d.length() > 0) {
            build.append('_');
            build.append(d);
        }
        return build.toString();
    }
}