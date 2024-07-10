package ch.qos.logback.classic.boolex;

import ch.qos.logback.classic.spi.ILoggingEvent;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/boolex/IEvaluator.class */
public interface IEvaluator {
    boolean doEvaluate(ILoggingEvent iLoggingEvent);
}