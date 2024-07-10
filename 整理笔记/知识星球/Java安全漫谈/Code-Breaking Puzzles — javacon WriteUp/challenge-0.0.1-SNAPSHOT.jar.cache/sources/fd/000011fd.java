package org.jboss.logging;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/JDKLoggerProvider.class */
public final class JDKLoggerProvider extends AbstractMdcLoggerProvider implements LoggerProvider {
    @Override // org.jboss.logging.LoggerProvider
    public Logger getLogger(String name) {
        return new JDKLogger(name);
    }
}