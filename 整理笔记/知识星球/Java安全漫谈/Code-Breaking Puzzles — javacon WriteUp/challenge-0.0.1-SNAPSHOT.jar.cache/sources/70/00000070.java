package ch.qos.logback.classic.pattern;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/pattern/ThrowableHandlingConverter.class */
public abstract class ThrowableHandlingConverter extends ClassicConverter {
    boolean handlesThrowable() {
        return true;
    }
}