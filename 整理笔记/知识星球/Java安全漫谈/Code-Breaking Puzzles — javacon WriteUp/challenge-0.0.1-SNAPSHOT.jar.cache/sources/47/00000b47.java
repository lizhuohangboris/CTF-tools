package org.apache.logging.log4j.simple;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerRegistry;
import org.apache.logging.log4j.util.PropertiesUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/simple/SimpleLoggerContext.class */
public class SimpleLoggerContext implements LoggerContext {
    private static final String SYSTEM_OUT = "system.out";
    private static final String SYSTEM_ERR = "system.err";
    protected static final String DEFAULT_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss:SSS zzz";
    protected static final String SYSTEM_PREFIX = "org.apache.logging.log4j.simplelog.";
    private final String dateTimeFormat;
    private final Level defaultLevel;
    private final PrintStream stream;
    private final LoggerRegistry<ExtendedLogger> loggerRegistry = new LoggerRegistry<>();
    private final PropertiesUtil props = new PropertiesUtil("log4j2.simplelog.properties");
    private final boolean showContextMap = this.props.getBooleanProperty("org.apache.logging.log4j.simplelog.showContextMap", false);
    private final boolean showLogName = this.props.getBooleanProperty("org.apache.logging.log4j.simplelog.showlogname", false);
    private final boolean showShortName = this.props.getBooleanProperty("org.apache.logging.log4j.simplelog.showShortLogname", true);
    private final boolean showDateTime = this.props.getBooleanProperty("org.apache.logging.log4j.simplelog.showdatetime", false);

    public SimpleLoggerContext() {
        PrintStream ps;
        String lvl = this.props.getStringProperty("org.apache.logging.log4j.simplelog.level");
        this.defaultLevel = Level.toLevel(lvl, Level.ERROR);
        this.dateTimeFormat = this.showDateTime ? this.props.getStringProperty("org.apache.logging.log4j.simplelog.dateTimeFormat", DEFAULT_DATE_TIME_FORMAT) : null;
        String fileName = this.props.getStringProperty("org.apache.logging.log4j.simplelog.logFile", SYSTEM_ERR);
        if (SYSTEM_ERR.equalsIgnoreCase(fileName)) {
            ps = System.err;
        } else if (SYSTEM_OUT.equalsIgnoreCase(fileName)) {
            ps = System.out;
        } else {
            try {
                FileOutputStream os = new FileOutputStream(fileName);
                ps = new PrintStream(os);
            } catch (FileNotFoundException e) {
                ps = System.err;
            }
        }
        this.stream = ps;
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public ExtendedLogger getLogger(String name) {
        return getLogger(name, null);
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public ExtendedLogger getLogger(String name, MessageFactory messageFactory) {
        ExtendedLogger extendedLogger = this.loggerRegistry.getLogger(name, messageFactory);
        if (extendedLogger != null) {
            AbstractLogger.checkMessageFactory(extendedLogger, messageFactory);
            return extendedLogger;
        }
        SimpleLogger simpleLogger = new SimpleLogger(name, this.defaultLevel, this.showLogName, this.showShortName, this.showDateTime, this.showContextMap, this.dateTimeFormat, messageFactory, this.props, this.stream);
        this.loggerRegistry.putIfAbsent(name, messageFactory, simpleLogger);
        return this.loggerRegistry.getLogger(name, messageFactory);
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public boolean hasLogger(String name) {
        return false;
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public boolean hasLogger(String name, MessageFactory messageFactory) {
        return false;
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public boolean hasLogger(String name, Class<? extends MessageFactory> messageFactoryClass) {
        return false;
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public Object getExternalContext() {
        return null;
    }
}