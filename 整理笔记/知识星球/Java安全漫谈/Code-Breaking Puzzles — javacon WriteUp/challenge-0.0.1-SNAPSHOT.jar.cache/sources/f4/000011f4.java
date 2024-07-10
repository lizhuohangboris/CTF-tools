package org.jboss.logging;

import java.io.Serializable;
import org.jboss.logging.Logger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/DelegatingBasicLogger.class */
public class DelegatingBasicLogger implements BasicLogger, Serializable {
    private static final long serialVersionUID = -5774903162389601853L;
    private static final String FQCN = DelegatingBasicLogger.class.getName();
    protected final Logger log;

    public DelegatingBasicLogger(Logger log) {
        this.log = log;
    }

    @Override // org.jboss.logging.BasicLogger
    public boolean isTraceEnabled() {
        return this.log.isTraceEnabled();
    }

    @Override // org.jboss.logging.BasicLogger
    public void trace(Object message) {
        this.log.trace(FQCN, message, (Throwable) null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void trace(Object message, Throwable t) {
        this.log.trace(FQCN, message, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void trace(String loggerFqcn, Object message, Throwable t) {
        this.log.trace(loggerFqcn, message, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void trace(String loggerFqcn, Object message, Object[] params, Throwable t) {
        this.log.trace(loggerFqcn, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(String format, Object... params) {
        this.log.logv(FQCN, Logger.Level.TRACE, (Throwable) null, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(String format, Object param1) {
        this.log.logv(FQCN, Logger.Level.TRACE, (Throwable) null, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(String format, Object param1, Object param2) {
        this.log.logv(FQCN, Logger.Level.TRACE, (Throwable) null, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(String format, Object param1, Object param2, Object param3) {
        this.log.logv(FQCN, Logger.Level.TRACE, null, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(Throwable t, String format, Object... params) {
        this.log.logv(FQCN, Logger.Level.TRACE, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(Throwable t, String format, Object param1) {
        this.log.logv(FQCN, Logger.Level.TRACE, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(Throwable t, String format, Object param1, Object param2) {
        this.log.logv(FQCN, Logger.Level.TRACE, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracev(Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logv(FQCN, Logger.Level.TRACE, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, Object... params) {
        this.log.logf(FQCN, Logger.Level.TRACE, (Throwable) null, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, Object param1) {
        this.log.logf(FQCN, Logger.Level.TRACE, (Throwable) null, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, Object param1, Object param2) {
        this.log.logf(FQCN, Logger.Level.TRACE, (Throwable) null, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, Object param1, Object param2, Object param3) {
        this.log.logf(FQCN, Logger.Level.TRACE, null, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, Object... params) {
        this.log.logf(FQCN, Logger.Level.TRACE, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, Object param1) {
        this.log.logf(FQCN, Logger.Level.TRACE, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, Object param1, Object param2) {
        this.log.logf(FQCN, Logger.Level.TRACE, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logf(FQCN, Logger.Level.TRACE, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, int arg) {
        this.log.tracef(format, arg);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, int arg1, int arg2) {
        this.log.tracef(format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, int arg1, Object arg2) {
        this.log.tracef(format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, int arg1, int arg2, int arg3) {
        this.log.tracef(format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, int arg1, int arg2, Object arg3) {
        this.log.tracef(format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, int arg1, Object arg2, Object arg3) {
        this.log.tracef(format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, int arg) {
        this.log.tracef(t, format, arg);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, int arg1, int arg2) {
        this.log.tracef(t, format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, int arg1, Object arg2) {
        this.log.tracef(t, format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, int arg1, int arg2, int arg3) {
        this.log.tracef(t, format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, int arg1, int arg2, Object arg3) {
        this.log.tracef(t, format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, int arg1, Object arg2, Object arg3) {
        this.log.tracef(t, format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, long arg) {
        this.log.tracef(format, arg);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, long arg1, long arg2) {
        this.log.tracef(format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, long arg1, Object arg2) {
        this.log.tracef(format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, long arg1, long arg2, long arg3) {
        this.log.tracef(format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, long arg1, long arg2, Object arg3) {
        this.log.tracef(format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(String format, long arg1, Object arg2, Object arg3) {
        this.log.tracef(format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, long arg) {
        this.log.tracef(t, format, arg);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, long arg1, long arg2) {
        this.log.tracef(t, format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, long arg1, Object arg2) {
        this.log.tracef(t, format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, long arg1, long arg2, long arg3) {
        this.log.tracef(t, format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, long arg1, long arg2, Object arg3) {
        this.log.tracef(t, format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void tracef(Throwable t, String format, long arg1, Object arg2, Object arg3) {
        this.log.tracef(t, format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public boolean isDebugEnabled() {
        return this.log.isDebugEnabled();
    }

    @Override // org.jboss.logging.BasicLogger
    public void debug(Object message) {
        this.log.debug(FQCN, message, (Throwable) null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debug(Object message, Throwable t) {
        this.log.debug(FQCN, message, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debug(String loggerFqcn, Object message, Throwable t) {
        this.log.debug(loggerFqcn, message, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debug(String loggerFqcn, Object message, Object[] params, Throwable t) {
        this.log.debug(loggerFqcn, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(String format, Object... params) {
        this.log.logv(FQCN, Logger.Level.DEBUG, (Throwable) null, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(String format, Object param1) {
        this.log.logv(FQCN, Logger.Level.DEBUG, (Throwable) null, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(String format, Object param1, Object param2) {
        this.log.logv(FQCN, Logger.Level.DEBUG, (Throwable) null, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(String format, Object param1, Object param2, Object param3) {
        this.log.logv(FQCN, Logger.Level.DEBUG, null, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(Throwable t, String format, Object... params) {
        this.log.logv(FQCN, Logger.Level.DEBUG, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(Throwable t, String format, Object param1) {
        this.log.logv(FQCN, Logger.Level.DEBUG, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(Throwable t, String format, Object param1, Object param2) {
        this.log.logv(FQCN, Logger.Level.DEBUG, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugv(Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logv(FQCN, Logger.Level.DEBUG, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, Object... params) {
        this.log.logf(FQCN, Logger.Level.DEBUG, (Throwable) null, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, Object param1) {
        this.log.logf(FQCN, Logger.Level.DEBUG, (Throwable) null, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, Object param1, Object param2) {
        this.log.logf(FQCN, Logger.Level.DEBUG, (Throwable) null, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, Object param1, Object param2, Object param3) {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, Object... params) {
        this.log.logf(FQCN, Logger.Level.DEBUG, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, Object param1) {
        this.log.logf(FQCN, Logger.Level.DEBUG, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, Object param1, Object param2) {
        this.log.logf(FQCN, Logger.Level.DEBUG, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logf(FQCN, Logger.Level.DEBUG, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, int arg) {
        this.log.debugf(format, arg);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, int arg1, int arg2) {
        this.log.debugf(format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, int arg1, Object arg2) {
        this.log.debugf(format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, int arg1, int arg2, int arg3) {
        this.log.debugf(format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, int arg1, int arg2, Object arg3) {
        this.log.debugf(format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, int arg1, Object arg2, Object arg3) {
        this.log.debugf(format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, int arg) {
        this.log.debugf(t, format, arg);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, int arg1, int arg2) {
        this.log.debugf(t, format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, int arg1, Object arg2) {
        this.log.debugf(t, format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, int arg1, int arg2, int arg3) {
        this.log.debugf(t, format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, int arg1, int arg2, Object arg3) {
        this.log.debugf(t, format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, int arg1, Object arg2, Object arg3) {
        this.log.debugf(t, format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, long arg) {
        this.log.debugf(format, arg);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, long arg1, long arg2) {
        this.log.debugf(format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, long arg1, Object arg2) {
        this.log.debugf(format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, long arg1, long arg2, long arg3) {
        this.log.debugf(format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, long arg1, long arg2, Object arg3) {
        this.log.debugf(format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(String format, long arg1, Object arg2, Object arg3) {
        this.log.debugf(format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, long arg) {
        this.log.debugf(t, format, arg);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, long arg1, long arg2) {
        this.log.debugf(t, format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, long arg1, Object arg2) {
        this.log.debugf(t, format, arg1, arg2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, long arg1, long arg2, long arg3) {
        this.log.debugf(t, format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, long arg1, long arg2, Object arg3) {
        this.log.debugf(t, format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void debugf(Throwable t, String format, long arg1, Object arg2, Object arg3) {
        this.log.debugf(t, format, arg1, arg2, arg3);
    }

    @Override // org.jboss.logging.BasicLogger
    public boolean isInfoEnabled() {
        return this.log.isInfoEnabled();
    }

    @Override // org.jboss.logging.BasicLogger
    public void info(Object message) {
        this.log.info(FQCN, message, (Throwable) null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void info(Object message, Throwable t) {
        this.log.info(FQCN, message, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void info(String loggerFqcn, Object message, Throwable t) {
        this.log.info(loggerFqcn, message, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void info(String loggerFqcn, Object message, Object[] params, Throwable t) {
        this.log.info(loggerFqcn, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(String format, Object... params) {
        this.log.logv(FQCN, Logger.Level.INFO, (Throwable) null, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(String format, Object param1) {
        this.log.logv(FQCN, Logger.Level.INFO, (Throwable) null, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(String format, Object param1, Object param2) {
        this.log.logv(FQCN, Logger.Level.INFO, (Throwable) null, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(String format, Object param1, Object param2, Object param3) {
        this.log.logv(FQCN, Logger.Level.INFO, null, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(Throwable t, String format, Object... params) {
        this.log.logv(FQCN, Logger.Level.INFO, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(Throwable t, String format, Object param1) {
        this.log.logv(FQCN, Logger.Level.INFO, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(Throwable t, String format, Object param1, Object param2) {
        this.log.logv(FQCN, Logger.Level.INFO, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infov(Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logv(FQCN, Logger.Level.INFO, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(String format, Object... params) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable) null, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(String format, Object param1) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable) null, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(String format, Object param1, Object param2) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable) null, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(String format, Object param1, Object param2, Object param3) {
        this.log.logf(FQCN, Logger.Level.INFO, null, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(Throwable t, String format, Object... params) {
        this.log.logf(FQCN, Logger.Level.INFO, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(Throwable t, String format, Object param1) {
        this.log.logf(FQCN, Logger.Level.INFO, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(Throwable t, String format, Object param1, Object param2) {
        this.log.logf(FQCN, Logger.Level.INFO, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void infof(Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logf(FQCN, Logger.Level.INFO, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warn(Object message) {
        this.log.warn(FQCN, message, (Throwable) null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warn(Object message, Throwable t) {
        this.log.warn(FQCN, message, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warn(String loggerFqcn, Object message, Throwable t) {
        this.log.warn(loggerFqcn, message, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warn(String loggerFqcn, Object message, Object[] params, Throwable t) {
        this.log.warn(loggerFqcn, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(String format, Object... params) {
        this.log.logv(FQCN, Logger.Level.WARN, (Throwable) null, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(String format, Object param1) {
        this.log.logv(FQCN, Logger.Level.WARN, (Throwable) null, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(String format, Object param1, Object param2) {
        this.log.logv(FQCN, Logger.Level.WARN, (Throwable) null, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(String format, Object param1, Object param2, Object param3) {
        this.log.logv(FQCN, Logger.Level.WARN, null, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(Throwable t, String format, Object... params) {
        this.log.logv(FQCN, Logger.Level.WARN, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(Throwable t, String format, Object param1) {
        this.log.logv(FQCN, Logger.Level.WARN, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(Throwable t, String format, Object param1, Object param2) {
        this.log.logv(FQCN, Logger.Level.WARN, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnv(Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logv(FQCN, Logger.Level.WARN, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(String format, Object... params) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable) null, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(String format, Object param1) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable) null, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(String format, Object param1, Object param2) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable) null, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(String format, Object param1, Object param2, Object param3) {
        this.log.logf(FQCN, Logger.Level.WARN, null, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(Throwable t, String format, Object... params) {
        this.log.logf(FQCN, Logger.Level.WARN, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(Throwable t, String format, Object param1) {
        this.log.logf(FQCN, Logger.Level.WARN, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(Throwable t, String format, Object param1, Object param2) {
        this.log.logf(FQCN, Logger.Level.WARN, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void warnf(Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logf(FQCN, Logger.Level.WARN, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void error(Object message) {
        this.log.error(FQCN, message, (Throwable) null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void error(Object message, Throwable t) {
        this.log.error(FQCN, message, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void error(String loggerFqcn, Object message, Throwable t) {
        this.log.error(loggerFqcn, message, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void error(String loggerFqcn, Object message, Object[] params, Throwable t) {
        this.log.error(loggerFqcn, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(String format, Object... params) {
        this.log.logv(FQCN, Logger.Level.ERROR, (Throwable) null, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(String format, Object param1) {
        this.log.logv(FQCN, Logger.Level.ERROR, (Throwable) null, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(String format, Object param1, Object param2) {
        this.log.logv(FQCN, Logger.Level.ERROR, (Throwable) null, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(String format, Object param1, Object param2, Object param3) {
        this.log.logv(FQCN, Logger.Level.ERROR, null, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(Throwable t, String format, Object... params) {
        this.log.logv(FQCN, Logger.Level.ERROR, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(Throwable t, String format, Object param1) {
        this.log.logv(FQCN, Logger.Level.ERROR, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(Throwable t, String format, Object param1, Object param2) {
        this.log.logv(FQCN, Logger.Level.ERROR, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorv(Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logv(FQCN, Logger.Level.ERROR, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(String format, Object... params) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable) null, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(String format, Object param1) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable) null, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(String format, Object param1, Object param2) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable) null, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(String format, Object param1, Object param2, Object param3) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(Throwable t, String format, Object... params) {
        this.log.logf(FQCN, Logger.Level.ERROR, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(Throwable t, String format, Object param1) {
        this.log.logf(FQCN, Logger.Level.ERROR, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(Throwable t, String format, Object param1, Object param2) {
        this.log.logf(FQCN, Logger.Level.ERROR, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void errorf(Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logf(FQCN, Logger.Level.ERROR, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatal(Object message) {
        this.log.fatal(FQCN, message, (Throwable) null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatal(Object message, Throwable t) {
        this.log.fatal(FQCN, message, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatal(String loggerFqcn, Object message, Throwable t) {
        this.log.fatal(loggerFqcn, message, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatal(String loggerFqcn, Object message, Object[] params, Throwable t) {
        this.log.fatal(loggerFqcn, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(String format, Object... params) {
        this.log.logv(FQCN, Logger.Level.FATAL, (Throwable) null, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(String format, Object param1) {
        this.log.logv(FQCN, Logger.Level.FATAL, (Throwable) null, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(String format, Object param1, Object param2) {
        this.log.logv(FQCN, Logger.Level.FATAL, (Throwable) null, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(String format, Object param1, Object param2, Object param3) {
        this.log.logv(FQCN, Logger.Level.FATAL, null, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(Throwable t, String format, Object... params) {
        this.log.logv(FQCN, Logger.Level.FATAL, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(Throwable t, String format, Object param1) {
        this.log.logv(FQCN, Logger.Level.FATAL, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(Throwable t, String format, Object param1, Object param2) {
        this.log.logv(FQCN, Logger.Level.FATAL, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalv(Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logv(FQCN, Logger.Level.FATAL, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(String format, Object... params) {
        this.log.logf(FQCN, Logger.Level.FATAL, (Throwable) null, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(String format, Object param1) {
        this.log.logf(FQCN, Logger.Level.FATAL, (Throwable) null, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(String format, Object param1, Object param2) {
        this.log.logf(FQCN, Logger.Level.FATAL, (Throwable) null, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(String format, Object param1, Object param2, Object param3) {
        this.log.logf(FQCN, Logger.Level.FATAL, null, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(Throwable t, String format, Object... params) {
        this.log.logf(FQCN, Logger.Level.FATAL, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(Throwable t, String format, Object param1) {
        this.log.logf(FQCN, Logger.Level.FATAL, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(Throwable t, String format, Object param1, Object param2) {
        this.log.logf(FQCN, Logger.Level.FATAL, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void fatalf(Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logf(FQCN, Logger.Level.FATAL, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void log(Logger.Level level, Object message) {
        this.log.log(FQCN, level, message, null, null);
    }

    @Override // org.jboss.logging.BasicLogger
    public void log(Logger.Level level, Object message, Throwable t) {
        this.log.log(FQCN, level, message, null, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void log(Logger.Level level, String loggerFqcn, Object message, Throwable t) {
        this.log.log(level, loggerFqcn, message, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void log(String loggerFqcn, Logger.Level level, Object message, Object[] params, Throwable t) {
        this.log.log(loggerFqcn, level, message, params, t);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Logger.Level level, String format, Object... params) {
        this.log.logv(FQCN, level, (Throwable) null, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Logger.Level level, String format, Object param1) {
        this.log.logv(FQCN, level, (Throwable) null, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Logger.Level level, String format, Object param1, Object param2) {
        this.log.logv(FQCN, level, (Throwable) null, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Logger.Level level, String format, Object param1, Object param2, Object param3) {
        this.log.logv(FQCN, level, null, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Logger.Level level, Throwable t, String format, Object... params) {
        this.log.logv(FQCN, level, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Logger.Level level, Throwable t, String format, Object param1) {
        this.log.logv(FQCN, level, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Logger.Level level, Throwable t, String format, Object param1, Object param2) {
        this.log.logv(FQCN, level, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(Logger.Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logv(FQCN, level, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(String loggerFqcn, Logger.Level level, Throwable t, String format, Object... params) {
        this.log.logv(loggerFqcn, level, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1) {
        this.log.logv(loggerFqcn, level, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1, Object param2) {
        this.log.logv(loggerFqcn, level, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logv(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logv(loggerFqcn, level, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Logger.Level level, String format, Object... params) {
        this.log.logf(FQCN, level, (Throwable) null, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Logger.Level level, String format, Object param1) {
        this.log.logf(FQCN, level, (Throwable) null, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Logger.Level level, String format, Object param1, Object param2) {
        this.log.logf(FQCN, level, (Throwable) null, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Logger.Level level, String format, Object param1, Object param2, Object param3) {
        this.log.logf(FQCN, level, null, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Logger.Level level, Throwable t, String format, Object... params) {
        this.log.logf(FQCN, level, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Logger.Level level, Throwable t, String format, Object param1) {
        this.log.logf(FQCN, level, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Logger.Level level, Throwable t, String format, Object param1, Object param2) {
        this.log.logf(FQCN, level, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(Logger.Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logf(FQCN, level, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1) {
        this.log.logf(loggerFqcn, level, t, format, param1);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1, Object param2) {
        this.log.logf(loggerFqcn, level, t, format, param1, param2);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        this.log.logf(loggerFqcn, level, t, format, param1, param2, param3);
    }

    @Override // org.jboss.logging.BasicLogger
    public void logf(String loggerFqcn, Logger.Level level, Throwable t, String format, Object... params) {
        this.log.logf(loggerFqcn, level, t, format, params);
    }

    @Override // org.jboss.logging.BasicLogger
    public boolean isEnabled(Logger.Level level) {
        return this.log.isEnabled(level);
    }
}