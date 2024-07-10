package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/pattern/MessageConverter.class */
public class MessageConverter extends ClassicConverter {
    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent event) {
        return event.getFormattedMessage();
    }
}