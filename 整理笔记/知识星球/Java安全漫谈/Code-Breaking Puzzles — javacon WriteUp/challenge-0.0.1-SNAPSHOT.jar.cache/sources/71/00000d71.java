package org.apache.tomcat.util.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/SocketWrapperBase.class */
public abstract class SocketWrapperBase<E> {
    private final E socket;
    private final AbstractEndpoint<E, ?> endpoint;
    private final Lock blockingStatusReadLock;
    private final ReentrantReadWriteLock.WriteLock blockingStatusWriteLock;
    private static final Log log = LogFactory.getLog(SocketWrapperBase.class);
    protected static final StringManager sm = StringManager.getManager(SocketWrapperBase.class);
    public static final CompletionCheck COMPLETE_WRITE = new CompletionCheck() { // from class: org.apache.tomcat.util.net.SocketWrapperBase.1
        @Override // org.apache.tomcat.util.net.SocketWrapperBase.CompletionCheck
        public CompletionHandlerCall callHandler(CompletionState state, ByteBuffer[] buffers, int offset, int length) {
            for (int i = 0; i < length; i++) {
                if (buffers[offset + i].remaining() > 0) {
                    return CompletionHandlerCall.CONTINUE;
                }
            }
            return state == CompletionState.DONE ? CompletionHandlerCall.DONE : CompletionHandlerCall.NONE;
        }
    };
    public static final CompletionCheck COMPLETE_WRITE_WITH_COMPLETION = new CompletionCheck() { // from class: org.apache.tomcat.util.net.SocketWrapperBase.2
        @Override // org.apache.tomcat.util.net.SocketWrapperBase.CompletionCheck
        public CompletionHandlerCall callHandler(CompletionState state, ByteBuffer[] buffers, int offset, int length) {
            for (int i = 0; i < length; i++) {
                if (buffers[offset + i].remaining() > 0) {
                    return CompletionHandlerCall.CONTINUE;
                }
            }
            return CompletionHandlerCall.DONE;
        }
    };
    public static final CompletionCheck READ_DATA = new CompletionCheck() { // from class: org.apache.tomcat.util.net.SocketWrapperBase.3
        @Override // org.apache.tomcat.util.net.SocketWrapperBase.CompletionCheck
        public CompletionHandlerCall callHandler(CompletionState state, ByteBuffer[] buffers, int offset, int length) {
            return state == CompletionState.DONE ? CompletionHandlerCall.DONE : CompletionHandlerCall.NONE;
        }
    };
    private volatile long readTimeout = -1;
    private volatile long writeTimeout = -1;
    private volatile int keepAliveLeft = 100;
    private volatile boolean upgraded = false;
    private boolean secure = false;
    private String negotiatedProtocol = null;
    protected String localAddr = null;
    protected String localName = null;
    protected int localPort = -1;
    protected String remoteAddr = null;
    protected String remoteHost = null;
    protected int remotePort = -1;
    private volatile boolean blockingStatus = true;
    private volatile IOException error = null;
    protected volatile SocketBufferHandler socketBufferHandler = null;
    protected int bufferedWriteSize = 65536;
    protected final WriteBuffer nonBlockingWriteBuffer = new WriteBuffer(this.bufferedWriteSize);

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/SocketWrapperBase$BlockingMode.class */
    public enum BlockingMode {
        NON_BLOCK,
        SEMI_BLOCK,
        BLOCK
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/SocketWrapperBase$CompletionCheck.class */
    public interface CompletionCheck {
        CompletionHandlerCall callHandler(CompletionState completionState, ByteBuffer[] byteBufferArr, int i, int i2);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/SocketWrapperBase$CompletionHandlerCall.class */
    public enum CompletionHandlerCall {
        CONTINUE,
        NONE,
        DONE
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/SocketWrapperBase$CompletionState.class */
    public enum CompletionState {
        PENDING,
        NOT_DONE,
        INLINE,
        ERROR,
        DONE
    }

    protected abstract void populateRemoteHost();

    protected abstract void populateRemoteAddr();

    protected abstract void populateRemotePort();

    protected abstract void populateLocalName();

    protected abstract void populateLocalAddr();

    protected abstract void populateLocalPort();

    public abstract int read(boolean z, byte[] bArr, int i, int i2) throws IOException;

    public abstract int read(boolean z, ByteBuffer byteBuffer) throws IOException;

    public abstract boolean isReadyForRead() throws IOException;

    public abstract void setAppReadBufHandler(ApplicationBufferHandler applicationBufferHandler);

    public abstract void close() throws IOException;

    public abstract boolean isClosed();

    protected abstract void doWrite(boolean z, ByteBuffer byteBuffer) throws IOException;

    public abstract void registerReadInterest();

    public abstract void registerWriteInterest();

    public abstract SendfileDataBase createSendfileData(String str, long j, long j2);

    public abstract SendfileState processSendfile(SendfileDataBase sendfileDataBase);

    public abstract void doClientAuth(SSLSupport sSLSupport) throws IOException;

    public abstract SSLSupport getSslSupport(String str);

    public SocketWrapperBase(E socket, AbstractEndpoint<E, ?> endpoint) {
        this.socket = socket;
        this.endpoint = endpoint;
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.blockingStatusReadLock = lock.readLock();
        this.blockingStatusWriteLock = lock.writeLock();
    }

    public E getSocket() {
        return this.socket;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractEndpoint<E, ?> getEndpoint() {
        return this.endpoint;
    }

    public void execute(Runnable runnable) {
        Executor executor = this.endpoint.getExecutor();
        if (!this.endpoint.isRunning() || executor == null) {
            throw new RejectedExecutionException();
        }
        executor.execute(runnable);
    }

    public IOException getError() {
        return this.error;
    }

    public void setError(IOException error) {
        if (this.error != null) {
            return;
        }
        this.error = error;
    }

    public void checkError() throws IOException {
        if (this.error != null) {
            throw this.error;
        }
    }

    public boolean isUpgraded() {
        return this.upgraded;
    }

    public void setUpgraded(boolean upgraded) {
        this.upgraded = upgraded;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public String getNegotiatedProtocol() {
        return this.negotiatedProtocol;
    }

    public void setNegotiatedProtocol(String negotiatedProtocol) {
        this.negotiatedProtocol = negotiatedProtocol;
    }

    public void setReadTimeout(long readTimeout) {
        if (readTimeout > 0) {
            this.readTimeout = readTimeout;
        } else {
            this.readTimeout = -1L;
        }
    }

    public long getReadTimeout() {
        return this.readTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        if (writeTimeout > 0) {
            this.writeTimeout = writeTimeout;
        } else {
            this.writeTimeout = -1L;
        }
    }

    public long getWriteTimeout() {
        return this.writeTimeout;
    }

    public void setKeepAliveLeft(int keepAliveLeft) {
        this.keepAliveLeft = keepAliveLeft;
    }

    public int decrementKeepAlive() {
        int i = this.keepAliveLeft - 1;
        this.keepAliveLeft = i;
        return i;
    }

    public String getRemoteHost() {
        if (this.remoteHost == null) {
            populateRemoteHost();
        }
        return this.remoteHost;
    }

    public String getRemoteAddr() {
        if (this.remoteAddr == null) {
            populateRemoteAddr();
        }
        return this.remoteAddr;
    }

    public int getRemotePort() {
        if (this.remotePort == -1) {
            populateRemotePort();
        }
        return this.remotePort;
    }

    public String getLocalName() {
        if (this.localName == null) {
            populateLocalName();
        }
        return this.localName;
    }

    public String getLocalAddr() {
        if (this.localAddr == null) {
            populateLocalAddr();
        }
        return this.localAddr;
    }

    public int getLocalPort() {
        if (this.localPort == -1) {
            populateLocalPort();
        }
        return this.localPort;
    }

    public boolean getBlockingStatus() {
        return this.blockingStatus;
    }

    public void setBlockingStatus(boolean blockingStatus) {
        this.blockingStatus = blockingStatus;
    }

    public Lock getBlockingStatusReadLock() {
        return this.blockingStatusReadLock;
    }

    public ReentrantReadWriteLock.WriteLock getBlockingStatusWriteLock() {
        return this.blockingStatusWriteLock;
    }

    public SocketBufferHandler getSocketBufferHandler() {
        return this.socketBufferHandler;
    }

    public boolean hasDataToWrite() {
        return (this.socketBufferHandler.isWriteBufferEmpty() && this.nonBlockingWriteBuffer.isEmpty()) ? false : true;
    }

    public boolean isReadyForWrite() {
        boolean result = canWrite();
        if (!result) {
            registerWriteInterest();
        }
        return result;
    }

    public boolean canWrite() {
        if (this.socketBufferHandler == null) {
            throw new IllegalStateException(sm.getString("socket.closed"));
        }
        return this.socketBufferHandler.isWriteBufferWritable() && this.nonBlockingWriteBuffer.isEmpty();
    }

    public String toString() {
        return super.toString() + ":" + String.valueOf(this.socket);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int populateReadBuffer(byte[] b, int off, int len) {
        this.socketBufferHandler.configureReadBufferForRead();
        ByteBuffer readBuffer = this.socketBufferHandler.getReadBuffer();
        int remaining = readBuffer.remaining();
        if (remaining > 0) {
            remaining = Math.min(remaining, len);
            readBuffer.get(b, off, remaining);
            if (log.isDebugEnabled()) {
                log.debug("Socket: [" + this + "], Read from buffer: [" + remaining + "]");
            }
        }
        return remaining;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int populateReadBuffer(ByteBuffer to) {
        this.socketBufferHandler.configureReadBufferForRead();
        int nRead = transfer(this.socketBufferHandler.getReadBuffer(), to);
        if (log.isDebugEnabled()) {
            log.debug("Socket: [" + this + "], Read from buffer: [" + nRead + "]");
        }
        return nRead;
    }

    public void unRead(ByteBuffer returnedInput) {
        if (returnedInput != null) {
            this.socketBufferHandler.configureReadBufferForWrite();
            this.socketBufferHandler.getReadBuffer().put(returnedInput);
        }
    }

    public final void write(boolean block, byte[] buf, int off, int len) throws IOException {
        if (len == 0 || buf == null) {
            return;
        }
        if (block) {
            writeBlocking(buf, off, len);
        } else {
            writeNonBlocking(buf, off, len);
        }
    }

    public final void write(boolean block, ByteBuffer from) throws IOException {
        if (from == null || from.remaining() == 0) {
            return;
        }
        if (block) {
            writeBlocking(from);
        } else {
            writeNonBlocking(from);
        }
    }

    protected void writeBlocking(byte[] buf, int off, int len) throws IOException {
        this.socketBufferHandler.configureWriteBufferForWrite();
        int transfer = transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
        while (true) {
            int thisTime = transfer;
            if (this.socketBufferHandler.getWriteBuffer().remaining() == 0) {
                len -= thisTime;
                off += thisTime;
                doWrite(true);
                this.socketBufferHandler.configureWriteBufferForWrite();
                transfer = transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeBlocking(ByteBuffer from) throws IOException {
        if (this.socketBufferHandler.isWriteBufferEmpty()) {
            writeBlockingDirect(from);
            return;
        }
        this.socketBufferHandler.configureWriteBufferForWrite();
        transfer(from, this.socketBufferHandler.getWriteBuffer());
        if (!this.socketBufferHandler.isWriteBufferWritable()) {
            doWrite(true);
            writeBlockingDirect(from);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeBlockingDirect(ByteBuffer from) throws IOException {
        int limit = this.socketBufferHandler.getWriteBuffer().capacity();
        int fromLimit = from.limit();
        while (from.remaining() >= limit) {
            from.limit(from.position() + limit);
            doWrite(true, from);
            from.limit(fromLimit);
        }
        if (from.remaining() > 0) {
            this.socketBufferHandler.configureWriteBufferForWrite();
            transfer(from, this.socketBufferHandler.getWriteBuffer());
        }
    }

    protected void writeNonBlocking(byte[] buf, int off, int len) throws IOException {
        if (this.nonBlockingWriteBuffer.isEmpty() && this.socketBufferHandler.isWriteBufferWritable()) {
            this.socketBufferHandler.configureWriteBufferForWrite();
            int transfer = transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
            while (true) {
                int thisTime = transfer;
                len -= thisTime;
                if (!this.socketBufferHandler.isWriteBufferWritable()) {
                    off += thisTime;
                    doWrite(false);
                    if (len <= 0 || !this.socketBufferHandler.isWriteBufferWritable()) {
                        break;
                    }
                    this.socketBufferHandler.configureWriteBufferForWrite();
                    transfer = transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
                } else {
                    break;
                }
            }
        }
        if (len > 0) {
            this.nonBlockingWriteBuffer.add(buf, off, len);
        }
    }

    protected void writeNonBlocking(ByteBuffer from) throws IOException {
        if (this.nonBlockingWriteBuffer.isEmpty() && this.socketBufferHandler.isWriteBufferWritable()) {
            writeNonBlockingInternal(from);
        }
        if (from.remaining() > 0) {
            this.nonBlockingWriteBuffer.add(from);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeNonBlockingInternal(ByteBuffer from) throws IOException {
        if (this.socketBufferHandler.isWriteBufferEmpty()) {
            writeNonBlockingDirect(from);
            return;
        }
        this.socketBufferHandler.configureWriteBufferForWrite();
        transfer(from, this.socketBufferHandler.getWriteBuffer());
        if (!this.socketBufferHandler.isWriteBufferWritable()) {
            doWrite(false);
            if (this.socketBufferHandler.isWriteBufferWritable()) {
                writeNonBlockingDirect(from);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeNonBlockingDirect(ByteBuffer from) throws IOException {
        int limit = this.socketBufferHandler.getWriteBuffer().capacity();
        int fromLimit = from.limit();
        while (from.remaining() >= limit) {
            int newLimit = from.position() + limit;
            from.limit(newLimit);
            doWrite(false, from);
            from.limit(fromLimit);
            if (from.position() != newLimit) {
                return;
            }
        }
        if (from.remaining() > 0) {
            this.socketBufferHandler.configureWriteBufferForWrite();
            transfer(from, this.socketBufferHandler.getWriteBuffer());
        }
    }

    public boolean flush(boolean block) throws IOException {
        boolean result = false;
        if (block) {
            flushBlocking();
        } else {
            result = flushNonBlocking();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void flushBlocking() throws IOException {
        doWrite(true);
        if (!this.nonBlockingWriteBuffer.isEmpty()) {
            this.nonBlockingWriteBuffer.write((SocketWrapperBase<?>) this, true);
            if (!this.socketBufferHandler.isWriteBufferEmpty()) {
                doWrite(true);
            }
        }
    }

    protected boolean flushNonBlocking() throws IOException {
        boolean dataLeft = !this.socketBufferHandler.isWriteBufferEmpty();
        if (dataLeft) {
            doWrite(false);
            dataLeft = !this.socketBufferHandler.isWriteBufferEmpty();
        }
        if (!dataLeft && !this.nonBlockingWriteBuffer.isEmpty()) {
            dataLeft = this.nonBlockingWriteBuffer.write((SocketWrapperBase<?>) this, false);
            if (!dataLeft && !this.socketBufferHandler.isWriteBufferEmpty()) {
                doWrite(false);
                dataLeft = !this.socketBufferHandler.isWriteBufferEmpty();
            }
        }
        return dataLeft;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doWrite(boolean block) throws IOException {
        this.socketBufferHandler.configureWriteBufferForRead();
        doWrite(block, this.socketBufferHandler.getWriteBuffer());
    }

    public void processSocket(SocketEvent socketStatus, boolean dispatch) {
        this.endpoint.processSocket(this, socketStatus, dispatch);
    }

    public boolean hasAsyncIO() {
        return false;
    }

    public boolean isReadPending() {
        return false;
    }

    public boolean isWritePending() {
        return false;
    }

    public boolean awaitReadComplete(long timeout, TimeUnit unit) {
        return true;
    }

    public boolean awaitWriteComplete(long timeout, TimeUnit unit) {
        return true;
    }

    public final <A> CompletionState read(BlockingMode block, long timeout, TimeUnit unit, A attachment, CompletionCheck check, CompletionHandler<Long, ? super A> handler, ByteBuffer... dsts) {
        if (dsts == null) {
            throw new IllegalArgumentException();
        }
        return read(dsts, 0, dsts.length, block, timeout, unit, attachment, check, handler);
    }

    public <A> CompletionState read(ByteBuffer[] dsts, int offset, int length, BlockingMode block, long timeout, TimeUnit unit, A attachment, CompletionCheck check, CompletionHandler<Long, ? super A> handler) {
        throw new UnsupportedOperationException();
    }

    public final <A> CompletionState write(BlockingMode block, long timeout, TimeUnit unit, A attachment, CompletionCheck check, CompletionHandler<Long, ? super A> handler, ByteBuffer... srcs) {
        if (srcs == null) {
            throw new IllegalArgumentException();
        }
        return write(srcs, 0, srcs.length, block, timeout, unit, attachment, check, handler);
    }

    public <A> CompletionState write(ByteBuffer[] srcs, int offset, int length, BlockingMode block, long timeout, TimeUnit unit, A attachment, CompletionCheck check, CompletionHandler<Long, ? super A> handler) {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int transfer(byte[] from, int offset, int length, ByteBuffer to) {
        int max = Math.min(length, to.remaining());
        if (max > 0) {
            to.put(from, offset, max);
        }
        return max;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int transfer(ByteBuffer from, ByteBuffer to) {
        int max = Math.min(from.remaining(), to.remaining());
        if (max > 0) {
            int fromLimit = from.limit();
            from.limit(from.position() + max);
            to.put(from);
            from.limit(fromLimit);
        }
        return max;
    }
}