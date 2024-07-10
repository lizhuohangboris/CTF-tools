package ch.qos.logback.core.rolling;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.spi.LifeCycle;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/rolling/RollingPolicy.class */
public interface RollingPolicy extends LifeCycle {
    void rollover() throws RolloverFailure;

    String getActiveFileName();

    CompressionMode getCompressionMode();

    void setParent(FileAppender<?> fileAppender);
}