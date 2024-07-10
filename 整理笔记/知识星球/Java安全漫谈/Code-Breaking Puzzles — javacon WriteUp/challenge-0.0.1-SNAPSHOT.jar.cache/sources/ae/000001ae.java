package ch.qos.logback.core.rolling;

import ch.qos.logback.core.spi.LifeCycle;
import java.io.File;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/rolling/TriggeringPolicy.class */
public interface TriggeringPolicy<E> extends LifeCycle {
    boolean isTriggeringEvent(File file, E e);
}