package org.jboss.logging;

import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/MDC.class */
public final class MDC {
    private MDC() {
    }

    public static Object put(String key, Object val) {
        return LoggerProviders.PROVIDER.putMdc(key, val);
    }

    public static Object get(String key) {
        return LoggerProviders.PROVIDER.getMdc(key);
    }

    public static void remove(String key) {
        LoggerProviders.PROVIDER.removeMdc(key);
    }

    public static Map<String, Object> getMap() {
        return LoggerProviders.PROVIDER.getMdcMap();
    }

    public static void clear() {
        LoggerProviders.PROVIDER.clearMdc();
    }
}