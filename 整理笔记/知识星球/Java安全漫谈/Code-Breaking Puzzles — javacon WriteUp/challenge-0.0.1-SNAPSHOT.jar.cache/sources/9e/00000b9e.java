package org.apache.logging.slf4j;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.spi.LocationAwareLogger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-to-slf4j-2.11.1.jar:org/apache/logging/slf4j/SLF4JLogger.class */
public class SLF4JLogger extends AbstractLogger {
    private static final long serialVersionUID = 1;
    private final Logger logger;
    private final LocationAwareLogger locationAwareLogger;

    public SLF4JLogger(String name, MessageFactory messageFactory, Logger logger) {
        super(name, messageFactory);
        this.logger = logger;
        this.locationAwareLogger = logger instanceof LocationAwareLogger ? (LocationAwareLogger) logger : null;
    }

    public SLF4JLogger(String name, Logger logger) {
        super(name);
        this.logger = logger;
        this.locationAwareLogger = logger instanceof LocationAwareLogger ? (LocationAwareLogger) logger : null;
    }

    private int convertLevel(Level level) {
        switch (level.getStandardLevel()) {
            case DEBUG:
                return 10;
            case TRACE:
                return 0;
            case INFO:
                return 20;
            case WARN:
                return 30;
            case ERROR:
                return 40;
            default:
                return 40;
        }
    }

    @Override // org.apache.logging.log4j.Logger
    public Level getLevel() {
        if (this.logger.isTraceEnabled()) {
            return Level.TRACE;
        }
        if (this.logger.isDebugEnabled()) {
            return Level.DEBUG;
        }
        if (this.logger.isInfoEnabled()) {
            return Level.INFO;
        }
        if (this.logger.isWarnEnabled()) {
            return Level.WARN;
        }
        if (this.logger.isErrorEnabled()) {
            return Level.ERROR;
        }
        return Level.OFF;
    }

    public Logger getLogger() {
        return this.locationAwareLogger != null ? this.locationAwareLogger : this.logger;
    }

    private Marker getMarker(org.apache.logging.log4j.Marker marker) {
        if (marker == null) {
            return null;
        }
        Marker slf4jMarker = MarkerFactory.getMarker(marker.getName());
        org.apache.logging.log4j.Marker[] parents = marker.getParents();
        if (parents != null) {
            for (org.apache.logging.log4j.Marker parent : parents) {
                Marker slf4jParent = getMarker(parent);
                if (!slf4jMarker.contains(slf4jParent)) {
                    slf4jMarker.add(slf4jParent);
                }
            }
        }
        return slf4jMarker;
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, Message data, Throwable t) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, CharSequence data, Throwable t) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, Object data, Throwable t) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, String data) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, String data, Object... p1) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, String message, Object p0) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, org.apache.logging.log4j.Marker marker, String data, Throwable t) {
        return isEnabledFor(level, marker);
    }

    private boolean isEnabledFor(Level level, org.apache.logging.log4j.Marker marker) {
        Marker slf4jMarker = getMarker(marker);
        switch (level.getStandardLevel()) {
            case DEBUG:
                return this.logger.isDebugEnabled(slf4jMarker);
            case TRACE:
                return this.logger.isTraceEnabled(slf4jMarker);
            case INFO:
                return this.logger.isInfoEnabled(slf4jMarker);
            case WARN:
                return this.logger.isWarnEnabled(slf4jMarker);
            case ERROR:
                return this.logger.isErrorEnabled(slf4jMarker);
            default:
                return this.logger.isErrorEnabled(slf4jMarker);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logMessage(String fqcn, Level level, org.apache.logging.log4j.Marker marker, Message message, Throwable t) {
        if (this.locationAwareLogger != null) {
            if (message instanceof LoggerNameAwareMessage) {
                ((LoggerNameAwareMessage) message).setLoggerName(getName());
            }
            this.locationAwareLogger.log(getMarker(marker), fqcn, convertLevel(level), message.getFormattedMessage(), message.getParameters(), t);
            return;
        }
        switch (level.getStandardLevel()) {
            case DEBUG:
                this.logger.debug(getMarker(marker), message.getFormattedMessage(), message.getParameters(), t);
                return;
            case TRACE:
                this.logger.trace(getMarker(marker), message.getFormattedMessage(), message.getParameters(), t);
                return;
            case INFO:
                this.logger.info(getMarker(marker), message.getFormattedMessage(), message.getParameters(), t);
                return;
            case WARN:
                this.logger.warn(getMarker(marker), message.getFormattedMessage(), message.getParameters(), t);
                return;
            case ERROR:
                this.logger.error(getMarker(marker), message.getFormattedMessage(), message.getParameters(), t);
                return;
            default:
                this.logger.error(getMarker(marker), message.getFormattedMessage(), message.getParameters(), t);
                return;
        }
    }
}