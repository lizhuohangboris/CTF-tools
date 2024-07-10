package org.jboss.logging;

import java.util.Collections;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/Slf4jLoggerProvider.class */
public final class Slf4jLoggerProvider extends AbstractLoggerProvider implements LoggerProvider {
    @Override // org.jboss.logging.LoggerProvider
    public Logger getLogger(String name) {
        org.slf4j.Logger l = LoggerFactory.getLogger(name);
        try {
            return new Slf4jLocationAwareLogger(name, (LocationAwareLogger) l);
        } catch (Throwable th) {
            return new Slf4jLogger(name, l);
        }
    }

    @Override // org.jboss.logging.LoggerProvider
    public void clearMdc() {
        org.slf4j.MDC.clear();
    }

    @Override // org.jboss.logging.LoggerProvider
    public Object putMdc(String key, Object value) {
        try {
            String str = org.slf4j.MDC.get(key);
            if (value == null) {
                org.slf4j.MDC.remove(key);
            } else {
                org.slf4j.MDC.put(key, String.valueOf(value));
            }
            return str;
        } catch (Throwable th) {
            if (value == null) {
                org.slf4j.MDC.remove(key);
            } else {
                org.slf4j.MDC.put(key, String.valueOf(value));
            }
            throw th;
        }
    }

    @Override // org.jboss.logging.LoggerProvider
    public Object getMdc(String key) {
        return org.slf4j.MDC.get(key);
    }

    @Override // org.jboss.logging.LoggerProvider
    public void removeMdc(String key) {
        org.slf4j.MDC.remove(key);
    }

    @Override // org.jboss.logging.LoggerProvider
    public Map<String, Object> getMdcMap() {
        Map<String, Object> map = org.slf4j.MDC.getCopyOfContextMap();
        return map == null ? Collections.emptyMap() : map;
    }
}