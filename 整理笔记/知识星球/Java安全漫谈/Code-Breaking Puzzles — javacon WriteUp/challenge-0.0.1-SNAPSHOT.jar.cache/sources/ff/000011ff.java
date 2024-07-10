package org.jboss.logging;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.ThreadContext;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/Log4j2LoggerProvider.class */
public final class Log4j2LoggerProvider implements LoggerProvider {
    @Override // org.jboss.logging.LoggerProvider
    public Log4j2Logger getLogger(String name) {
        return new Log4j2Logger(name);
    }

    @Override // org.jboss.logging.LoggerProvider
    public void clearMdc() {
        ThreadContext.clearMap();
    }

    @Override // org.jboss.logging.LoggerProvider
    public Object putMdc(String key, Object value) {
        try {
            String str = ThreadContext.get(key);
            ThreadContext.put(key, String.valueOf(value));
            return str;
        } catch (Throwable th) {
            ThreadContext.put(key, String.valueOf(value));
            throw th;
        }
    }

    @Override // org.jboss.logging.LoggerProvider
    public Object getMdc(String key) {
        return ThreadContext.get(key);
    }

    @Override // org.jboss.logging.LoggerProvider
    public void removeMdc(String key) {
        ThreadContext.remove(key);
    }

    @Override // org.jboss.logging.LoggerProvider
    public Map<String, Object> getMdcMap() {
        return new HashMap(ThreadContext.getImmutableContext());
    }

    @Override // org.jboss.logging.LoggerProvider
    public void clearNdc() {
        ThreadContext.clearStack();
    }

    @Override // org.jboss.logging.LoggerProvider
    public String getNdc() {
        return ThreadContext.peek();
    }

    @Override // org.jboss.logging.LoggerProvider
    public int getNdcDepth() {
        return ThreadContext.getDepth();
    }

    @Override // org.jboss.logging.LoggerProvider
    public String popNdc() {
        return ThreadContext.pop();
    }

    @Override // org.jboss.logging.LoggerProvider
    public String peekNdc() {
        return ThreadContext.peek();
    }

    @Override // org.jboss.logging.LoggerProvider
    public void pushNdc(String message) {
        ThreadContext.push(message);
    }

    @Override // org.jboss.logging.LoggerProvider
    public void setNdcMaxDepth(int maxDepth) {
        ThreadContext.trim(maxDepth);
    }
}