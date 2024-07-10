package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.Level;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/spi/LoggingEventVO.class */
public class LoggingEventVO implements ILoggingEvent, Serializable {
    private static final long serialVersionUID = 6553722650255690312L;
    private static final int NULL_ARGUMENT_ARRAY = -1;
    private static final String NULL_ARGUMENT_ARRAY_ELEMENT = "NULL_ARGUMENT_ARRAY_ELEMENT";
    private String threadName;
    private String loggerName;
    private LoggerContextVO loggerContextVO;
    private transient Level level;
    private String message;
    private transient String formattedMessage;
    private transient Object[] argumentArray;
    private ThrowableProxyVO throwableProxy;
    private StackTraceElement[] callerDataArray;
    private Marker marker;
    private Map<String, String> mdcPropertyMap;
    private long timeStamp;

    public static LoggingEventVO build(ILoggingEvent le) {
        LoggingEventVO ledo = new LoggingEventVO();
        ledo.loggerName = le.getLoggerName();
        ledo.loggerContextVO = le.getLoggerContextVO();
        ledo.threadName = le.getThreadName();
        ledo.level = le.getLevel();
        ledo.message = le.getMessage();
        ledo.argumentArray = le.getArgumentArray();
        ledo.marker = le.getMarker();
        ledo.mdcPropertyMap = le.getMDCPropertyMap();
        ledo.timeStamp = le.getTimeStamp();
        ledo.throwableProxy = ThrowableProxyVO.build(le.getThrowableProxy());
        if (le.hasCallerData()) {
            ledo.callerDataArray = le.getCallerData();
        }
        return ledo;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public String getThreadName() {
        return this.threadName;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public LoggerContextVO getLoggerContextVO() {
        return this.loggerContextVO;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public String getLoggerName() {
        return this.loggerName;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public Level getLevel() {
        return this.level;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public String getMessage() {
        return this.message;
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
    public Object[] getArgumentArray() {
        return this.argumentArray;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public IThrowableProxy getThrowableProxy() {
        return this.throwableProxy;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public StackTraceElement[] getCallerData() {
        return this.callerDataArray;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public boolean hasCallerData() {
        return this.callerDataArray != null;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public Marker getMarker() {
        return this.marker;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public long getTimeStamp() {
        return this.timeStamp;
    }

    public long getContextBirthTime() {
        return this.loggerContextVO.getBirthTime();
    }

    public LoggerContextVO getContextLoggerRemoteView() {
        return this.loggerContextVO;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public Map<String, String> getMDCPropertyMap() {
        return this.mdcPropertyMap;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public Map<String, String> getMdc() {
        return this.mdcPropertyMap;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent, ch.qos.logback.core.spi.DeferredProcessingAware
    public void prepareForDeferredProcessing() {
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(this.level.levelInt);
        if (this.argumentArray != null) {
            int len = this.argumentArray.length;
            out.writeInt(len);
            for (int i = 0; i < this.argumentArray.length; i++) {
                if (this.argumentArray[i] != null) {
                    out.writeObject(this.argumentArray[i].toString());
                } else {
                    out.writeObject(NULL_ARGUMENT_ARRAY_ELEMENT);
                }
            }
            return;
        }
        out.writeInt(-1);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int levelInt = in.readInt();
        this.level = Level.toLevel(levelInt);
        int argArrayLen = in.readInt();
        if (argArrayLen != -1) {
            this.argumentArray = new String[argArrayLen];
            for (int i = 0; i < argArrayLen; i++) {
                Object val = in.readObject();
                if (!NULL_ARGUMENT_ARRAY_ELEMENT.equals(val)) {
                    this.argumentArray[i] = val;
                }
            }
        }
    }

    public int hashCode() {
        int result = (31 * 1) + (this.message == null ? 0 : this.message.hashCode());
        return (31 * ((31 * result) + (this.threadName == null ? 0 : this.threadName.hashCode()))) + ((int) (this.timeStamp ^ (this.timeStamp >>> 32)));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LoggingEventVO other = (LoggingEventVO) obj;
        if (this.message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!this.message.equals(other.message)) {
            return false;
        }
        if (this.loggerName == null) {
            if (other.loggerName != null) {
                return false;
            }
        } else if (!this.loggerName.equals(other.loggerName)) {
            return false;
        }
        if (this.threadName == null) {
            if (other.threadName != null) {
                return false;
            }
        } else if (!this.threadName.equals(other.threadName)) {
            return false;
        }
        if (this.timeStamp != other.timeStamp) {
            return false;
        }
        if (this.marker == null) {
            if (other.marker != null) {
                return false;
            }
        } else if (!this.marker.equals(other.marker)) {
            return false;
        }
        if (this.mdcPropertyMap == null) {
            if (other.mdcPropertyMap != null) {
                return false;
            }
            return true;
        } else if (!this.mdcPropertyMap.equals(other.mdcPropertyMap)) {
            return false;
        } else {
            return true;
        }
    }
}