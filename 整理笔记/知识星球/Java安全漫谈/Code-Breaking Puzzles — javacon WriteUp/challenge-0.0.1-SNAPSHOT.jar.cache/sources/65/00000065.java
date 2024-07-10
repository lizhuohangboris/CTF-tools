package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Marker;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/pattern/MarkerConverter.class */
public class MarkerConverter extends ClassicConverter {
    private static String EMPTY = "";

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent le) {
        Marker marker = le.getMarker();
        if (marker == null) {
            return EMPTY;
        }
        return marker.toString();
    }
}