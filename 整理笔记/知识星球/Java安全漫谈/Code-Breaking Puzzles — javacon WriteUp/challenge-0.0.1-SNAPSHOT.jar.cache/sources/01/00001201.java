package org.jboss.logging;

import java.util.Collections;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/Log4jLoggerProvider.class */
public final class Log4jLoggerProvider implements LoggerProvider {
    @Override // org.jboss.logging.LoggerProvider
    public Logger getLogger(String name) {
        return new Log4jLogger("".equals(name) ? "ROOT" : name);
    }

    @Override // org.jboss.logging.LoggerProvider
    public void clearMdc() {
        org.apache.log4j.MDC.clear();
    }

    @Override // org.jboss.logging.LoggerProvider
    public Object getMdc(String key) {
        return org.apache.log4j.MDC.get(key);
    }

    @Override // org.jboss.logging.LoggerProvider
    public Map<String, Object> getMdcMap() {
        Map<String, Object> map = org.apache.log4j.MDC.getContext();
        return map == null ? Collections.emptyMap() : map;
    }

    @Override // org.jboss.logging.LoggerProvider
    public Object putMdc(String key, Object val) {
        try {
            Object obj = org.apache.log4j.MDC.get(key);
            org.apache.log4j.MDC.put(key, val);
            return obj;
        } catch (Throwable th) {
            org.apache.log4j.MDC.put(key, val);
            throw th;
        }
    }

    @Override // org.jboss.logging.LoggerProvider
    public void removeMdc(String key) {
        org.apache.log4j.MDC.remove(key);
    }

    @Override // org.jboss.logging.LoggerProvider
    public void clearNdc() {
        org.apache.log4j.NDC.remove();
    }

    @Override // org.jboss.logging.LoggerProvider
    public String getNdc() {
        return org.apache.log4j.NDC.get();
    }

    @Override // org.jboss.logging.LoggerProvider
    public int getNdcDepth() {
        return org.apache.log4j.NDC.getDepth();
    }

    @Override // org.jboss.logging.LoggerProvider
    public String peekNdc() {
        return org.apache.log4j.NDC.peek();
    }

    @Override // org.jboss.logging.LoggerProvider
    public String popNdc() {
        return org.apache.log4j.NDC.pop();
    }

    @Override // org.jboss.logging.LoggerProvider
    public void pushNdc(String message) {
        org.apache.log4j.NDC.push(message);
    }

    @Override // org.jboss.logging.LoggerProvider
    public void setNdcMaxDepth(int maxDepth) {
        org.apache.log4j.NDC.setMaxDepth(maxDepth);
    }
}