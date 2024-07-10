package org.springframework.boot.context.logging;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.logging.LoggingSystemProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/logging/LoggingApplicationListener.class */
public class LoggingApplicationListener implements GenericApplicationListener {
    private static final ConfigurationPropertyName LOGGING_LEVEL = ConfigurationPropertyName.of("logging.level");
    private static final ConfigurationPropertyName LOGGING_GROUP = ConfigurationPropertyName.of("logging.group");
    private static final Bindable<Map<String, String>> STRING_STRING_MAP = Bindable.mapOf(String.class, String.class);
    private static final Bindable<Map<String, String[]>> STRING_STRINGS_MAP = Bindable.mapOf(String.class, String[].class);
    public static final int DEFAULT_ORDER = -2147483628;
    public static final String CONFIG_PROPERTY = "logging.config";
    public static final String REGISTER_SHUTDOWN_HOOK_PROPERTY = "logging.register-shutdown-hook";
    public static final String LOGGING_SYSTEM_BEAN_NAME = "springBootLoggingSystem";
    private static final Map<String, List<String>> DEFAULT_GROUP_LOGGERS;
    private static final Map<LogLevel, List<String>> LOG_LEVEL_LOGGERS;
    private static final Class<?>[] EVENT_TYPES;
    private static final Class<?>[] SOURCE_TYPES;
    private static final AtomicBoolean shutdownHookRegistered;
    private LoggingSystem loggingSystem;
    private final Log logger = LogFactory.getLog(getClass());
    private int order = -2147483628;
    private boolean parseArgs = true;
    private LogLevel springBootLogging = null;

    static {
        MultiValueMap<String, String> loggers = new LinkedMultiValueMap<>();
        loggers.add("web", "org.springframework.core.codec");
        loggers.add("web", "org.springframework.http");
        loggers.add("web", "org.springframework.web");
        loggers.add("sql", "org.springframework.jdbc.core");
        loggers.add("sql", "org.hibernate.SQL");
        DEFAULT_GROUP_LOGGERS = Collections.unmodifiableMap(loggers);
        MultiValueMap<LogLevel, String> loggers2 = new LinkedMultiValueMap<>();
        loggers2.add(LogLevel.DEBUG, "sql");
        loggers2.add(LogLevel.DEBUG, "web");
        loggers2.add(LogLevel.DEBUG, "org.springframework.boot");
        loggers2.add(LogLevel.TRACE, "org.springframework");
        loggers2.add(LogLevel.TRACE, "org.apache.tomcat");
        loggers2.add(LogLevel.TRACE, "org.apache.catalina");
        loggers2.add(LogLevel.TRACE, "org.eclipse.jetty");
        loggers2.add(LogLevel.TRACE, "org.hibernate.tool.hbm2ddl");
        LOG_LEVEL_LOGGERS = Collections.unmodifiableMap(loggers2);
        EVENT_TYPES = new Class[]{ApplicationStartingEvent.class, ApplicationEnvironmentPreparedEvent.class, ApplicationPreparedEvent.class, ContextClosedEvent.class, ApplicationFailedEvent.class};
        SOURCE_TYPES = new Class[]{SpringApplication.class, ApplicationContext.class};
        shutdownHookRegistered = new AtomicBoolean(false);
    }

    @Override // org.springframework.context.event.GenericApplicationListener
    public boolean supportsEventType(ResolvableType resolvableType) {
        return isAssignableFrom(resolvableType.getRawClass(), EVENT_TYPES);
    }

    @Override // org.springframework.context.event.GenericApplicationListener
    public boolean supportsSourceType(Class<?> sourceType) {
        return isAssignableFrom(sourceType, SOURCE_TYPES);
    }

    private boolean isAssignableFrom(Class<?> type, Class<?>... supportedTypes) {
        if (type != null) {
            for (Class<?> supportedType : supportedTypes) {
                if (supportedType.isAssignableFrom(type)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationStartingEvent) {
            onApplicationStartingEvent((ApplicationStartingEvent) event);
        } else if (event instanceof ApplicationEnvironmentPreparedEvent) {
            onApplicationEnvironmentPreparedEvent((ApplicationEnvironmentPreparedEvent) event);
        } else if (event instanceof ApplicationPreparedEvent) {
            onApplicationPreparedEvent((ApplicationPreparedEvent) event);
        } else if ((event instanceof ContextClosedEvent) && ((ContextClosedEvent) event).getApplicationContext().getParent() == null) {
            onContextClosedEvent();
        } else if (event instanceof ApplicationFailedEvent) {
            onApplicationFailedEvent();
        }
    }

    private void onApplicationStartingEvent(ApplicationStartingEvent event) {
        this.loggingSystem = LoggingSystem.get(event.getSpringApplication().getClassLoader());
        this.loggingSystem.beforeInitialize();
    }

    private void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
        if (this.loggingSystem == null) {
            this.loggingSystem = LoggingSystem.get(event.getSpringApplication().getClassLoader());
        }
        initialize(event.getEnvironment(), event.getSpringApplication().getClassLoader());
    }

    private void onApplicationPreparedEvent(ApplicationPreparedEvent event) {
        ConfigurableListableBeanFactory beanFactory = event.getApplicationContext().getBeanFactory();
        if (!beanFactory.containsBean(LOGGING_SYSTEM_BEAN_NAME)) {
            beanFactory.registerSingleton(LOGGING_SYSTEM_BEAN_NAME, this.loggingSystem);
        }
    }

    private void onContextClosedEvent() {
        if (this.loggingSystem != null) {
            this.loggingSystem.cleanUp();
        }
    }

    private void onApplicationFailedEvent() {
        if (this.loggingSystem != null) {
            this.loggingSystem.cleanUp();
        }
    }

    protected void initialize(ConfigurableEnvironment environment, ClassLoader classLoader) {
        new LoggingSystemProperties(environment).apply();
        LogFile logFile = LogFile.get(environment);
        if (logFile != null) {
            logFile.applyToSystemProperties();
        }
        initializeEarlyLoggingLevel(environment);
        initializeSystem(environment, this.loggingSystem, logFile);
        initializeFinalLoggingLevels(environment, this.loggingSystem);
        registerShutdownHookIfNecessary(environment, this.loggingSystem);
    }

    private void initializeEarlyLoggingLevel(ConfigurableEnvironment environment) {
        if (this.parseArgs && this.springBootLogging == null) {
            if (isSet(environment, "debug")) {
                this.springBootLogging = LogLevel.DEBUG;
            }
            if (isSet(environment, "trace")) {
                this.springBootLogging = LogLevel.TRACE;
            }
        }
    }

    private boolean isSet(ConfigurableEnvironment environment, String property) {
        String value = environment.getProperty(property);
        return (value == null || value.equals("false")) ? false : true;
    }

    private void initializeSystem(ConfigurableEnvironment environment, LoggingSystem system, LogFile logFile) {
        LoggingInitializationContext initializationContext = new LoggingInitializationContext(environment);
        String logConfig = environment.getProperty(CONFIG_PROPERTY);
        if (ignoreLogConfig(logConfig)) {
            system.initialize(initializationContext, null, logFile);
            return;
        }
        try {
            ResourceUtils.getURL(logConfig).openStream().close();
            system.initialize(initializationContext, logConfig, logFile);
        } catch (Exception ex) {
            System.err.println("Logging system failed to initialize using configuration from '" + logConfig + "'");
            ex.printStackTrace(System.err);
            throw new IllegalStateException(ex);
        }
    }

    private boolean ignoreLogConfig(String logConfig) {
        return !StringUtils.hasLength(logConfig) || logConfig.startsWith("-D");
    }

    private void initializeFinalLoggingLevels(ConfigurableEnvironment environment, LoggingSystem system) {
        if (this.springBootLogging != null) {
            initializeLogLevel(system, this.springBootLogging);
        }
        setLogLevels(system, environment);
    }

    protected void initializeLogLevel(LoggingSystem system, LogLevel level) {
        List<String> loggers = LOG_LEVEL_LOGGERS.get(level);
        if (loggers != null) {
            for (String logger : loggers) {
                system.setLogLevel(logger, level);
            }
        }
    }

    protected void setLogLevels(LoggingSystem system, Environment environment) {
        if (!(environment instanceof ConfigurableEnvironment)) {
            return;
        }
        Binder binder = Binder.get(environment);
        Map<String, String[]> groups = getGroups();
        binder.bind(LOGGING_GROUP, STRING_STRINGS_MAP.withExistingValue(groups));
        Map<String, String> levels = (Map) binder.bind(LOGGING_LEVEL, STRING_STRING_MAP).orElseGet(Collections::emptyMap);
        levels.forEach(name, level -> {
            String[] groupedNames = (String[]) groups.get(name);
            if (ObjectUtils.isEmpty((Object[]) groupedNames)) {
                setLogLevel(system, name, level);
            } else {
                setLogLevel(system, groupedNames, level);
            }
        });
    }

    private Map<String, String[]> getGroups() {
        Map<String, String[]> groups = new LinkedHashMap<>();
        DEFAULT_GROUP_LOGGERS.forEach(name, loggers -> {
            String[] strArr = (String[]) groups.put(name, StringUtils.toStringArray(loggers));
        });
        return groups;
    }

    private void setLogLevel(LoggingSystem system, String[] names, String level) {
        for (String name : names) {
            setLogLevel(system, name, level);
        }
    }

    private void setLogLevel(LoggingSystem system, String name, String level) {
        try {
            name = name.equalsIgnoreCase("ROOT") ? null : name;
            system.setLogLevel(name, coerceLogLevel(level));
        } catch (RuntimeException e) {
            this.logger.error("Cannot set level: " + level + " for '" + name + "'");
        }
    }

    private LogLevel coerceLogLevel(String level) {
        if ("false".equalsIgnoreCase(level)) {
            return LogLevel.OFF;
        }
        return LogLevel.valueOf(level.toUpperCase(Locale.ENGLISH));
    }

    private void registerShutdownHookIfNecessary(Environment environment, LoggingSystem loggingSystem) {
        Runnable shutdownHandler;
        boolean registerShutdownHook = ((Boolean) environment.getProperty(REGISTER_SHUTDOWN_HOOK_PROPERTY, Boolean.class, false)).booleanValue();
        if (registerShutdownHook && (shutdownHandler = loggingSystem.getShutdownHandler()) != null && shutdownHookRegistered.compareAndSet(false, true)) {
            registerShutdownHook(new Thread(shutdownHandler));
        }
    }

    void registerShutdownHook(Thread shutdownHook) {
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.context.event.GenericApplicationListener, org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setSpringBootLogging(LogLevel springBootLogging) {
        this.springBootLogging = springBootLogging;
    }

    public void setParseArgs(boolean parseArgs) {
        this.parseArgs = parseArgs;
    }
}