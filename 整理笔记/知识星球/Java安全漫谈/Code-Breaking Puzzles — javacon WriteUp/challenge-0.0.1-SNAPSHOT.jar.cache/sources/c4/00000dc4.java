package org.apache.tomcat.websocket;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/AsyncChannelWrapper.class */
public interface AsyncChannelWrapper {
    Future<Integer> read(ByteBuffer byteBuffer);

    <B, A extends B> void read(ByteBuffer byteBuffer, A a, CompletionHandler<Integer, B> completionHandler);

    Future<Integer> write(ByteBuffer byteBuffer);

    <B, A extends B> void write(ByteBuffer[] byteBufferArr, int i, int i2, long j, TimeUnit timeUnit, A a, CompletionHandler<Long, B> completionHandler);

    void close();

    Future<Void> handshake() throws SSLException;
}