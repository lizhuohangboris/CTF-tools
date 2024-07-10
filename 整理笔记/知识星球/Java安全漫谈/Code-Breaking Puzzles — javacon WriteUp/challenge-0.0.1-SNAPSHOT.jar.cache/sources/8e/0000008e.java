package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.LogbackMDCAdapter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Map;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.MDCAdapter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/spi/LoggingEvent.class */
public class LoggingEvent implements ILoggingEvent {
    transient String fqnOfLoggerClass;
    private String threadName;
    private String loggerName;
    private LoggerContext loggerContext;
    private LoggerContextVO loggerContextVO;
    private transient Level level;
    private String message;
    transient String formattedMessage;
    private transient Object[] argumentArray;
    private ThrowableProxy throwableProxy;
    private StackTraceElement[] callerDataArray;
    private Marker marker;
    private Map<String, String> mdcPropertyMap;
    private long timeStamp;

    public LoggingEvent() {
    }

    public LoggingEvent(String fqcn, Logger logger, Level level, String message, Throwable throwable, Object[] argArray) {
        this.fqnOfLoggerClass = fqcn;
        this.loggerName = logger.getName();
        this.loggerContext = logger.getLoggerContext();
        this.loggerContextVO = this.loggerContext.getLoggerContextRemoteView();
        this.level = level;
        this.message = message;
        this.argumentArray = argArray;
        throwable = throwable == null ? extractThrowableAnRearrangeArguments(argArray) : throwable;
        if (throwable != null) {
            this.throwableProxy = new ThrowableProxy(throwable);
            LoggerContext lc = logger.getLoggerContext();
            if (lc.isPackagingDataEnabled()) {
                this.throwableProxy.calculatePackagingData();
            }
        }
        this.timeStamp = System.currentTimeMillis();
    }

    private Throwable extractThrowableAnRearrangeArguments(Object[] argArray) {
        Throwable extractedThrowable = EventArgUtil.extractThrowable(argArray);
        if (EventArgUtil.successfulExtraction(extractedThrowable)) {
            this.argumentArray = EventArgUtil.trimmedCopy(argArray);
        }
        return extractedThrowable;
    }

    public void setArgumentArray(Object[] argArray) {
        if (this.argumentArray != null) {
            throw new IllegalStateException("argArray has been already set");
        }
        this.argumentArray = argArray;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public Object[] getArgumentArray() {
        return this.argumentArray;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public Level getLevel() {
        return this.level;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public String getLoggerName() {
        return this.loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public String getThreadName() {
        if (this.threadName == null) {
            this.threadName = Thread.currentThread().getName();
        }
        return this.threadName;
    }

    public void setThreadName(String threadName) throws IllegalStateException {
        if (this.threadName != null) {
            throw new IllegalStateException("threadName has been already set");
        }
        this.threadName = threadName;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public IThrowableProxy getThrowableProxy() {
        return this.throwableProxy;
    }

    public void setThrowableProxy(ThrowableProxy tp) {
        if (this.throwableProxy != null) {
            throw new IllegalStateException("ThrowableProxy has been already set.");
        }
        this.throwableProxy = tp;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent, ch.qos.logback.core.spi.DeferredProcessingAware
    public void prepareForDeferredProcessing() {
        getFormattedMessage();
        getThreadName();
        getMDCPropertyMap();
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public LoggerContextVO getLoggerContextVO() {
        return this.loggerContextVO;
    }

    public void setLoggerContextRemoteView(LoggerContextVO loggerContextVO) {
        this.loggerContextVO = loggerContextVO;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        if (this.message != null) {
            throw new IllegalStateException("The message for this event has been set already.");
        }
        this.message = message;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setLevel(Level level) {
        if (this.level != null) {
            throw new IllegalStateException("The level has been already set for this event.");
        }
        this.level = level;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public StackTraceElement[] getCallerData() {
        if (this.callerDataArray == null) {
            this.callerDataArray = CallerData.extract(new Throwable(), this.fqnOfLoggerClass, this.loggerContext.getMaxCallerDataDepth(), this.loggerContext.getFrameworkPackages());
        }
        return this.callerDataArray;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public boolean hasCallerData() {
        return this.callerDataArray != null;
    }

    public void setCallerData(StackTraceElement[] callerDataArray) {
        this.callerDataArray = callerDataArray;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public Marker getMarker() {
        return this.marker;
    }

    public void setMarker(Marker marker) {
        if (this.marker != null) {
            throw new IllegalStateException("The marker has been already set for this event.");
        }
        this.marker = marker;
    }

    public long getContextBirthTime() {
        return this.loggerContextVO.getBirthTime();
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public String getFormattedMessage() {
        if (this.formattedMessage != null) {
            return this.formattedMessage;
        }
        if (this.argumentArray != null) {
            this.formattedMessage = MessageFormatter.arrayFormat(this.message, this.argumentArray).getMessage();
        } else {
            this.formattedMessage = this.message;
        }
        return this.formattedMessage;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public Map<String, String> getMDCPropertyMap() {
        if (this.mdcPropertyMap == null) {
            MDCAdapter mdc = MDC.getMDCAdapter();
            if (mdc instanceof LogbackMDCAdapter) {
                this.mdcPropertyMap = ((LogbackMDCAdapter) mdc).getPropertyMap();
            } else {
                this.mdcPropertyMap = mdc.getCopyOfContextMap();
            }
        }
        if (this.mdcPropertyMap == null) {
            this.mdcPropertyMap = Collections.emptyMap();
        }
        return this.mdcPropertyMap;
    }

    public void setMDCPropertyMap(Map<String, String> map) {
        if (this.mdcPropertyMap != null) {
            throw new IllegalStateException("The MDCPropertyMap has been already set for this event.");
        }
        this.mdcPropertyMap = map;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public Map<String, String> getMdc() {
        return getMDCPropertyMap();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(this.level).append("] ");
        sb.append(getFormattedMessage());
        return sb.toString();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        throw new UnsupportedOperationException(getClass() + " does not support serialization. Use LoggerEventVO instance instead. See also LoggerEventVO.build method.");
    }
}