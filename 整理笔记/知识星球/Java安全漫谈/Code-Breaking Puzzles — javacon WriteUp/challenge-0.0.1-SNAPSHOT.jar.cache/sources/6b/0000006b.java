package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/pattern/RelativeTimeConverter.class */
public class RelativeTimeConverter extends ClassicConverter {
    long lastTimestamp = -1;
    String timesmapCache = null;

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent event) {
        String str;
        long now = event.getTimeStamp();
        synchronized (this) {
            if (now != this.lastTimestamp) {
                this.lastTimestamp = now;
                this.timesmapCache = Long.toString(now - event.getLoggerContextVO().getBirthTime());
            }
            str = this.timesmapCache;
        }
        return str;
    }
}