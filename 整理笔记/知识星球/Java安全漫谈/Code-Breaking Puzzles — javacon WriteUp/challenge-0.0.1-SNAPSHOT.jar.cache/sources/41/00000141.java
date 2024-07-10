package ch.qos.logback.core.net;

import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/net/ObjectWriter.class */
public interface ObjectWriter {
    void write(Object obj) throws IOException;
}