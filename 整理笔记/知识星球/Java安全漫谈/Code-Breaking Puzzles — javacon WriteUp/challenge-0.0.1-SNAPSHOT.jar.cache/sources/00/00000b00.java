package org.apache.logging.log4j;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.apache.logging.log4j.spi.ExtendedLogger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/EventLogger.class */
public final class EventLogger {
    public static final Marker EVENT_MARKER = MarkerManager.getMarker("EVENT");
    private static final String FQCN = EventLogger.class.getName();
    private static final String NAME = "EventLogger";
    private static final ExtendedLogger LOGGER = LogManager.getContext(false).getLogger(NAME);

    private EventLogger() {
    }

    public static void logEvent(StructuredDataMessage msg) {
        LOGGER.logIfEnabled(FQCN, Level.OFF, EVENT_MARKER, (Message) msg, (Throwable) null);
    }

    public static void logEvent(StructuredDataMessage msg, Level level) {
        LOGGER.logIfEnabled(FQCN, level, EVENT_MARKER, (Message) msg, (Throwable) null);
    }
}