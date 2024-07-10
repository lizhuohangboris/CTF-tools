package org.apache.tomcat.websocket.server;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Transformation;
import org.apache.tomcat.websocket.WsFrameBase;
import org.apache.tomcat.websocket.WsIOException;
import org.apache.tomcat.websocket.WsSession;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/server/WsFrameServer.class */
public class WsFrameServer extends WsFrameBase {
    private final Log log;
    private static final StringManager sm = StringManager.getManager(WsFrameServer.class);
    private final SocketWrapperBase<?> socketWrapper;
    private final ClassLoader applicationClassLoader;

    public WsFrameServer(SocketWrapperBase<?> socketWrapper, WsSession wsSession, Transformation transformation, ClassLoader applicationClassLoader) {
        super(wsSession, transformation);
        this.log = LogFactory.getLog(WsFrameServer.class);
        this.socketWrapper = socketWrapper;
        this.applicationClassLoader = applicationClassLoader;
    }

    private void onDataAvailable() throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("wsFrameServer.onDataAvailable");
        }
        if (isOpen() && this.inputBuffer.hasRemaining() && !isSuspended()) {
            processInputBuffer();
        }
        while (isOpen() && !isSuspended()) {
            this.inputBuffer.mark();
            this.inputBuffer.position(this.inputBuffer.limit()).limit(this.inputBuffer.capacity());
            int read = this.socketWrapper.read(false, this.inputBuffer);
            this.inputBuffer.limit(this.inputBuffer.position()).reset();
            if (read < 0) {
                throw new EOFException();
            }
            if (read == 0) {
                return;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("wsFrameServer.bytesRead", Integer.toString(read)));
            }
            processInputBuffer();
        }
    }

    @Override // org.apache.tomcat.websocket.WsFrameBase
    protected boolean isMasked() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.WsFrameBase
    public Transformation getTransformation() {
        return super.getTransformation();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.WsFrameBase
    public boolean isOpen() {
        return super.isOpen();
    }

    @Override // org.apache.tomcat.websocket.WsFrameBase
    protected Log getLog() {
        return this.log;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.WsFrameBase
    public void sendMessageText(boolean last) throws WsIOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.applicationClassLoader);
            super.sendMessageText(last);
            Thread.currentThread().setContextClassLoader(cl);
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(cl);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.WsFrameBase
    public void sendMessageBinary(ByteBuffer msg, boolean last) throws WsIOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.applicationClassLoader);
            super.sendMessageBinary(msg, last);
            Thread.currentThread().setContextClassLoader(cl);
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(cl);
            throw th;
        }
    }

    @Override // org.apache.tomcat.websocket.WsFrameBase
    protected void resumeProcessing() {
        this.socketWrapper.processSocket(SocketEvent.OPEN_READ, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractEndpoint.Handler.SocketState notifyDataAvailable() throws IOException {
        while (isOpen()) {
            switch (getReadState()) {
                case WAITING:
                    if (!changeReadState(WsFrameBase.ReadState.WAITING, WsFrameBase.ReadState.PROCESSING)) {
                        break;
                    } else {
                        try {
                            return doOnDataAvailable();
                        } catch (IOException e) {
                            changeReadState(WsFrameBase.ReadState.CLOSING);
                            throw e;
                        }
                    }
                case SUSPENDING_WAIT:
                    if (!changeReadState(WsFrameBase.ReadState.SUSPENDING_WAIT, WsFrameBase.ReadState.SUSPENDED)) {
                        break;
                    } else {
                        return AbstractEndpoint.Handler.SocketState.SUSPENDED;
                    }
                default:
                    throw new IllegalStateException(sm.getString("wsFrameServer.illegalReadState", getReadState()));
            }
        }
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }

    private AbstractEndpoint.Handler.SocketState doOnDataAvailable() throws IOException {
        onDataAvailable();
        while (isOpen()) {
            switch (getReadState()) {
                case PROCESSING:
                    if (!changeReadState(WsFrameBase.ReadState.PROCESSING, WsFrameBase.ReadState.WAITING)) {
                        break;
                    } else {
                        return AbstractEndpoint.Handler.SocketState.UPGRADED;
                    }
                case SUSPENDING_PROCESS:
                    if (!changeReadState(WsFrameBase.ReadState.SUSPENDING_PROCESS, WsFrameBase.ReadState.SUSPENDED)) {
                        break;
                    } else {
                        return AbstractEndpoint.Handler.SocketState.SUSPENDED;
                    }
                default:
                    throw new IllegalStateException(sm.getString("wsFrameServer.illegalReadState", getReadState()));
            }
        }
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }
}