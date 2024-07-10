package org.jboss.logging;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jboss.logmanager.LogContext;
import org.jboss.logmanager.Logger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/JBossLogManagerProvider.class */
public final class JBossLogManagerProvider implements LoggerProvider {
    private static final Logger.AttachmentKey<Logger> KEY = new Logger.AttachmentKey<>();
    private static final Logger.AttachmentKey<ConcurrentMap<String, Logger>> LEGACY_KEY = new Logger.AttachmentKey<>();

    @Override // org.jboss.logging.LoggerProvider
    public Logger getLogger(final String name) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            return (Logger) AccessController.doPrivileged(new PrivilegedAction<Logger>() { // from class: org.jboss.logging.JBossLogManagerProvider.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedAction
                public Logger run() {
                    try {
                        return JBossLogManagerProvider.doGetLogger(name);
                    } catch (NoSuchMethodError e) {
                        return JBossLogManagerProvider.doLegacyGetLogger(name);
                    }
                }
            });
        }
        try {
            return doGetLogger(name);
        } catch (NoSuchMethodError e) {
            return doLegacyGetLogger(name);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Logger doLegacyGetLogger(String name) {
        org.jboss.logmanager.Logger lmLogger = LogContext.getLogContext().getLogger("");
        ConcurrentMap<String, Logger> loggers = (ConcurrentMap) lmLogger.getAttachment(LEGACY_KEY);
        if (loggers == null) {
            loggers = new ConcurrentHashMap<>();
            ConcurrentMap<String, Logger> appearing = (ConcurrentMap) lmLogger.attachIfAbsent(LEGACY_KEY, loggers);
            if (appearing != null) {
                loggers = appearing;
            }
        }
        Logger l = loggers.get(name);
        if (l != null) {
            return l;
        }
        org.jboss.logmanager.Logger logger = org.jboss.logmanager.Logger.getLogger(name);
        Logger l2 = new JBossLogManagerLogger(name, logger);
        Logger appearing2 = loggers.putIfAbsent(name, l2);
        if (appearing2 == null) {
            return l2;
        }
        return appearing2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Logger doGetLogger(String name) {
        Logger l = (Logger) LogContext.getLogContext().getAttachment(name, KEY);
        if (l != null) {
            return l;
        }
        org.jboss.logmanager.Logger logger = org.jboss.logmanager.Logger.getLogger(name);
        Logger l2 = new JBossLogManagerLogger(name, logger);
        Logger a = (Logger) logger.attachIfAbsent(KEY, l2);
        if (a == null) {
            return l2;
        }
        return a;
    }

    @Override // org.jboss.logging.LoggerProvider
    public void clearMdc() {
        org.jboss.logmanager.MDC.clear();
    }

    @Override // org.jboss.logging.LoggerProvider
    public Object putMdc(String key, Object value) {
        return org.jboss.logmanager.MDC.put(key, String.valueOf(value));
    }

    @Override // org.jboss.logging.LoggerProvider
    public Object getMdc(String key) {
        return org.jboss.logmanager.MDC.get(key);
    }

    @Override // org.jboss.logging.LoggerProvider
    public void removeMdc(String key) {
        org.jboss.logmanager.MDC.remove(key);
    }

    @Override // org.jboss.logging.LoggerProvider
    public Map<String, Object> getMdcMap() {
        return org.jboss.logmanager.MDC.copy();
    }

    @Override // org.jboss.logging.LoggerProvider
    public void clearNdc() {
        org.jboss.logmanager.NDC.clear();
    }

    @Override // org.jboss.logging.LoggerProvider
    public String getNdc() {
        return org.jboss.logmanager.NDC.get();
    }

    @Override // org.jboss.logging.LoggerProvider
    public int getNdcDepth() {
        return org.jboss.logmanager.NDC.getDepth();
    }

    @Override // org.jboss.logging.LoggerProvider
    public String popNdc() {
        return org.jboss.logmanager.NDC.pop();
    }

    @Override // org.jboss.logging.LoggerProvider
    public String peekNdc() {
        return org.jboss.logmanager.NDC.get();
    }

    @Override // org.jboss.logging.LoggerProvider
    public void pushNdc(String message) {
        org.jboss.logmanager.NDC.push(message);
    }

    @Override // org.jboss.logging.LoggerProvider
    public void setNdcMaxDepth(int maxDepth) {
        org.jboss.logmanager.NDC.trimTo(maxDepth);
    }
}