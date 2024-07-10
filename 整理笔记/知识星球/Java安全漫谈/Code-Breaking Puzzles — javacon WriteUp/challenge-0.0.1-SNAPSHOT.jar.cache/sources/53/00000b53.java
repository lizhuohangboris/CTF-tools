package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/ExtendedLoggerWrapper.class */
public class ExtendedLoggerWrapper extends AbstractLogger {
    private static final long serialVersionUID = 1;
    protected final ExtendedLogger logger;

    public ExtendedLoggerWrapper(ExtendedLogger logger, String name, MessageFactory messageFactory) {
        super(name, messageFactory);
        this.logger = logger;
    }

    @Override // org.apache.logging.log4j.Logger
    public Level getLevel() {
        return this.logger.getLevel();
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, Message message, Throwable t) {
        return this.logger.isEnabled(level, marker, message, t);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, CharSequence message, Throwable t) {
        return this.logger.isEnabled(level, marker, message, t);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, Object message, Throwable t) {
        return this.logger.isEnabled(level, marker, message, t);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message) {
        return this.logger.isEnabled(level, marker, message);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object... params) {
        return this.logger.isEnabled(level, marker, message, params);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0) {
        return this.logger.isEnabled(level, marker, message, p0);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1) {
        return this.logger.isEnabled(level, marker, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Throwable t) {
        return this.logger.isEnabled(level, marker, message, t);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logMessage(String fqcn, Level level, Marker marker, Message message, Throwable t) {
        this.logger.logMessage(fqcn, level, marker, message, t);
    }
}