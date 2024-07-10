package org.apache.tomcat.websocket;

import java.nio.ByteBuffer;
import java.util.concurrent.Future;
import javax.websocket.RemoteEndpoint;
import javax.websocket.SendHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsRemoteEndpointAsync.class */
public class WsRemoteEndpointAsync extends WsRemoteEndpointBase implements RemoteEndpoint.Async {
    /* JADX INFO: Access modifiers changed from: package-private */
    public WsRemoteEndpointAsync(WsRemoteEndpointImplBase base) {
        super(base);
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public long getSendTimeout() {
        return this.base.getSendTimeout();
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public void setSendTimeout(long timeout) {
        this.base.setSendTimeout(timeout);
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public void sendText(String text, SendHandler completion) {
        this.base.sendStringByCompletion(text, completion);
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public Future<Void> sendText(String text) {
        return this.base.sendStringByFuture(text);
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public Future<Void> sendBinary(ByteBuffer data) {
        return this.base.sendBytesByFuture(data);
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public void sendBinary(ByteBuffer data, SendHandler completion) {
        this.base.sendBytesByCompletion(data, completion);
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public Future<Void> sendObject(Object obj) {
        return this.base.sendObjectByFuture(obj);
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public void sendObject(Object obj, SendHandler completion) {
        this.base.sendObjectByCompletion(obj, completion);
    }
}