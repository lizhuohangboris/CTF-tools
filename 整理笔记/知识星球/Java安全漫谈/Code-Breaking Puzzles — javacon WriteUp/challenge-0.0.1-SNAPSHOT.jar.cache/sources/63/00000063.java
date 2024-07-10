package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/pattern/LoggerConverter.class */
public class LoggerConverter extends NamedConverter {
    @Override // ch.qos.logback.classic.pattern.NamedConverter
    protected String getFullyQualifiedName(ILoggingEvent event) {
        return event.getLoggerName();
    }
}