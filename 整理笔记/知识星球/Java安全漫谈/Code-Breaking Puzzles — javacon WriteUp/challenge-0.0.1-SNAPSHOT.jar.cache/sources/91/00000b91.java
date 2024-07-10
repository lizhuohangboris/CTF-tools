package org.apache.logging.log4j.util;

import java.util.Stack;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/util/StackLocatorUtil.class */
public final class StackLocatorUtil {
    private static StackLocator stackLocator;

    static {
        stackLocator = null;
        stackLocator = StackLocator.getInstance();
    }

    private StackLocatorUtil() {
    }

    @PerformanceSensitive
    public static Class<?> getCallerClass(int depth) {
        return stackLocator.getCallerClass(depth + 1);
    }

    public static StackTraceElement getStackTraceElement(int depth) {
        return stackLocator.getStackTraceElement(depth + 1);
    }

    @PerformanceSensitive
    public static Class<?> getCallerClass(String fqcn) {
        return getCallerClass(fqcn, "");
    }

    @PerformanceSensitive
    public static Class<?> getCallerClass(String fqcn, String pkg) {
        return stackLocator.getCallerClass(fqcn, pkg);
    }

    @PerformanceSensitive
    public static Class<?> getCallerClass(Class<?> anchor) {
        return stackLocator.getCallerClass(anchor);
    }

    @PerformanceSensitive
    public static Stack<Class<?>> getCurrentStackTrace() {
        return stackLocator.getCurrentStackTrace();
    }

    public static StackTraceElement calcLocation(String fqcnOfLogger) {
        return stackLocator.calcLocation(fqcnOfLogger);
    }
}