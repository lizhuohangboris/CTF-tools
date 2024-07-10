package org.slf4j.event;

import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.SubstituteLogger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/slf4j-api-1.7.25.jar:org/slf4j/event/EventRecodingLogger.class */
public class EventRecodingLogger implements Logger {
    String name;
    SubstituteLogger logger;
    Queue<SubstituteLoggingEvent> eventQueue;

    public EventRecodingLogger(SubstituteLogger logger, Queue<SubstituteLoggingEvent> eventQueue) {
        this.logger = logger;
        this.name = logger.getName();
        this.eventQueue = eventQueue;
    }

    @Override // org.slf4j.Logger
    public String getName() {
        return this.name;
    }

    private void recordEvent(Level level, String msg, Object[] args, Throwable throwable) {
        recordEvent(level, null, msg, args, throwable);
    }

    private void recordEvent(Level level, Marker marker, String msg, Object[] args, Throwable throwable) {
        SubstituteLoggingEvent loggingEvent = new SubstituteLoggingEvent();
        loggingEvent.setTimeStamp(System.currentTimeMillis());
        loggingEvent.setLevel(level);
        loggingEvent.setLogger(this.logger);
        loggingEvent.setLoggerName(this.name);
        loggingEvent.setMarker(marker);
        loggingEvent.setMessage(msg);
        loggingEvent.setArgumentArray(args);
        loggingEvent.setThrowable(throwable);
        loggingEvent.setThreadName(Thread.currentThread().getName());
        this.eventQueue.add(loggingEvent);
    }

    @Override // org.slf4j.Logger
    public boolean isTraceEnabled() {
        return true;
    }

    @Override // org.slf4j.Logger
    public void trace(String msg) {
        recordEvent(Level.TRACE, msg, null, null);
    }

    @Override // org.slf4j.Logger
    public void trace(String format, Object arg) {
        recordEvent(Level.TRACE, format, new Object[]{arg}, null);
    }

    @Override // org.slf4j.Logger
    public void trace(String format, Object arg1, Object arg2) {
        recordEvent(Level.TRACE, format, new Object[]{arg1, arg2}, null);
    }

    @Override // org.slf4j.Logger
    public void trace(String format, Object... arguments) {
        recordEvent(Level.TRACE, format, arguments, null);
    }

    @Override // org.slf4j.Logger
    public void trace(String msg, Throwable t) {
        recordEvent(Level.TRACE, msg, null, t);
    }

    @Override // org.slf4j.Logger
    public boolean isTraceEnabled(Marker marker) {
        return true;
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String msg) {
        recordEvent(Level.TRACE, marker, msg, null, null);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String format, Object arg) {
        recordEvent(Level.TRACE, marker, format, new Object[]{arg}, null);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        recordEvent(Level.TRACE, marker, format, new Object[]{arg1, arg2}, null);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String format, Object... argArray) {
        recordEvent(Level.TRACE, marker, format, argArray, null);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String msg, Throwable t) {
        recordEvent(Level.TRACE, marker, msg, null, t);
    }

    @Override // org.slf4j.Logger
    public boolean isDebugEnabled() {
        return true;
    }

    @Override // org.slf4j.Logger
    public void debug(String msg) {
        recordEvent(Level.TRACE, msg, null, null);
    }

    @Override // org.slf4j.Logger
    public void debug(String format, Object arg) {
        recordEvent(Level.DEBUG, format, new Object[]{arg}, null);
    }

    @Override // org.slf4j.Logger
    public void debug(String format, Object arg1, Object arg2) {
        recordEvent(Level.DEBUG, format, new Object[]{arg1, arg2}, null);
    }

    @Override // org.slf4j.Logger
    public void debug(String format, Object... arguments) {
        recordEvent(Level.DEBUG, format, arguments, null);
    }

    @Override // org.slf4j.Logger
    public void debug(String msg, Throwable t) {
        recordEvent(Level.DEBUG, msg, null, t);
    }

    @Override // org.slf4j.Logger
    public boolean isDebugEnabled(Marker marker) {
        return true;
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String msg) {
        recordEvent(Level.DEBUG, marker, msg, null, null);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String format, Object arg) {
        recordEvent(Level.DEBUG, marker, format, new Object[]{arg}, null);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        recordEvent(Level.DEBUG, marker, format, new Object[]{arg1, arg2}, null);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String format, Object... arguments) {
        recordEvent(Level.DEBUG, marker, format, arguments, null);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String msg, Throwable t) {
        recordEvent(Level.DEBUG, marker, msg, null, t);
    }

    @Override // org.slf4j.Logger
    public boolean isInfoEnabled() {
        return true;
    }

    @Override // org.slf4j.Logger
    public void info(String msg) {
        recordEvent(Level.INFO, msg, null, null);
    }

    @Override // org.slf4j.Logger
    public void info(String format, Object arg) {
        recordEvent(Level.INFO, format, new Object[]{arg}, null);
    }

    @Override // org.slf4j.Logger
    public void info(String format, Object arg1, Object arg2) {
        recordEvent(Level.INFO, format, new Object[]{arg1, arg2}, null);
    }

    @Override // org.slf4j.Logger
    public void info(String format, Object... arguments) {
        recordEvent(Level.INFO, format, arguments, null);
    }

    @Override // org.slf4j.Logger
    public void info(String msg, Throwable t) {
        recordEvent(Level.INFO, msg, null, t);
    }

    @Override // org.slf4j.Logger
    public boolean isInfoEnabled(Marker marker) {
        return true;
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String msg) {
        recordEvent(Level.INFO, marker, msg, null, null);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String format, Object arg) {
        recordEvent(Level.INFO, marker, format, new Object[]{arg}, null);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        recordEvent(Level.INFO, marker, format, new Object[]{arg1, arg2}, null);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String format, Object... arguments) {
        recordEvent(Level.INFO, marker, format, arguments, null);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String msg, Throwable t) {
        recordEvent(Level.INFO, marker, msg, null, t);
    }

    @Override // org.slf4j.Logger
    public boolean isWarnEnabled() {
        return true;
    }

    @Override // org.slf4j.Logger
    public void warn(String msg) {
        recordEvent(Level.WARN, msg, null, null);
    }

    @Override // org.slf4j.Logger
    public void warn(String format, Object arg) {
        recordEvent(Level.WARN, format, new Object[]{arg}, null);
    }

    @Override // org.slf4j.Logger
    public void warn(String format, Object arg1, Object arg2) {
        recordEvent(Level.WARN, format, new Object[]{arg1, arg2}, null);
    }

    @Override // org.slf4j.Logger
    public void warn(String format, Object... arguments) {
        recordEvent(Level.WARN, format, arguments, null);
    }

    @Override // org.slf4j.Logger
    public void warn(String msg, Throwable t) {
        recordEvent(Level.WARN, msg, null, t);
    }

    @Override // org.slf4j.Logger
    public boolean isWarnEnabled(Marker marker) {
        return true;
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String msg) {
        recordEvent(Level.WARN, msg, null, null);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String format, Object arg) {
        recordEvent(Level.WARN, format, new Object[]{arg}, null);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        recordEvent(Level.WARN, marker, format, new Object[]{arg1, arg2}, null);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String format, Object... arguments) {
        recordEvent(Level.WARN, marker, format, arguments, null);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String msg, Throwable t) {
        recordEvent(Level.WARN, marker, msg, null, t);
    }

    @Override // org.slf4j.Logger
    public boolean isErrorEnabled() {
        return true;
    }

    @Override // org.slf4j.Logger
    public void error(String msg) {
        recordEvent(Level.ERROR, msg, null, null);
    }

    @Override // org.slf4j.Logger
    public void error(String format, Object arg) {
        recordEvent(Level.ERROR, format, new Object[]{arg}, null);
    }

    @Override // org.slf4j.Logger
    public void error(String format, Object arg1, Object arg2) {
        recordEvent(Level.ERROR, format, new Object[]{arg1, arg2}, null);
    }

    @Override // org.slf4j.Logger
    public void error(String format, Object... arguments) {
        recordEvent(Level.ERROR, format, arguments, null);
    }

    @Override // org.slf4j.Logger
    public void error(String msg, Throwable t) {
        recordEvent(Level.ERROR, msg, null, t);
    }

    @Override // org.slf4j.Logger
    public boolean isErrorEnabled(Marker marker) {
        return true;
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String msg) {
        recordEvent(Level.ERROR, marker, msg, null, null);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String format, Object arg) {
        recordEvent(Level.ERROR, marker, format, new Object[]{arg}, null);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        recordEvent(Level.ERROR, marker, format, new Object[]{arg1, arg2}, null);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String format, Object... arguments) {
        recordEvent(Level.ERROR, marker, format, arguments, null);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String msg, Throwable t) {
        recordEvent(Level.ERROR, marker, msg, null, t);
    }
}