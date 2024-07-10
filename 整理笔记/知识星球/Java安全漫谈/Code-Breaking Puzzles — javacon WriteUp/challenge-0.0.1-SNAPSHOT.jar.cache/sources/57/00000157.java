package ch.qos.logback.core.net.server;

import ch.qos.logback.core.net.server.Client;
import java.io.Closeable;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/net/server/ServerListener.class */
public interface ServerListener<T extends Client> extends Closeable {
    T acceptClient() throws IOException, InterruptedException;

    @Override // java.io.Closeable, java.lang.AutoCloseable
    void close();
}