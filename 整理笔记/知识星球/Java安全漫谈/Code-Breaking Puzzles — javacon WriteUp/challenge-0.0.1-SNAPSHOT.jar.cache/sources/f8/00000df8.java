package org.apache.tomcat.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.websocket.RemoteEndpoint;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsRemoteEndpointBase.class */
public abstract class WsRemoteEndpointBase implements RemoteEndpoint {
    protected final WsRemoteEndpointImplBase base;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WsRemoteEndpointBase(WsRemoteEndpointImplBase base) {
        this.base = base;
    }

    @Override // javax.websocket.RemoteEndpoint
    public final void setBatchingAllowed(boolean batchingAllowed) throws IOException {
        this.base.setBatchingAllowed(batchingAllowed);
    }

    @Override // javax.websocket.RemoteEndpoint
    public final boolean getBatchingAllowed() {
        return this.base.getBatchingAllowed();
    }

    @Override // javax.websocket.RemoteEndpoint
    public final void flushBatch() throws IOException {
        this.base.flushBatch();
    }

    @Override // javax.websocket.RemoteEndpoint
    public final void sendPing(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        this.base.sendPing(applicationData);
    }

    @Override // javax.websocket.RemoteEndpoint
    public final void sendPong(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        this.base.sendPong(applicationData);
    }
}