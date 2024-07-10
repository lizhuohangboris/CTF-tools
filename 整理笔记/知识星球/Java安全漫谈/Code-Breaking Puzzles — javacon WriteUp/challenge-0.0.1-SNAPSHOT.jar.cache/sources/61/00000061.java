package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/pattern/LineSeparatorConverter.class */
public class LineSeparatorConverter extends ClassicConverter {
    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent event) {
        return CoreConstants.LINE_SEPARATOR;
    }
}