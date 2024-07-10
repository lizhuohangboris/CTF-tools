package org.apache.tomcat.util.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import org.apache.tomcat.util.net.NioEndpoint;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/NioChannel.class */
public class NioChannel implements ByteChannel {
    protected static final StringManager sm = StringManager.getManager(NioChannel.class);
    protected static final ByteBuffer emptyBuf = ByteBuffer.allocate(0);
    protected SocketChannel sc;
    protected SocketWrapperBase<NioChannel> socketWrapper = null;
    protected final SocketBufferHandler bufHandler;
    protected NioEndpoint.Poller poller;
    private ApplicationBufferHandler appReadBufHandler;

    public NioChannel(SocketChannel channel, SocketBufferHandler bufHandler) {
        this.sc = null;
        this.sc = channel;
        this.bufHandler = bufHandler;
    }

    public void reset() throws IOException {
        this.bufHandler.reset();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSocketWrapper(SocketWrapperBase<NioChannel> socketWrapper) {
        this.socketWrapper = socketWrapper;
    }

    public void free() {
        this.bufHandler.free();
    }

    public boolean flush(boolean block, Selector s, long timeout) throws IOException {
        return true;
    }

    @Override // java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        getIOChannel().socket().close();
        getIOChannel().close();
    }

    public void close(boolean force) throws IOException {
        if (isOpen() || force) {
            close();
        }
    }

    @Override // java.nio.channels.Channel
    public boolean isOpen() {
        return this.sc.isOpen();
    }

    @Override // java.nio.channels.WritableByteChannel
    public int write(ByteBuffer src) throws IOException {
        checkInterruptStatus();
        return this.sc.write(src);
    }

    @Override // java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer dst) throws IOException {
        return this.sc.read(dst);
    }

    public Object getAttachment() {
        NioEndpoint.Poller pol = getPoller();
        Selector sel = pol != null ? pol.getSelector() : null;
        SelectionKey key = sel != null ? getIOChannel().keyFor(sel) : null;
        Object att = key != null ? key.attachment() : null;
        return att;
    }

    public SocketBufferHandler getBufHandler() {
        return this.bufHandler;
    }

    public NioEndpoint.Poller getPoller() {
        return this.poller;
    }

    public SocketChannel getIOChannel() {
        return this.sc;
    }

    public boolean isClosing() {
        return false;
    }

    public boolean isHandshakeComplete() {
        return true;
    }

    public int handshake(boolean read, boolean write) throws IOException {
        return 0;
    }

    public void setPoller(NioEndpoint.Poller poller) {
        this.poller = poller;
    }

    public void setIOChannel(SocketChannel IOChannel) {
        this.sc = IOChannel;
    }

    public String toString() {
        return super.toString() + ":" + this.sc.toString();
    }

    public int getOutboundRemaining() {
        return 0;
    }

    public boolean flushOutbound() throws IOException {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkInterruptStatus() throws IOException {
        if (Thread.interrupted()) {
            throw new IOException(sm.getString("channel.nio.interrupted"));
        }
    }

    public void setAppReadBufHandler(ApplicationBufferHandler handler) {
        this.appReadBufHandler = handler;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ApplicationBufferHandler getAppReadBufHandler() {
        return this.appReadBufHandler;
    }
}