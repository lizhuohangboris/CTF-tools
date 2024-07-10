package org.springframework.boot.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/logging/Slf4JLoggingSystem.class */
public abstract class Slf4JLoggingSystem extends AbstractLoggingSystem {
    private static final String BRIDGE_HANDLER = "org.slf4j.bridge.SLF4JBridgeHandler";

    public Slf4JLoggingSystem(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem, org.springframework.boot.logging.LoggingSystem
    public void beforeInitialize() {
        super.beforeInitialize();
        configureJdkLoggingBridgeHandler();
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public void cleanUp() {
        if (isBridgeHandlerAvailable()) {
            removeJdkLoggingBridgeHandler();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.logging.AbstractLoggingSystem
    public void loadConfiguration(LoggingInitializationContext initializationContext, String location, LogFile logFile) {
        Assert.notNull(location, "Location must not be null");
        if (initializationContext != null) {
            applySystemProperties(initializationContext.getEnvironment(), logFile);
        }
    }

    private void configureJdkLoggingBridgeHandler() {
        try {
            if (isBridgeJulIntoSlf4j()) {
                removeJdkLoggingBridgeHandler();
                SLF4JBridgeHandler.install();
            }
        } catch (Throwable th) {
        }
    }

    protected final boolean isBridgeJulIntoSlf4j() {
        return isBridgeHandlerAvailable() && isJulUsingASingleConsoleHandlerAtMost();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isBridgeHandlerAvailable() {
        return ClassUtils.isPresent(BRIDGE_HANDLER, getClassLoader());
    }

    private boolean isJulUsingASingleConsoleHandlerAtMost() {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        return handlers.length == 0 || (handlers.length == 1 && (handlers[0] instanceof ConsoleHandler));
    }

    private void removeJdkLoggingBridgeHandler() {
        try {
            removeDefaultRootHandler();
            SLF4JBridgeHandler.uninstall();
        } catch (Throwable th) {
        }
    }

    private void removeDefaultRootHandler() {
        try {
            Logger rootLogger = LogManager.getLogManager().getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            if (handlers.length == 1 && (handlers[0] instanceof ConsoleHandler)) {
                rootLogger.removeHandler(handlers[0]);
            }
        } catch (Throwable th) {
        }
    }
}