package org.apache.tomcat.websocket.server;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Transformation;
import org.apache.tomcat.websocket.WsRemoteEndpointImplBase;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/server/WsRemoteEndpointImplServer.class */
public class WsRemoteEndpointImplServer extends WsRemoteEndpointImplBase {
    private static final StringManager sm = StringManager.getManager(WsRemoteEndpointImplServer.class);
    private final SocketWrapperBase<?> socketWrapper;
    private final WsWriteTimeout wsWriteTimeout;
    private volatile boolean close;
    private final Log log = LogFactory.getLog(WsRemoteEndpointImplServer.class);
    private volatile SendHandler handler = null;
    private volatile ByteBuffer[] buffers = null;
    private volatile long timeoutExpiry = -1;

    public WsRemoteEndpointImplServer(SocketWrapperBase<?> socketWrapper, WsServerContainer serverContainer) {
        this.socketWrapper = socketWrapper;
        this.wsWriteTimeout = serverContainer.getTimeout();
    }

    @Override // org.apache.tomcat.websocket.WsRemoteEndpointImplBase
    protected final boolean isMasked() {
        return false;
    }

    @Override // org.apache.tomcat.websocket.WsRemoteEndpointImplBase
    protected void doWrite(final SendHandler handler, final long blockingWriteTimeoutExpiry, ByteBuffer... buffers) {
        long timeout;
        if (this.socketWrapper.hasAsyncIO()) {
            final boolean block = blockingWriteTimeoutExpiry != -1;
            if (block) {
                timeout = blockingWriteTimeoutExpiry - System.currentTimeMillis();
                if (timeout <= 0) {
                    SendResult sr = new SendResult(new SocketTimeoutException());
                    handler.onResult(sr);
                    return;
                }
            } else {
                this.handler = handler;
                if (-1 > 0) {
                    this.timeoutExpiry = (-1) + System.currentTimeMillis();
                    this.wsWriteTimeout.register(this);
                }
                timeout = getSendTimeout();
            }
            this.socketWrapper.write(block ? SocketWrapperBase.BlockingMode.BLOCK : SocketWrapperBase.BlockingMode.SEMI_BLOCK, timeout, TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE_WITH_COMPLETION, new CompletionHandler<Long, Void>() { // from class: org.apache.tomcat.websocket.server.WsRemoteEndpointImplServer.1
                @Override // java.nio.channels.CompletionHandler
                public void completed(Long result, Void attachment) {
                    if (!block) {
                        WsRemoteEndpointImplServer.this.wsWriteTimeout.unregister(WsRemoteEndpointImplServer.this);
                        WsRemoteEndpointImplServer.this.clearHandler(null, true);
                        if (WsRemoteEndpointImplServer.this.close) {
                            WsRemoteEndpointImplServer.this.close();
                            return;
                        }
                        return;
                    }
                    long timeout2 = blockingWriteTimeoutExpiry - System.currentTimeMillis();
                    if (timeout2 > 0) {
                        handler.onResult(WsRemoteEndpointImplServer.SENDRESULT_OK);
                    } else {
                        failed((Throwable) new SocketTimeoutException(), (Void) null);
                    }
                }

                @Override // java.nio.channels.CompletionHandler
                public void failed(Throwable exc, Void attachment) {
                    if (exc instanceof InterruptedByTimeoutException) {
                        exc = new SocketTimeoutException();
                    }
                    if (!block) {
                        WsRemoteEndpointImplServer.this.wsWriteTimeout.unregister(WsRemoteEndpointImplServer.this);
                        WsRemoteEndpointImplServer.this.clearHandler(exc, true);
                        WsRemoteEndpointImplServer.this.close();
                        return;
                    }
                    SendResult sr2 = new SendResult(exc);
                    handler.onResult(sr2);
                }
            }, buffers);
        } else if (blockingWriteTimeoutExpiry == -1) {
            this.handler = handler;
            this.buffers = buffers;
            onWritePossible(true);
        } else {
            try {
                for (ByteBuffer buffer : buffers) {
                    long timeout2 = blockingWriteTimeoutExpiry - System.currentTimeMillis();
                    if (timeout2 <= 0) {
                        SendResult sr2 = new SendResult(new SocketTimeoutException());
                        handler.onResult(sr2);
                        return;
                    }
                    this.socketWrapper.setWriteTimeout(timeout2);
                    this.socketWrapper.write(true, buffer);
                }
                long timeout3 = blockingWriteTimeoutExpiry - System.currentTimeMillis();
                if (timeout3 <= 0) {
                    SendResult sr3 = new SendResult(new SocketTimeoutException());
                    handler.onResult(sr3);
                    return;
                }
                this.socketWrapper.setWriteTimeout(timeout3);
                this.socketWrapper.flush(true);
                handler.onResult(SENDRESULT_OK);
            } catch (IOException e) {
                SendResult sr4 = new SendResult(e);
                handler.onResult(sr4);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x005b, code lost:
        r6.socketWrapper.flush(false);
        r9 = r6.socketWrapper.isReadyForWrite();
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x006d, code lost:
        if (r9 == false) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0070, code lost:
        r6.wsWriteTimeout.unregister(r6);
        clearHandler(null, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0082, code lost:
        if (r6.close == false) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0085, code lost:
        close();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onWritePossible(boolean r7) {
        /*
            Method dump skipped, instructions count: 200
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.websocket.server.WsRemoteEndpointImplServer.onWritePossible(boolean):void");
    }

    @Override // org.apache.tomcat.websocket.WsRemoteEndpointImplBase
    protected void doClose() {
        if (this.handler != null) {
            clearHandler(new EOFException(), true);
        }
        try {
            this.socketWrapper.close();
        } catch (IOException e) {
            if (this.log.isInfoEnabled()) {
                this.log.info(sm.getString("wsRemoteEndpointServer.closeFailed"), e);
            }
        }
        this.wsWriteTimeout.unregister(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public long getTimeoutExpiry() {
        return this.timeoutExpiry;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onTimeout(boolean useDispatch) {
        if (this.handler != null) {
            clearHandler(new SocketTimeoutException(), useDispatch);
        }
        close();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.WsRemoteEndpointImplBase
    public void setTransformation(Transformation transformation) {
        super.setTransformation(transformation);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearHandler(Throwable t, boolean useDispatch) {
        SendHandler sh = this.handler;
        this.handler = null;
        this.buffers = null;
        if (sh != null) {
            if (useDispatch) {
                OnResultRunnable r = new OnResultRunnable(sh, t);
                try {
                    this.socketWrapper.execute(r);
                } catch (RejectedExecutionException e) {
                    r.run();
                }
            } else if (t == null) {
                sh.onResult(new SendResult());
            } else {
                sh.onResult(new SendResult(t));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/server/WsRemoteEndpointImplServer$OnResultRunnable.class */
    public static class OnResultRunnable implements Runnable {
        private final SendHandler sh;
        private final Throwable t;

        private OnResultRunnable(SendHandler sh, Throwable t) {
            this.sh = sh;
            this.t = t;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.t == null) {
                this.sh.onResult(new SendResult());
            } else {
                this.sh.onResult(new SendResult(this.t));
            }
        }
    }
}