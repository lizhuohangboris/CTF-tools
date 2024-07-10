package org.jboss.logging;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/NDC.class */
public final class NDC {
    private NDC() {
    }

    public static void clear() {
        LoggerProviders.PROVIDER.clearNdc();
    }

    public static String get() {
        return LoggerProviders.PROVIDER.getNdc();
    }

    public static int getDepth() {
        return LoggerProviders.PROVIDER.getNdcDepth();
    }

    public static String pop() {
        return LoggerProviders.PROVIDER.popNdc();
    }

    public static String peek() {
        return LoggerProviders.PROVIDER.peekNdc();
    }

    public static void push(String message) {
        LoggerProviders.PROVIDER.pushNdc(message);
    }

    public static void setMaxDepth(int maxDepth) {
        LoggerProviders.PROVIDER.setNdcMaxDepth(maxDepth);
    }
}