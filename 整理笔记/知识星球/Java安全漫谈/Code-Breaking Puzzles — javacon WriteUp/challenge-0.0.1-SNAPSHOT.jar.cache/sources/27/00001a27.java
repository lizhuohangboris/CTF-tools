package org.springframework.boot.logging.log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;
import org.springframework.boot.logging.AbstractLoggingSystem;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.logging.Slf4JLoggingSystem;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/logging/log4j2/Log4J2LoggingSystem.class */
public class Log4J2LoggingSystem extends Slf4JLoggingSystem {
    private static final String FILE_PROTOCOL = "file";
    private static final AbstractLoggingSystem.LogLevels<Level> LEVELS = new AbstractLoggingSystem.LogLevels<>();
    private static final Filter FILTER;

    static {
        LEVELS.map(LogLevel.TRACE, Level.TRACE);
        LEVELS.map(LogLevel.DEBUG, Level.DEBUG);
        LEVELS.map(LogLevel.INFO, Level.INFO);
        LEVELS.map(LogLevel.WARN, Level.WARN);
        LEVELS.map(LogLevel.ERROR, Level.ERROR);
        LEVELS.map(LogLevel.FATAL, Level.FATAL);
        LEVELS.map(LogLevel.OFF, Level.OFF);
        FILTER = new AbstractFilter() { // from class: org.springframework.boot.logging.log4j2.Log4J2LoggingSystem.1
            public Filter.Result filter(LogEvent event) {
                return Filter.Result.DENY;
            }

            public Filter.Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
                return Filter.Result.DENY;
            }

            public Filter.Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
                return Filter.Result.DENY;
            }

            public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
                return Filter.Result.DENY;
            }
        };
    }

    public Log4J2LoggingSystem(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem
    protected String[] getStandardConfigLocations() {
        return getCurrentlySupportedConfigLocations();
    }

    private String[] getCurrentlySupportedConfigLocations() {
        List<String> supportedConfigLocations = new ArrayList<>();
        if (isClassAvailable("com.fasterxml.jackson.dataformat.yaml.YAMLParser")) {
            Collections.addAll(supportedConfigLocations, "log4j2.yaml", "log4j2.yml");
        }
        if (isClassAvailable("com.fasterxml.jackson.databind.ObjectMapper")) {
            Collections.addAll(supportedConfigLocations, "log4j2.json", "log4j2.jsn");
        }
        supportedConfigLocations.add("log4j2.xml");
        return StringUtils.toStringArray(supportedConfigLocations);
    }

    protected boolean isClassAvailable(String className) {
        return ClassUtils.isPresent(className, getClassLoader());
    }

    @Override // org.springframework.boot.logging.Slf4JLoggingSystem, org.springframework.boot.logging.AbstractLoggingSystem, org.springframework.boot.logging.LoggingSystem
    public void beforeInitialize() {
        LoggerContext loggerContext = getLoggerContext();
        if (isAlreadyInitialized(loggerContext)) {
            return;
        }
        super.beforeInitialize();
        loggerContext.getConfiguration().addFilter(FILTER);
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem, org.springframework.boot.logging.LoggingSystem
    public void initialize(LoggingInitializationContext initializationContext, String configLocation, LogFile logFile) {
        LoggerContext loggerContext = getLoggerContext();
        if (isAlreadyInitialized(loggerContext)) {
            return;
        }
        loggerContext.getConfiguration().removeFilter(FILTER);
        super.initialize(initializationContext, configLocation, logFile);
        markAsInitialized(loggerContext);
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem
    protected void loadDefaults(LoggingInitializationContext initializationContext, LogFile logFile) {
        if (logFile != null) {
            loadConfiguration(getPackagedConfigFile("log4j2-file.xml"), logFile);
        } else {
            loadConfiguration(getPackagedConfigFile("log4j2.xml"), logFile);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.logging.Slf4JLoggingSystem, org.springframework.boot.logging.AbstractLoggingSystem
    public void loadConfiguration(LoggingInitializationContext initializationContext, String location, LogFile logFile) {
        super.loadConfiguration(initializationContext, location, logFile);
        loadConfiguration(location, logFile);
    }

    protected void loadConfiguration(String location, LogFile logFile) {
        Assert.notNull(location, "Location must not be null");
        try {
            LoggerContext ctx = getLoggerContext();
            URL url = ResourceUtils.getURL(location);
            ConfigurationSource source = getConfigurationSource(url);
            ctx.start(ConfigurationFactory.getInstance().getConfiguration(ctx, source));
        } catch (Exception ex) {
            throw new IllegalStateException("Could not initialize Log4J2 logging from " + location, ex);
        }
    }

    private ConfigurationSource getConfigurationSource(URL url) throws IOException {
        InputStream stream = url.openStream();
        if ("file".equals(url.getProtocol())) {
            return new ConfigurationSource(stream, ResourceUtils.getFile(url));
        }
        return new ConfigurationSource(stream, url);
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem
    protected void reinitialize(LoggingInitializationContext initializationContext) {
        getLoggerContext().reconfigure();
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public Set<LogLevel> getSupportedLogLevels() {
        return LEVELS.getSupported();
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public void setLogLevel(String loggerName, LogLevel logLevel) {
        Level level = LEVELS.convertSystemToNative(logLevel);
        LoggerConfig loggerConfig = getLoggerConfig(loggerName);
        if (loggerConfig == null) {
            getLoggerContext().getConfiguration().addLogger(loggerName, new LoggerConfig(loggerName, level, true));
        } else {
            loggerConfig.setLevel(level);
        }
        getLoggerContext().updateLoggers();
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public List<LoggerConfiguration> getLoggerConfigurations() {
        List<LoggerConfiguration> result = new ArrayList<>();
        Configuration configuration = getLoggerContext().getConfiguration();
        for (LoggerConfig loggerConfig : configuration.getLoggers().values()) {
            result.add(convertLoggerConfiguration(loggerConfig));
        }
        result.sort(CONFIGURATION_COMPARATOR);
        return result;
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public LoggerConfiguration getLoggerConfiguration(String loggerName) {
        return convertLoggerConfiguration(getLoggerConfig(loggerName));
    }

    private LoggerConfiguration convertLoggerConfiguration(LoggerConfig loggerConfig) {
        if (loggerConfig == null) {
            return null;
        }
        LogLevel level = LEVELS.convertNativeToSystem(loggerConfig.getLevel());
        String name = loggerConfig.getName();
        name = (!StringUtils.hasLength(name) || "".equals(name)) ? "ROOT" : "ROOT";
        return new LoggerConfiguration(name, level, level);
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public Runnable getShutdownHandler() {
        return new ShutdownHandler();
    }

    @Override // org.springframework.boot.logging.Slf4JLoggingSystem, org.springframework.boot.logging.LoggingSystem
    public void cleanUp() {
        super.cleanUp();
        LoggerContext loggerContext = getLoggerContext();
        markAsUninitialized(loggerContext);
        loggerContext.getConfiguration().removeFilter(FILTER);
    }

    private LoggerConfig getLoggerConfig(String name) {
        name = (!StringUtils.hasLength(name) || "ROOT".equals(name)) ? "" : "";
        return (LoggerConfig) getLoggerContext().getConfiguration().getLoggers().get(name);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public LoggerContext getLoggerContext() {
        return LogManager.getContext(false);
    }

    private boolean isAlreadyInitialized(LoggerContext loggerContext) {
        return LoggingSystem.class.getName().equals(loggerContext.getExternalContext());
    }

    private void markAsInitialized(LoggerContext loggerContext) {
        loggerContext.setExternalContext(LoggingSystem.class.getName());
    }

    private void markAsUninitialized(LoggerContext loggerContext) {
        loggerContext.setExternalContext((Object) null);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/logging/log4j2/Log4J2LoggingSystem$ShutdownHandler.class */
    private final class ShutdownHandler implements Runnable {
        private ShutdownHandler() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Log4J2LoggingSystem.this.getLoggerContext().stop();
        }
    }
}