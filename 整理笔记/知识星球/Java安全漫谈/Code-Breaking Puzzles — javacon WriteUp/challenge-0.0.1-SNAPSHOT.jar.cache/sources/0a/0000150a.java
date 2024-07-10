package org.springframework.boot;

import java.lang.Thread;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/SpringBootExceptionHandler.class */
public class SpringBootExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final Set<String> LOG_CONFIGURATION_MESSAGES;
    private static LoggedExceptionHandlerThreadLocal handler;
    private final Thread.UncaughtExceptionHandler parent;
    private final List<Throwable> loggedExceptions = new ArrayList();
    private int exitCode = 0;

    static {
        Set<String> messages = new HashSet<>();
        messages.add("Logback configuration error detected");
        LOG_CONFIGURATION_MESSAGES = Collections.unmodifiableSet(messages);
        handler = new LoggedExceptionHandlerThreadLocal();
    }

    SpringBootExceptionHandler(Thread.UncaughtExceptionHandler parent) {
        this.parent = parent;
    }

    public void registerLoggedException(Throwable exception) {
        this.loggedExceptions.add(exception);
    }

    public void registerExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            if (isPassedToParent(ex) && this.parent != null) {
                this.parent.uncaughtException(thread, ex);
            }
        } finally {
            this.loggedExceptions.clear();
            if (this.exitCode != 0) {
                System.exit(this.exitCode);
            }
        }
    }

    private boolean isPassedToParent(Throwable ex) {
        return isLogConfigurationMessage(ex) || !isRegistered(ex);
    }

    private boolean isLogConfigurationMessage(Throwable ex) {
        if (ex instanceof InvocationTargetException) {
            return isLogConfigurationMessage(ex.getCause());
        }
        String message = ex.getMessage();
        if (message != null) {
            for (String candidate : LOG_CONFIGURATION_MESSAGES) {
                if (message.contains(candidate)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private boolean isRegistered(Throwable ex) {
        if (this.loggedExceptions.contains(ex)) {
            return true;
        }
        if (ex instanceof InvocationTargetException) {
            return isRegistered(ex.getCause());
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SpringBootExceptionHandler forCurrentThread() {
        return handler.get();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/SpringBootExceptionHandler$LoggedExceptionHandlerThreadLocal.class */
    private static class LoggedExceptionHandlerThreadLocal extends ThreadLocal<SpringBootExceptionHandler> {
        private LoggedExceptionHandlerThreadLocal() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public SpringBootExceptionHandler initialValue() {
            SpringBootExceptionHandler handler = new SpringBootExceptionHandler(Thread.currentThread().getUncaughtExceptionHandler());
            Thread.currentThread().setUncaughtExceptionHandler(handler);
            return handler;
        }
    }
}