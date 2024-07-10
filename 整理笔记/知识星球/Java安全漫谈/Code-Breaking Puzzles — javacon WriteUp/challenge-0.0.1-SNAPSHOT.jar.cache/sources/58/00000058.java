package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/pattern/ClassOfCallerConverter.class */
public class ClassOfCallerConverter extends NamedConverter {
    @Override // ch.qos.logback.classic.pattern.NamedConverter
    protected String getFullyQualifiedName(ILoggingEvent event) {
        StackTraceElement[] cda = event.getCallerData();
        if (cda != null && cda.length > 0) {
            return cda[0].getClassName();
        }
        return CallerData.NA;
    }
}