package org.springframework.core.log;

import java.util.List;
import java.util.function.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.NoOpLog;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/log/CompositeLog.class */
final class CompositeLog implements Log {
    private static final Log NO_OP_LOG = new NoOpLog();
    private final Log fatalLogger;
    private final Log errorLogger;
    private final Log warnLogger;
    private final Log infoLogger;
    private final Log debugLogger;
    private final Log traceLogger;

    public CompositeLog(List<Log> loggers) {
        this.fatalLogger = initLogger(loggers, (v0) -> {
            return v0.isFatalEnabled();
        });
        this.errorLogger = initLogger(loggers, (v0) -> {
            return v0.isErrorEnabled();
        });
        this.warnLogger = initLogger(loggers, (v0) -> {
            return v0.isWarnEnabled();
        });
        this.infoLogger = initLogger(loggers, (v0) -> {
            return v0.isInfoEnabled();
        });
        this.debugLogger = initLogger(loggers, (v0) -> {
            return v0.isDebugEnabled();
        });
        this.traceLogger = initLogger(loggers, (v0) -> {
            return v0.isTraceEnabled();
        });
    }

    private static Log initLogger(List<Log> loggers, Predicate<Log> predicate) {
        return loggers.stream().filter(predicate).findFirst().orElse(NO_OP_LOG);
    }

    @Override // org.apache.commons.logging.Log
    public boolean isFatalEnabled() {
        return this.fatalLogger != NO_OP_LOG;
    }

    @Override // org.apache.commons.logging.Log
    public boolean isErrorEnabled() {
        return this.errorLogger != NO_OP_LOG;
    }

    @Override // org.apache.commons.logging.Log
    public boolean isWarnEnabled() {
        return this.warnLogger != NO_OP_LOG;
    }

    @Override // org.apache.commons.logging.Log
    public boolean isInfoEnabled() {
        return this.infoLogger != NO_OP_LOG;
    }

    @Override // org.apache.commons.logging.Log
    public boolean isDebugEnabled() {
        return this.debugLogger != NO_OP_LOG;
    }

    @Override // org.apache.commons.logging.Log
    public boolean isTraceEnabled() {
        return this.traceLogger != NO_OP_LOG;
    }

    @Override // org.apache.commons.logging.Log
    public void fatal(Object message) {
        this.fatalLogger.fatal(message);
    }

    @Override // org.apache.commons.logging.Log
    public void fatal(Object message, Throwable ex) {
        this.fatalLogger.fatal(message, ex);
    }

    @Override // org.apache.commons.logging.Log
    public void error(Object message) {
        this.errorLogger.error(message);
    }

    @Override // org.apache.commons.logging.Log
    public void error(Object message, Throwable ex) {
        this.errorLogger.error(message);
    }

    @Override // org.apache.commons.logging.Log
    public void warn(Object message) {
        this.warnLogger.warn(message);
    }

    @Override // org.apache.commons.logging.Log
    public void warn(Object message, Throwable ex) {
        this.warnLogger.warn(message, ex);
    }

    @Override // org.apache.commons.logging.Log
    public void info(Object message) {
        this.infoLogger.info(message);
    }

    @Override // org.apache.commons.logging.Log
    public void info(Object message, Throwable ex) {
        this.infoLogger.info(message, ex);
    }

    @Override // org.apache.commons.logging.Log
    public void debug(Object message) {
        this.debugLogger.debug(message);
    }

    @Override // org.apache.commons.logging.Log
    public void debug(Object message, Throwable ex) {
        this.debugLogger.debug(message, ex);
    }

    @Override // org.apache.commons.logging.Log
    public void trace(Object message) {
        this.traceLogger.trace(message);
    }

    @Override // org.apache.commons.logging.Log
    public void trace(Object message, Throwable ex) {
        this.traceLogger.trace(message, ex);
    }
}