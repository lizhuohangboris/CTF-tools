package org.apache.logging.log4j.status;

import java.io.Closeable;
import java.util.EventListener;
import org.apache.logging.log4j.Level;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/status/StatusListener.class */
public interface StatusListener extends Closeable, EventListener {
    void log(StatusData statusData);

    Level getStatusLevel();
}