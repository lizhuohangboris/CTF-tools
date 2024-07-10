package org.apache.tomcat.util.net;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.channels.NetworkChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.net.jsse.JSSESupport;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/Nio2Endpoint.class */
public class Nio2Endpoint extends AbstractJsseEndpoint<Nio2Channel, AsynchronousSocketChannel> {
    private volatile AsynchronousServerSocketChannel serverSock = null;
    private AsynchronousChannelGroup threadGroup = null;
    private volatile boolean allClosed;
    private SynchronizedStack<Nio2Channel> nioChannels;
    private static final Log log = LogFactory.getLog(Nio2Endpoint.class);
    private static ThreadLocal<Boolean> inlineCompletion = new ThreadLocal<>();

    public Nio2Endpoint() {
        setMaxConnections(-1);
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public boolean getDeferAccept() {
        return false;
    }

    public int getKeepAliveCount() {
        return -1;
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void bind() throws Exception {
        if (getExecutor() == null) {
            createExecutor();
        }
        if (getExecutor() instanceof ExecutorService) {
            this.threadGroup = AsynchronousChannelGroup.withThreadPool((ExecutorService) getExecutor());
        }
        if (!this.internalExecutor) {
            log.warn(sm.getString("endpoint.nio2.exclusiveExecutor"));
        }
        this.serverSock = AsynchronousServerSocketChannel.open(this.threadGroup);
        this.socketProperties.setProperties(this.serverSock);
        InetSocketAddress addr = getAddress() != null ? new InetSocketAddress(getAddress(), getPort()) : new InetSocketAddress(getPort());
        this.serverSock.bind(addr, getAcceptCount());
        if (this.acceptorThreadCount != 1) {
            this.acceptorThreadCount = 1;
        }
        initialiseSsl();
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void startInternal() throws Exception {
        if (!this.running) {
            this.allClosed = false;
            this.running = true;
            this.paused = false;
            this.processorCache = new SynchronizedStack<>(128, this.socketProperties.getProcessorCache());
            this.nioChannels = new SynchronizedStack<>(128, this.socketProperties.getBufferPool());
            if (getExecutor() == null) {
                createExecutor();
            }
            initializeConnectionLatch();
            startAcceptorThreads();
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void stopInternal() {
        if (!this.paused) {
            pause();
        }
        if (this.running) {
            this.running = false;
            getExecutor().execute(new Runnable() { // from class: org.apache.tomcat.util.net.Nio2Endpoint.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        for (Nio2Channel channel : Nio2Endpoint.this.getHandler().getOpenSockets()) {
                            Nio2Endpoint.this.closeSocket(channel.getSocket());
                        }
                    } catch (Throwable t) {
                        try {
                            ExceptionUtils.handleThrowable(t);
                        } finally {
                            Nio2Endpoint.this.allClosed = true;
                        }
                    }
                }
            });
            this.nioChannels.clear();
            this.processorCache.clear();
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractJsseEndpoint, org.apache.tomcat.util.net.AbstractEndpoint
    public void unbind() throws Exception {
        if (this.running) {
            stop();
        }
        doCloseServerSocket();
        destroySsl();
        super.unbind();
        shutdownExecutor();
        if (getHandler() != null) {
            getHandler().recycle();
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected void doCloseServerSocket() throws IOException {
        if (this.serverSock != null) {
            this.serverSock.close();
            this.serverSock = null;
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void shutdownExecutor() {
        if (this.threadGroup != null && this.internalExecutor) {
            try {
                long timeout = getExecutorTerminationTimeoutMillis();
                while (timeout > 0 && !this.allClosed) {
                    timeout -= 100;
                    Thread.sleep(100L);
                }
                this.threadGroup.shutdownNow();
                if (timeout > 0) {
                    this.threadGroup.awaitTermination(timeout, TimeUnit.MILLISECONDS);
                }
            } catch (IOException e) {
                getLog().warn(sm.getString("endpoint.warn.executorShutdown", getName()), e);
            } catch (InterruptedException e2) {
            }
            if (!this.threadGroup.isTerminated()) {
                getLog().warn(sm.getString("endpoint.warn.executorShutdown", getName()));
            }
            this.threadGroup = null;
        }
        super.shutdownExecutor();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public boolean setSocketOptions(AsynchronousSocketChannel socket) {
        try {
            this.socketProperties.setProperties(socket);
            Nio2Channel channel = this.nioChannels.pop();
            if (channel == null) {
                SocketBufferHandler bufhandler = new SocketBufferHandler(this.socketProperties.getAppReadBufSize(), this.socketProperties.getAppWriteBufSize(), this.socketProperties.getDirectBuffer());
                if (isSSLEnabled()) {
                    channel = new SecureNio2Channel(bufhandler, this);
                } else {
                    channel = new Nio2Channel(bufhandler);
                }
            }
            Nio2SocketWrapper socketWrapper = new Nio2SocketWrapper(channel, this);
            channel.reset(socket, socketWrapper);
            socketWrapper.setReadTimeout(getConnectionTimeout());
            socketWrapper.setWriteTimeout(getConnectionTimeout());
            socketWrapper.setKeepAliveLeft(getMaxKeepAliveRequests());
            socketWrapper.setSecure(isSSLEnabled());
            return processSocket(socketWrapper, SocketEvent.OPEN_READ, true);
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log.error("", t);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void closeSocket(AsynchronousSocketChannel socket) {
        countDownConnection();
        try {
            socket.close();
        } catch (IOException ioe) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("endpoint.err.close"), ioe);
            }
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractJsseEndpoint
    protected NetworkChannel getServerSocket() {
        return this.serverSock;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public AsynchronousSocketChannel serverSocketAccept() throws Exception {
        return this.serverSock.accept().get();
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected Log getLog() {
        return log;
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected SocketProcessorBase<Nio2Channel> createSocketProcessor(SocketWrapperBase<Nio2Channel> socketWrapper, SocketEvent event) {
        return new SocketProcessor(socketWrapper, event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeSocket(SocketWrapperBase<Nio2Channel> socket) {
        if (log.isDebugEnabled()) {
            log.debug("Calling [" + this + "].closeSocket([" + socket + "],[" + socket.getSocket() + "])", new Exception());
        }
        if (socket == null) {
            return;
        }
        try {
            getHandler().release(socket);
        } catch (Throwable e) {
            ExceptionUtils.handleThrowable(e);
            if (log.isDebugEnabled()) {
                log.error("", e);
            }
        }
        Nio2SocketWrapper nio2Socket = (Nio2SocketWrapper) socket;
        try {
            synchronized (socket.getSocket()) {
                if (!nio2Socket.closed) {
                    nio2Socket.closed = true;
                    countDownConnection();
                }
                if (socket.getSocket().isOpen()) {
                    socket.getSocket().close(true);
                }
            }
        } catch (Throwable e2) {
            ExceptionUtils.handleThrowable(e2);
            if (log.isDebugEnabled()) {
                log.error("", e2);
            }
        }
        try {
            if (nio2Socket.getSendfileData() != null && nio2Socket.getSendfileData().fchannel != null && nio2Socket.getSendfileData().fchannel.isOpen()) {
                nio2Socket.getSendfileData().fchannel.close();
            }
        } catch (Throwable e3) {
            ExceptionUtils.handleThrowable(e3);
            if (log.isDebugEnabled()) {
                log.error("", e3);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/Nio2Endpoint$Nio2SocketWrapper.class */
    public static class Nio2SocketWrapper extends SocketWrapperBase<Nio2Channel> {
        private static final ThreadLocal<AtomicInteger> nestedWriteCompletionCount = new ThreadLocal<AtomicInteger>() { // from class: org.apache.tomcat.util.net.Nio2Endpoint.Nio2SocketWrapper.1
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.lang.ThreadLocal
            public AtomicInteger initialValue() {
                return new AtomicInteger(0);
            }
        };
        private SendfileData sendfileData;
        private final CompletionHandler<Integer, SocketWrapperBase<Nio2Channel>> readCompletionHandler;
        private final Semaphore readPending;
        private boolean readInterest;
        private final CompletionHandler<Integer, ByteBuffer> writeCompletionHandler;
        private final CompletionHandler<Long, ByteBuffer[]> gatheringWriteCompletionHandler;
        private final Semaphore writePending;
        private boolean writeInterest;
        private boolean writeNotify;
        private volatile boolean closed;
        private CompletionHandler<Integer, SocketWrapperBase<Nio2Channel>> awaitBytesHandler;
        private CompletionHandler<Integer, SendfileData> sendfileHandler;

        public Nio2SocketWrapper(Nio2Channel channel, final Nio2Endpoint endpoint) {
            super(channel, endpoint);
            this.sendfileData = null;
            this.readPending = new Semaphore(1);
            this.readInterest = false;
            this.writePending = new Semaphore(1);
            this.writeInterest = false;
            this.writeNotify = false;
            this.closed = false;
            this.awaitBytesHandler = new CompletionHandler<Integer, SocketWrapperBase<Nio2Channel>>() { // from class: org.apache.tomcat.util.net.Nio2Endpoint.Nio2SocketWrapper.2
                @Override // java.nio.channels.CompletionHandler
                public void completed(Integer nBytes, SocketWrapperBase<Nio2Channel> attachment) {
                    if (nBytes.intValue() < 0) {
                        failed((Throwable) new ClosedChannelException(), attachment);
                    } else {
                        Nio2SocketWrapper.this.getEndpoint().processSocket(attachment, SocketEvent.OPEN_READ, Nio2Endpoint.isInline());
                    }
                }

                @Override // java.nio.channels.CompletionHandler
                public void failed(Throwable exc, SocketWrapperBase<Nio2Channel> attachment) {
                    Nio2SocketWrapper.this.getEndpoint().processSocket(attachment, SocketEvent.DISCONNECT, true);
                }
            };
            this.sendfileHandler = new CompletionHandler<Integer, SendfileData>() { // from class: org.apache.tomcat.util.net.Nio2Endpoint.Nio2SocketWrapper.3
                @Override // java.nio.channels.CompletionHandler
                public void completed(Integer nWrite, SendfileData attachment) {
                    if (nWrite.intValue() < 0) {
                        failed((Throwable) new EOFException(), attachment);
                        return;
                    }
                    attachment.pos += nWrite.intValue();
                    ByteBuffer buffer = Nio2SocketWrapper.this.getSocket().getBufHandler().getWriteBuffer();
                    if (!buffer.hasRemaining()) {
                        if (attachment.length <= 0) {
                            Nio2SocketWrapper.this.setSendfileData(null);
                            try {
                                attachment.fchannel.close();
                            } catch (IOException e) {
                            }
                            if (!Nio2Endpoint.isInline()) {
                                switch (attachment.keepAliveState) {
                                    case NONE:
                                        Nio2SocketWrapper.this.getEndpoint().processSocket(Nio2SocketWrapper.this, SocketEvent.DISCONNECT, false);
                                        return;
                                    case PIPELINED:
                                        Nio2SocketWrapper.this.getEndpoint().processSocket(Nio2SocketWrapper.this, SocketEvent.OPEN_READ, true);
                                        return;
                                    case OPEN:
                                        Nio2SocketWrapper.this.awaitBytes();
                                        return;
                                    default:
                                        return;
                                }
                            }
                            attachment.doneInline = true;
                            return;
                        }
                        Nio2SocketWrapper.this.getSocket().getBufHandler().configureWriteBufferForWrite();
                        try {
                            int nRead = attachment.fchannel.read(buffer);
                            if (nRead > 0) {
                                Nio2SocketWrapper.this.getSocket().getBufHandler().configureWriteBufferForRead();
                                if (attachment.length < buffer.remaining()) {
                                    buffer.limit((buffer.limit() - buffer.remaining()) + ((int) attachment.length));
                                }
                                attachment.length -= nRead;
                            } else {
                                failed((Throwable) new EOFException(), attachment);
                                return;
                            }
                        } catch (IOException e2) {
                            failed((Throwable) e2, attachment);
                            return;
                        }
                    }
                    Nio2SocketWrapper.this.getSocket().write(buffer, Nio2Endpoint.toNio2Timeout(Nio2SocketWrapper.this.getWriteTimeout()), TimeUnit.MILLISECONDS, attachment, this);
                }

                @Override // java.nio.channels.CompletionHandler
                public void failed(Throwable exc, SendfileData attachment) {
                    try {
                        attachment.fchannel.close();
                    } catch (IOException e) {
                    }
                    if (!Nio2Endpoint.isInline()) {
                        Nio2SocketWrapper.this.getEndpoint().processSocket(Nio2SocketWrapper.this, SocketEvent.ERROR, false);
                        return;
                    }
                    attachment.doneInline = true;
                    attachment.error = true;
                }
            };
            this.socketBufferHandler = channel.getBufHandler();
            this.readCompletionHandler = new CompletionHandler<Integer, SocketWrapperBase<Nio2Channel>>() { // from class: org.apache.tomcat.util.net.Nio2Endpoint.Nio2SocketWrapper.4
                @Override // java.nio.channels.CompletionHandler
                public void completed(Integer nBytes, SocketWrapperBase<Nio2Channel> attachment) {
                    boolean notify = false;
                    if (Nio2Endpoint.log.isDebugEnabled()) {
                        Nio2Endpoint.log.debug("Socket: [" + attachment + "], Interest: [" + Nio2SocketWrapper.this.readInterest + "]");
                    }
                    synchronized (Nio2SocketWrapper.this.readCompletionHandler) {
                        if (nBytes.intValue() >= 0) {
                            if (!Nio2SocketWrapper.this.readInterest || Nio2Endpoint.isInline()) {
                                Nio2SocketWrapper.this.readPending.release();
                            } else {
                                Nio2SocketWrapper.this.readInterest = false;
                                notify = true;
                            }
                        } else {
                            failed((Throwable) new EOFException(), attachment);
                        }
                    }
                    if (notify) {
                        Nio2SocketWrapper.this.getEndpoint().processSocket(attachment, SocketEvent.OPEN_READ, false);
                    }
                }

                @Override // java.nio.channels.CompletionHandler
                public void failed(Throwable exc, SocketWrapperBase<Nio2Channel> attachment) {
                    IOException ioe;
                    if (exc instanceof IOException) {
                        ioe = (IOException) exc;
                    } else {
                        ioe = new IOException(exc);
                    }
                    Nio2SocketWrapper.this.setError(ioe);
                    if (exc instanceof AsynchronousCloseException) {
                        Nio2SocketWrapper.this.readPending.release();
                    } else {
                        Nio2SocketWrapper.this.getEndpoint().processSocket(attachment, SocketEvent.ERROR, true);
                    }
                }
            };
            this.writeCompletionHandler = new CompletionHandler<Integer, ByteBuffer>() { // from class: org.apache.tomcat.util.net.Nio2Endpoint.Nio2SocketWrapper.5
                @Override // java.nio.channels.CompletionHandler
                public void completed(Integer nBytes, ByteBuffer attachment) {
                    Nio2SocketWrapper.this.writeNotify = false;
                    synchronized (Nio2SocketWrapper.this.writeCompletionHandler) {
                        if (nBytes.intValue() < 0) {
                            failed((Throwable) new EOFException(SocketWrapperBase.sm.getString("iob.failedwrite")), attachment);
                        } else if (!Nio2SocketWrapper.this.nonBlockingWriteBuffer.isEmpty()) {
                            ((AtomicInteger) Nio2SocketWrapper.nestedWriteCompletionCount.get()).incrementAndGet();
                            ByteBuffer[] array = Nio2SocketWrapper.this.nonBlockingWriteBuffer.toArray(attachment);
                            Nio2SocketWrapper.this.getSocket().write(array, 0, array.length, Nio2Endpoint.toNio2Timeout(Nio2SocketWrapper.this.getWriteTimeout()), TimeUnit.MILLISECONDS, array, Nio2SocketWrapper.this.gatheringWriteCompletionHandler);
                            ((AtomicInteger) Nio2SocketWrapper.nestedWriteCompletionCount.get()).decrementAndGet();
                        } else if (attachment.hasRemaining()) {
                            ((AtomicInteger) Nio2SocketWrapper.nestedWriteCompletionCount.get()).incrementAndGet();
                            Nio2SocketWrapper.this.getSocket().write(attachment, Nio2Endpoint.toNio2Timeout(Nio2SocketWrapper.this.getWriteTimeout()), TimeUnit.MILLISECONDS, attachment, Nio2SocketWrapper.this.writeCompletionHandler);
                            ((AtomicInteger) Nio2SocketWrapper.nestedWriteCompletionCount.get()).decrementAndGet();
                        } else {
                            if (Nio2SocketWrapper.this.writeInterest) {
                                Nio2SocketWrapper.this.writeInterest = false;
                                Nio2SocketWrapper.this.writeNotify = true;
                            }
                            Nio2SocketWrapper.this.writePending.release();
                        }
                    }
                    if (Nio2SocketWrapper.this.writeNotify && ((AtomicInteger) Nio2SocketWrapper.nestedWriteCompletionCount.get()).get() == 0) {
                        endpoint.processSocket(Nio2SocketWrapper.this, SocketEvent.OPEN_WRITE, Nio2Endpoint.isInline());
                    }
                }

                @Override // java.nio.channels.CompletionHandler
                public void failed(Throwable exc, ByteBuffer attachment) {
                    IOException ioe;
                    if (exc instanceof IOException) {
                        ioe = (IOException) exc;
                    } else {
                        ioe = new IOException(exc);
                    }
                    Nio2SocketWrapper.this.setError(ioe);
                    Nio2SocketWrapper.this.writePending.release();
                    endpoint.processSocket(Nio2SocketWrapper.this, SocketEvent.ERROR, true);
                }
            };
            this.gatheringWriteCompletionHandler = new CompletionHandler<Long, ByteBuffer[]>() { // from class: org.apache.tomcat.util.net.Nio2Endpoint.Nio2SocketWrapper.6
                @Override // java.nio.channels.CompletionHandler
                public void completed(Long nBytes, ByteBuffer[] attachment) {
                    Nio2SocketWrapper.this.writeNotify = false;
                    synchronized (Nio2SocketWrapper.this.writeCompletionHandler) {
                        if (nBytes.longValue() < 0) {
                            failed((Throwable) new EOFException(SocketWrapperBase.sm.getString("iob.failedwrite")), attachment);
                        } else if (!Nio2SocketWrapper.this.nonBlockingWriteBuffer.isEmpty() || Nio2SocketWrapper.arrayHasData(attachment)) {
                            ((AtomicInteger) Nio2SocketWrapper.nestedWriteCompletionCount.get()).incrementAndGet();
                            ByteBuffer[] array = Nio2SocketWrapper.this.nonBlockingWriteBuffer.toArray(attachment);
                            Nio2SocketWrapper.this.getSocket().write(array, 0, array.length, Nio2Endpoint.toNio2Timeout(Nio2SocketWrapper.this.getWriteTimeout()), TimeUnit.MILLISECONDS, array, Nio2SocketWrapper.this.gatheringWriteCompletionHandler);
                            ((AtomicInteger) Nio2SocketWrapper.nestedWriteCompletionCount.get()).decrementAndGet();
                        } else {
                            if (Nio2SocketWrapper.this.writeInterest) {
                                Nio2SocketWrapper.this.writeInterest = false;
                                Nio2SocketWrapper.this.writeNotify = true;
                            }
                            Nio2SocketWrapper.this.writePending.release();
                        }
                    }
                    if (Nio2SocketWrapper.this.writeNotify && ((AtomicInteger) Nio2SocketWrapper.nestedWriteCompletionCount.get()).get() == 0) {
                        endpoint.processSocket(Nio2SocketWrapper.this, SocketEvent.OPEN_WRITE, Nio2Endpoint.isInline());
                    }
                }

                @Override // java.nio.channels.CompletionHandler
                public void failed(Throwable exc, ByteBuffer[] attachment) {
                    IOException ioe;
                    if (exc instanceof IOException) {
                        ioe = (IOException) exc;
                    } else {
                        ioe = new IOException(exc);
                    }
                    Nio2SocketWrapper.this.setError(ioe);
                    Nio2SocketWrapper.this.writePending.release();
                    endpoint.processSocket(Nio2SocketWrapper.this, SocketEvent.ERROR, true);
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static boolean arrayHasData(ByteBuffer[] byteBuffers) {
            for (ByteBuffer byteBuffer : byteBuffers) {
                if (byteBuffer.hasRemaining()) {
                    return true;
                }
            }
            return false;
        }

        public void setSendfileData(SendfileData sf) {
            this.sendfileData = sf;
        }

        public SendfileData getSendfileData() {
            return this.sendfileData;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean isReadyForRead() throws IOException {
            synchronized (this.readCompletionHandler) {
                if (!this.readPending.tryAcquire()) {
                    this.readInterest = true;
                    return false;
                } else if (!this.socketBufferHandler.isReadBufferEmpty()) {
                    this.readPending.release();
                    return true;
                } else {
                    int nRead = fillReadBuffer(false);
                    boolean isReady = nRead > 0;
                    if (!isReady) {
                        this.readInterest = true;
                    }
                    return isReady;
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public int read(boolean block, byte[] b, int off, int len) throws IOException {
            int i;
            checkError();
            if (Nio2Endpoint.log.isDebugEnabled()) {
                Nio2Endpoint.log.debug("Socket: [" + this + "], block: [" + block + "], length: [" + len + "]");
            }
            if (this.socketBufferHandler == null) {
                throw new IOException(sm.getString("socket.closed"));
            }
            if (block) {
                try {
                    this.readPending.acquire();
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
            } else if (!this.readPending.tryAcquire()) {
                if (Nio2Endpoint.log.isDebugEnabled()) {
                    Nio2Endpoint.log.debug("Socket: [" + this + "], Read in progress. Returning [0]");
                    return 0;
                }
                return 0;
            }
            int nRead = populateReadBuffer(b, off, len);
            if (nRead > 0) {
                this.readPending.release();
                return nRead;
            }
            synchronized (this.readCompletionHandler) {
                int nRead2 = fillReadBuffer(block);
                if (nRead2 > 0) {
                    this.socketBufferHandler.configureReadBufferForRead();
                    nRead2 = Math.min(nRead2, len);
                    this.socketBufferHandler.getReadBuffer().get(b, off, nRead2);
                } else if (nRead2 == 0 && !block) {
                    this.readInterest = true;
                }
                if (Nio2Endpoint.log.isDebugEnabled()) {
                    Nio2Endpoint.log.debug("Socket: [" + this + "], Read: [" + nRead2 + "]");
                }
                i = nRead2;
            }
            return i;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public int read(boolean block, ByteBuffer to) throws IOException {
            int nRead;
            int i;
            checkError();
            if (this.socketBufferHandler == null) {
                throw new IOException(sm.getString("socket.closed"));
            }
            if (block) {
                try {
                    this.readPending.acquire();
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
            } else if (!this.readPending.tryAcquire()) {
                if (Nio2Endpoint.log.isDebugEnabled()) {
                    Nio2Endpoint.log.debug("Socket: [" + this + "], Read in progress. Returning [0]");
                    return 0;
                }
                return 0;
            }
            int nRead2 = populateReadBuffer(to);
            if (nRead2 > 0) {
                this.readPending.release();
                return nRead2;
            }
            synchronized (this.readCompletionHandler) {
                int limit = this.socketBufferHandler.getReadBuffer().capacity();
                if (block && to.remaining() >= limit) {
                    to.limit(to.position() + limit);
                    nRead = fillReadBuffer(block, to);
                    if (Nio2Endpoint.log.isDebugEnabled()) {
                        Nio2Endpoint.log.debug("Socket: [" + this + "], Read direct from socket: [" + nRead + "]");
                    }
                } else {
                    nRead = fillReadBuffer(block);
                    if (Nio2Endpoint.log.isDebugEnabled()) {
                        Nio2Endpoint.log.debug("Socket: [" + this + "], Read into buffer: [" + nRead + "]");
                    }
                    if (nRead > 0) {
                        nRead = populateReadBuffer(to);
                    } else if (nRead == 0 && !block) {
                        this.readInterest = true;
                    }
                }
                i = nRead;
            }
            return i;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void close() throws IOException {
            getSocket().close();
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean isClosed() {
            return this.closed || !getSocket().isOpen();
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean isWritePending() {
            boolean z;
            synchronized (this.writeCompletionHandler) {
                z = this.writePending.availablePermits() == 0;
            }
            return z;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean hasAsyncIO() {
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/Nio2Endpoint$Nio2SocketWrapper$OperationState.class */
        public static class OperationState<A> {
            private final boolean read;
            private final ByteBuffer[] buffers;
            private final int offset;
            private final int length;
            private final A attachment;
            private final long timeout;
            private final TimeUnit unit;
            private final SocketWrapperBase.BlockingMode block;
            private final SocketWrapperBase.CompletionCheck check;
            private final CompletionHandler<Long, ? super A> handler;
            private final Semaphore semaphore;
            private volatile long nBytes;
            private volatile SocketWrapperBase.CompletionState state;

            private OperationState(boolean read, ByteBuffer[] buffers, int offset, int length, SocketWrapperBase.BlockingMode block, long timeout, TimeUnit unit, A attachment, SocketWrapperBase.CompletionCheck check, CompletionHandler<Long, ? super A> handler, Semaphore semaphore) {
                this.nBytes = 0L;
                this.state = SocketWrapperBase.CompletionState.PENDING;
                this.read = read;
                this.buffers = buffers;
                this.offset = offset;
                this.length = length;
                this.block = block;
                this.timeout = timeout;
                this.unit = unit;
                this.attachment = attachment;
                this.check = check;
                this.handler = handler;
                this.semaphore = semaphore;
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public <A> SocketWrapperBase.CompletionState read(ByteBuffer[] dsts, int offset, int length, SocketWrapperBase.BlockingMode block, long timeout, TimeUnit unit, A attachment, SocketWrapperBase.CompletionCheck check, CompletionHandler<Long, ? super A> handler) {
            IOException ioe = getError();
            if (ioe != null) {
                handler.failed(ioe, attachment);
                return SocketWrapperBase.CompletionState.ERROR;
            }
            if (timeout == -1) {
                timeout = Nio2Endpoint.toNio2Timeout(getReadTimeout());
            }
            if (block != SocketWrapperBase.BlockingMode.NON_BLOCK) {
                try {
                    if (!this.readPending.tryAcquire(timeout, unit)) {
                        handler.failed(new SocketTimeoutException(), attachment);
                        return SocketWrapperBase.CompletionState.ERROR;
                    }
                } catch (InterruptedException e) {
                    handler.failed(e, attachment);
                    return SocketWrapperBase.CompletionState.ERROR;
                }
            } else if (!this.readPending.tryAcquire()) {
                return SocketWrapperBase.CompletionState.NOT_DONE;
            }
            OperationState<A> state = new OperationState<>(true, dsts, offset, length, block, timeout, unit, attachment, check, handler, this.readPending);
            VectoredIOCompletionHandler<A> completion = new VectoredIOCompletionHandler<>();
            Nio2Endpoint.startInline();
            long nBytes = 0;
            if (!this.socketBufferHandler.isReadBufferEmpty()) {
                synchronized (this.readCompletionHandler) {
                    this.socketBufferHandler.configureReadBufferForRead();
                    for (int i = 0; i < length && !this.socketBufferHandler.isReadBufferEmpty(); i++) {
                        nBytes += transfer(this.socketBufferHandler.getReadBuffer(), dsts[offset + i]);
                    }
                }
                if (nBytes > 0) {
                    completion.completed(Long.valueOf(nBytes), (OperationState) state);
                }
            }
            if (nBytes == 0) {
                getSocket().read(dsts, offset, length, timeout, unit, state, completion);
            }
            Nio2Endpoint.endInline();
            if (block == SocketWrapperBase.BlockingMode.BLOCK) {
                synchronized (state) {
                    if (((OperationState) state).state == SocketWrapperBase.CompletionState.PENDING) {
                        try {
                            state.wait(unit.toMillis(timeout));
                            if (((OperationState) state).state == SocketWrapperBase.CompletionState.PENDING) {
                                return SocketWrapperBase.CompletionState.ERROR;
                            }
                        } catch (InterruptedException e2) {
                            handler.failed(new SocketTimeoutException(), attachment);
                            return SocketWrapperBase.CompletionState.ERROR;
                        }
                    }
                }
            }
            return ((OperationState) state).state;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public <A> SocketWrapperBase.CompletionState write(ByteBuffer[] srcs, int offset, int length, SocketWrapperBase.BlockingMode block, long timeout, TimeUnit unit, A attachment, SocketWrapperBase.CompletionCheck check, CompletionHandler<Long, ? super A> handler) {
            IOException ioe = getError();
            if (ioe != null) {
                handler.failed(ioe, attachment);
                return SocketWrapperBase.CompletionState.ERROR;
            }
            if (timeout == -1) {
                timeout = Nio2Endpoint.toNio2Timeout(getWriteTimeout());
            }
            if (block != SocketWrapperBase.BlockingMode.NON_BLOCK) {
                try {
                    if (!this.writePending.tryAcquire(timeout, unit)) {
                        handler.failed(new SocketTimeoutException(), attachment);
                        return SocketWrapperBase.CompletionState.ERROR;
                    }
                } catch (InterruptedException e) {
                    handler.failed(e, attachment);
                    return SocketWrapperBase.CompletionState.ERROR;
                }
            } else if (!this.writePending.tryAcquire()) {
                return SocketWrapperBase.CompletionState.NOT_DONE;
            }
            if (!this.socketBufferHandler.isWriteBufferEmpty()) {
                try {
                    doWrite(true);
                } catch (IOException e2) {
                    handler.failed(e2, attachment);
                    return SocketWrapperBase.CompletionState.ERROR;
                }
            }
            OperationState<A> state = new OperationState<>(false, srcs, offset, length, block, timeout, unit, attachment, check, handler, this.writePending);
            VectoredIOCompletionHandler<A> completion = new VectoredIOCompletionHandler<>();
            Nio2Endpoint.startInline();
            getSocket().write(srcs, offset, length, timeout, unit, state, completion);
            Nio2Endpoint.endInline();
            if (block == SocketWrapperBase.BlockingMode.BLOCK) {
                synchronized (state) {
                    if (((OperationState) state).state == SocketWrapperBase.CompletionState.PENDING) {
                        try {
                            state.wait(unit.toMillis(timeout));
                            if (((OperationState) state).state == SocketWrapperBase.CompletionState.PENDING) {
                                return SocketWrapperBase.CompletionState.ERROR;
                            }
                        } catch (InterruptedException e3) {
                            handler.failed(new SocketTimeoutException(), attachment);
                            return SocketWrapperBase.CompletionState.ERROR;
                        }
                    }
                }
            }
            return ((OperationState) state).state;
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/Nio2Endpoint$Nio2SocketWrapper$VectoredIOCompletionHandler.class */
        private class VectoredIOCompletionHandler<A> implements CompletionHandler<Long, OperationState<A>> {
            private VectoredIOCompletionHandler() {
            }

            @Override // java.nio.channels.CompletionHandler
            public /* bridge */ /* synthetic */ void failed(Throwable th, Object obj) {
                failed(th, (OperationState) ((OperationState) obj));
            }

            @Override // java.nio.channels.CompletionHandler
            public /* bridge */ /* synthetic */ void completed(Long l, Object obj) {
                completed(l, (OperationState) ((OperationState) obj));
            }

            /* JADX WARN: Multi-variable type inference failed */
            public void completed(Long nBytes, OperationState<A> state) {
                if (nBytes.longValue() >= 0) {
                    ((OperationState) state).nBytes += nBytes.longValue();
                    SocketWrapperBase.CompletionState currentState = Nio2Endpoint.isInline() ? SocketWrapperBase.CompletionState.INLINE : SocketWrapperBase.CompletionState.DONE;
                    boolean complete = true;
                    boolean completion = true;
                    if (((OperationState) state).check != null) {
                        switch (((OperationState) state).check.callHandler(currentState, ((OperationState) state).buffers, ((OperationState) state).offset, ((OperationState) state).length)) {
                            case CONTINUE:
                                complete = false;
                                break;
                            case NONE:
                                completion = false;
                                break;
                        }
                    }
                    if (!complete) {
                        if (((OperationState) state).read) {
                            Nio2SocketWrapper.this.getSocket().read(((OperationState) state).buffers, ((OperationState) state).offset, ((OperationState) state).length, ((OperationState) state).timeout, ((OperationState) state).unit, state, this);
                            return;
                        } else {
                            Nio2SocketWrapper.this.getSocket().write(((OperationState) state).buffers, ((OperationState) state).offset, ((OperationState) state).length, ((OperationState) state).timeout, ((OperationState) state).unit, state, this);
                            return;
                        }
                    }
                    boolean notify = false;
                    ((OperationState) state).semaphore.release();
                    if (((OperationState) state).block != SocketWrapperBase.BlockingMode.BLOCK || currentState == SocketWrapperBase.CompletionState.INLINE) {
                        ((OperationState) state).state = currentState;
                    } else {
                        notify = true;
                    }
                    if (completion && ((OperationState) state).handler != null) {
                        ((OperationState) state).handler.completed(Long.valueOf(((OperationState) state).nBytes), ((OperationState) state).attachment);
                    }
                    if (notify) {
                        synchronized (state) {
                            ((OperationState) state).state = currentState;
                            state.notify();
                        }
                        return;
                    }
                    return;
                }
                failed((Throwable) new EOFException(), (OperationState) state);
            }

            /* JADX WARN: Multi-variable type inference failed */
            public void failed(Throwable exc, OperationState<A> state) {
                IOException ioe;
                if (exc instanceof IOException) {
                    ioe = (IOException) exc;
                } else {
                    ioe = new IOException(exc);
                }
                Nio2SocketWrapper.this.setError(ioe);
                boolean notify = false;
                ((OperationState) state).semaphore.release();
                if (((OperationState) state).block != SocketWrapperBase.BlockingMode.BLOCK) {
                    ((OperationState) state).state = Nio2Endpoint.isInline() ? SocketWrapperBase.CompletionState.ERROR : SocketWrapperBase.CompletionState.DONE;
                } else {
                    notify = true;
                }
                if (((OperationState) state).handler != null) {
                    ((OperationState) state).handler.failed(ioe, ((OperationState) state).attachment);
                }
                if (notify) {
                    synchronized (state) {
                        ((OperationState) state).state = Nio2Endpoint.isInline() ? SocketWrapperBase.CompletionState.ERROR : SocketWrapperBase.CompletionState.DONE;
                        state.notify();
                    }
                }
            }
        }

        private int fillReadBuffer(boolean block) throws IOException {
            this.socketBufferHandler.configureReadBufferForWrite();
            return fillReadBuffer(block, this.socketBufferHandler.getReadBuffer());
        }

        private int fillReadBuffer(boolean block, ByteBuffer to) throws IOException {
            int nRead = 0;
            Future<Integer> integer = null;
            try {
                if (block) {
                    try {
                        try {
                            try {
                                Future<Integer> integer2 = getSocket().read(to);
                                long timeout = getReadTimeout();
                                if (timeout > 0) {
                                    nRead = integer2.get(timeout, TimeUnit.MILLISECONDS).intValue();
                                } else {
                                    nRead = integer2.get().intValue();
                                }
                            } catch (TimeoutException e) {
                                integer.cancel(true);
                                throw new SocketTimeoutException();
                            }
                        } catch (ExecutionException e2) {
                            if (e2.getCause() instanceof IOException) {
                                throw ((IOException) e2.getCause());
                            }
                            throw new IOException(e2);
                        }
                    } catch (InterruptedException e3) {
                        throw new IOException(e3);
                    }
                } else {
                    Nio2Endpoint.startInline();
                    getSocket().read(to, Nio2Endpoint.toNio2Timeout(getReadTimeout()), TimeUnit.MILLISECONDS, this, this.readCompletionHandler);
                    Nio2Endpoint.endInline();
                    if (this.readPending.availablePermits() == 1) {
                        nRead = to.position();
                    }
                }
                return nRead;
            } finally {
                this.readPending.release();
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void writeNonBlocking(byte[] buf, int off, int len) throws IOException {
            synchronized (this.writeCompletionHandler) {
                if (this.writePending.tryAcquire()) {
                    this.socketBufferHandler.configureWriteBufferForWrite();
                    int thisTime = transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
                    int len2 = len - thisTime;
                    int off2 = off + thisTime;
                    if (len2 > 0) {
                        this.nonBlockingWriteBuffer.add(buf, off2, len2);
                    }
                    flushNonBlocking(true);
                } else {
                    this.nonBlockingWriteBuffer.add(buf, off, len);
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void writeNonBlocking(ByteBuffer from) throws IOException {
            writeNonBlockingInternal(from);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void writeNonBlockingInternal(ByteBuffer from) throws IOException {
            synchronized (this.writeCompletionHandler) {
                if (this.writePending.tryAcquire()) {
                    this.socketBufferHandler.configureWriteBufferForWrite();
                    transfer(from, this.socketBufferHandler.getWriteBuffer());
                    if (from.remaining() > 0) {
                        this.nonBlockingWriteBuffer.add(from);
                    }
                    flushNonBlocking(true);
                } else {
                    this.nonBlockingWriteBuffer.add(from);
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void doWrite(boolean block, ByteBuffer from) throws IOException {
            Future<Integer> integer = null;
            do {
                try {
                    integer = getSocket().write(from);
                    long timeout = getWriteTimeout();
                    if (timeout > 0) {
                        if (integer.get(timeout, TimeUnit.MILLISECONDS).intValue() < 0) {
                            throw new EOFException(sm.getString("iob.failedwrite"));
                        }
                    } else if (integer.get().intValue() < 0) {
                        throw new EOFException(sm.getString("iob.failedwrite"));
                    }
                } catch (InterruptedException e) {
                    throw new IOException(e);
                } catch (ExecutionException e2) {
                    if (e2.getCause() instanceof IOException) {
                        throw ((IOException) e2.getCause());
                    }
                    throw new IOException(e2);
                } catch (TimeoutException e3) {
                    integer.cancel(true);
                    throw new SocketTimeoutException();
                }
            } while (from.hasRemaining());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void flushBlocking() throws IOException {
            checkError();
            if (this.writePending.tryAcquire(Nio2Endpoint.toNio2Timeout(getWriteTimeout()), TimeUnit.MILLISECONDS)) {
                this.writePending.release();
                super.flushBlocking();
                return;
            }
            throw new SocketTimeoutException();
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected boolean flushNonBlocking() throws IOException {
            return flushNonBlocking(false);
        }

        /* JADX WARN: Code restructure failed: missing block: B:7:0x0016, code lost:
            if (r10.writePending.tryAcquire() != false) goto L5;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private boolean flushNonBlocking(boolean r11) throws java.io.IOException {
            /*
                r10 = this;
                r0 = r10
                r0.checkError()
                r0 = r10
                java.nio.channels.CompletionHandler<java.lang.Integer, java.nio.ByteBuffer> r0 = r0.writeCompletionHandler
                r1 = r0
                r12 = r1
                monitor-enter(r0)
                r0 = r11
                if (r0 != 0) goto L19
                r0 = r10
                java.util.concurrent.Semaphore r0 = r0.writePending     // Catch: java.lang.Throwable -> Lb4
                boolean r0 = r0.tryAcquire()     // Catch: java.lang.Throwable -> Lb4
                if (r0 == 0) goto Lad
            L19:
                r0 = r10
                org.apache.tomcat.util.net.SocketBufferHandler r0 = r0.socketBufferHandler     // Catch: java.lang.Throwable -> Lb4
                r0.configureWriteBufferForRead()     // Catch: java.lang.Throwable -> Lb4
                r0 = r10
                org.apache.tomcat.util.net.WriteBuffer r0 = r0.nonBlockingWriteBuffer     // Catch: java.lang.Throwable -> Lb4
                boolean r0 = r0.isEmpty()     // Catch: java.lang.Throwable -> Lb4
                if (r0 != 0) goto L66
                r0 = r10
                org.apache.tomcat.util.net.WriteBuffer r0 = r0.nonBlockingWriteBuffer     // Catch: java.lang.Throwable -> Lb4
                r1 = 1
                java.nio.ByteBuffer[] r1 = new java.nio.ByteBuffer[r1]     // Catch: java.lang.Throwable -> Lb4
                r2 = r1
                r3 = 0
                r4 = r10
                org.apache.tomcat.util.net.SocketBufferHandler r4 = r4.socketBufferHandler     // Catch: java.lang.Throwable -> Lb4
                java.nio.ByteBuffer r4 = r4.getWriteBuffer()     // Catch: java.lang.Throwable -> Lb4
                r2[r3] = r4     // Catch: java.lang.Throwable -> Lb4
                java.nio.ByteBuffer[] r0 = r0.toArray(r1)     // Catch: java.lang.Throwable -> Lb4
                r13 = r0
                org.apache.tomcat.util.net.Nio2Endpoint.startInline()     // Catch: java.lang.Throwable -> Lb4
                r0 = r10
                java.lang.Object r0 = r0.getSocket()     // Catch: java.lang.Throwable -> Lb4
                org.apache.tomcat.util.net.Nio2Channel r0 = (org.apache.tomcat.util.net.Nio2Channel) r0     // Catch: java.lang.Throwable -> Lb4
                r1 = r13
                r2 = 0
                r3 = r13
                int r3 = r3.length     // Catch: java.lang.Throwable -> Lb4
                r4 = r10
                long r4 = r4.getWriteTimeout()     // Catch: java.lang.Throwable -> Lb4
                long r4 = org.apache.tomcat.util.net.Nio2Endpoint.toNio2Timeout(r4)     // Catch: java.lang.Throwable -> Lb4
                java.util.concurrent.TimeUnit r5 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch: java.lang.Throwable -> Lb4
                r6 = r13
                r7 = r10
                java.nio.channels.CompletionHandler<java.lang.Long, java.nio.ByteBuffer[]> r7 = r7.gatheringWriteCompletionHandler     // Catch: java.lang.Throwable -> Lb4
                r0.write(r1, r2, r3, r4, r5, r6, r7)     // Catch: java.lang.Throwable -> Lb4
                org.apache.tomcat.util.net.Nio2Endpoint.endInline()     // Catch: java.lang.Throwable -> Lb4
                goto Lad
            L66:
                r0 = r10
                org.apache.tomcat.util.net.SocketBufferHandler r0 = r0.socketBufferHandler     // Catch: java.lang.Throwable -> Lb4
                java.nio.ByteBuffer r0 = r0.getWriteBuffer()     // Catch: java.lang.Throwable -> Lb4
                boolean r0 = r0.hasRemaining()     // Catch: java.lang.Throwable -> Lb4
                if (r0 == 0) goto La2
                org.apache.tomcat.util.net.Nio2Endpoint.startInline()     // Catch: java.lang.Throwable -> Lb4
                r0 = r10
                java.lang.Object r0 = r0.getSocket()     // Catch: java.lang.Throwable -> Lb4
                org.apache.tomcat.util.net.Nio2Channel r0 = (org.apache.tomcat.util.net.Nio2Channel) r0     // Catch: java.lang.Throwable -> Lb4
                r1 = r10
                org.apache.tomcat.util.net.SocketBufferHandler r1 = r1.socketBufferHandler     // Catch: java.lang.Throwable -> Lb4
                java.nio.ByteBuffer r1 = r1.getWriteBuffer()     // Catch: java.lang.Throwable -> Lb4
                r2 = r10
                long r2 = r2.getWriteTimeout()     // Catch: java.lang.Throwable -> Lb4
                long r2 = org.apache.tomcat.util.net.Nio2Endpoint.toNio2Timeout(r2)     // Catch: java.lang.Throwable -> Lb4
                java.util.concurrent.TimeUnit r3 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch: java.lang.Throwable -> Lb4
                r4 = r10
                org.apache.tomcat.util.net.SocketBufferHandler r4 = r4.socketBufferHandler     // Catch: java.lang.Throwable -> Lb4
                java.nio.ByteBuffer r4 = r4.getWriteBuffer()     // Catch: java.lang.Throwable -> Lb4
                r5 = r10
                java.nio.channels.CompletionHandler<java.lang.Integer, java.nio.ByteBuffer> r5 = r5.writeCompletionHandler     // Catch: java.lang.Throwable -> Lb4
                r0.write(r1, r2, r3, r4, r5)     // Catch: java.lang.Throwable -> Lb4
                org.apache.tomcat.util.net.Nio2Endpoint.endInline()     // Catch: java.lang.Throwable -> Lb4
                goto Lad
            La2:
                r0 = r11
                if (r0 != 0) goto Lad
                r0 = r10
                java.util.concurrent.Semaphore r0 = r0.writePending     // Catch: java.lang.Throwable -> Lb4
                r0.release()     // Catch: java.lang.Throwable -> Lb4
            Lad:
                r0 = r10
                boolean r0 = r0.hasDataToWrite()     // Catch: java.lang.Throwable -> Lb4
                r1 = r12
                monitor-exit(r1)     // Catch: java.lang.Throwable -> Lb4
                return r0
            Lb4:
                r14 = move-exception
                r0 = r12
                monitor-exit(r0)     // Catch: java.lang.Throwable -> Lb4
                r0 = r14
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.net.Nio2Endpoint.Nio2SocketWrapper.flushNonBlocking(boolean):boolean");
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean hasDataToWrite() {
            boolean z;
            synchronized (this.writeCompletionHandler) {
                z = (this.socketBufferHandler.isWriteBufferEmpty() && this.nonBlockingWriteBuffer.isEmpty() && getError() == null) ? false : true;
            }
            return z;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean isReadPending() {
            boolean z;
            synchronized (this.readCompletionHandler) {
                z = this.readPending.availablePermits() == 0;
            }
            return z;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean awaitReadComplete(long timeout, TimeUnit unit) {
            try {
                if (this.readPending.tryAcquire(timeout, unit)) {
                    this.readPending.release();
                    return true;
                }
                return false;
            } catch (InterruptedException e) {
                return false;
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean awaitWriteComplete(long timeout, TimeUnit unit) {
            try {
                if (this.writePending.tryAcquire(timeout, unit)) {
                    this.writePending.release();
                    return true;
                }
                return false;
            } catch (InterruptedException e) {
                return false;
            }
        }

        void releaseReadPending() {
            synchronized (this.readCompletionHandler) {
                if (this.readPending.availablePermits() == 0) {
                    this.readPending.release();
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void registerReadInterest() {
            synchronized (this.readCompletionHandler) {
                if (this.readPending.availablePermits() == 0) {
                    this.readInterest = true;
                } else {
                    awaitBytes();
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void registerWriteInterest() {
            synchronized (this.writeCompletionHandler) {
                if (this.writePending.availablePermits() == 0) {
                    this.writeInterest = true;
                } else {
                    getEndpoint().processSocket(this, SocketEvent.OPEN_WRITE, true);
                }
            }
        }

        public void awaitBytes() {
            if (this.readPending.tryAcquire()) {
                getSocket().getBufHandler().configureReadBufferForWrite();
                Nio2Endpoint.startInline();
                getSocket().read(getSocket().getBufHandler().getReadBuffer(), Nio2Endpoint.toNio2Timeout(getReadTimeout()), TimeUnit.MILLISECONDS, this, this.awaitBytesHandler);
                Nio2Endpoint.endInline();
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public SendfileDataBase createSendfileData(String filename, long pos, long length) {
            return new SendfileData(filename, pos, length);
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public SendfileState processSendfile(SendfileDataBase sendfileData) {
            SendfileData data = (SendfileData) sendfileData;
            setSendfileData(data);
            if (data.fchannel == null || !data.fchannel.isOpen()) {
                Path path = new File(sendfileData.fileName).toPath();
                try {
                    data.fchannel = FileChannel.open(path, StandardOpenOption.READ).position(sendfileData.pos);
                } catch (IOException e) {
                    return SendfileState.ERROR;
                }
            }
            getSocket().getBufHandler().configureWriteBufferForWrite();
            ByteBuffer buffer = getSocket().getBufHandler().getWriteBuffer();
            try {
                int nRead = data.fchannel.read(buffer);
                if (nRead >= 0) {
                    data.length -= nRead;
                    getSocket().getBufHandler().configureWriteBufferForRead();
                    Nio2Endpoint.startInline();
                    getSocket().write(buffer, Nio2Endpoint.toNio2Timeout(getWriteTimeout()), TimeUnit.MILLISECONDS, data, this.sendfileHandler);
                    Nio2Endpoint.endInline();
                    if (!data.doneInline) {
                        return SendfileState.PENDING;
                    }
                    if (data.error) {
                        return SendfileState.ERROR;
                    }
                    return SendfileState.DONE;
                }
                return SendfileState.ERROR;
            } catch (IOException e2) {
                return SendfileState.ERROR;
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateRemoteAddr() {
            SocketAddress socketAddress = null;
            try {
                socketAddress = getSocket().getIOChannel().getRemoteAddress();
            } catch (IOException e) {
            }
            if (socketAddress instanceof InetSocketAddress) {
                this.remoteAddr = ((InetSocketAddress) socketAddress).getAddress().getHostAddress();
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateRemoteHost() {
            SocketAddress socketAddress = null;
            try {
                socketAddress = getSocket().getIOChannel().getRemoteAddress();
            } catch (IOException e) {
                Nio2Endpoint.log.warn(sm.getString("endpoint.warn.noRemoteHost", getSocket()), e);
            }
            if (socketAddress instanceof InetSocketAddress) {
                this.remoteHost = ((InetSocketAddress) socketAddress).getAddress().getHostName();
                if (this.remoteAddr == null) {
                    this.remoteAddr = ((InetSocketAddress) socketAddress).getAddress().getHostAddress();
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateRemotePort() {
            SocketAddress socketAddress = null;
            try {
                socketAddress = getSocket().getIOChannel().getRemoteAddress();
            } catch (IOException e) {
                Nio2Endpoint.log.warn(sm.getString("endpoint.warn.noRemotePort", getSocket()), e);
            }
            if (socketAddress instanceof InetSocketAddress) {
                this.remotePort = ((InetSocketAddress) socketAddress).getPort();
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateLocalName() {
            SocketAddress socketAddress = null;
            try {
                socketAddress = getSocket().getIOChannel().getLocalAddress();
            } catch (IOException e) {
                Nio2Endpoint.log.warn(sm.getString("endpoint.warn.noLocalName", getSocket()), e);
            }
            if (socketAddress instanceof InetSocketAddress) {
                this.localName = ((InetSocketAddress) socketAddress).getHostName();
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateLocalAddr() {
            SocketAddress socketAddress = null;
            try {
                socketAddress = getSocket().getIOChannel().getLocalAddress();
            } catch (IOException e) {
                Nio2Endpoint.log.warn(sm.getString("endpoint.warn.noLocalAddr", getSocket()), e);
            }
            if (socketAddress instanceof InetSocketAddress) {
                this.localAddr = ((InetSocketAddress) socketAddress).getAddress().getHostAddress();
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateLocalPort() {
            SocketAddress socketAddress = null;
            try {
                socketAddress = getSocket().getIOChannel().getLocalAddress();
            } catch (IOException e) {
                Nio2Endpoint.log.warn(sm.getString("endpoint.warn.noLocalPort", getSocket()), e);
            }
            if (socketAddress instanceof InetSocketAddress) {
                this.localPort = ((InetSocketAddress) socketAddress).getPort();
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public SSLSupport getSslSupport(String clientCertProvider) {
            if (getSocket() instanceof SecureNio2Channel) {
                SecureNio2Channel ch2 = (SecureNio2Channel) getSocket();
                SSLSession session = ch2.getSslEngine().getSession();
                return ((Nio2Endpoint) getEndpoint()).getSslImplementation().getSSLSupport(session);
            }
            return null;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void doClientAuth(SSLSupport sslSupport) throws IOException {
            SecureNio2Channel sslChannel = (SecureNio2Channel) getSocket();
            SSLEngine engine = sslChannel.getSslEngine();
            if (!engine.getNeedClientAuth()) {
                engine.setNeedClientAuth(true);
                sslChannel.rehandshake();
                ((JSSESupport) sslSupport).setSession(engine.getSession());
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void setAppReadBufHandler(ApplicationBufferHandler handler) {
            getSocket().setAppReadBufHandler(handler);
        }
    }

    public static long toNio2Timeout(long timeout) {
        if (timeout > 0) {
            return timeout;
        }
        return Long.MAX_VALUE;
    }

    public static void startInline() {
        inlineCompletion.set(Boolean.TRUE);
    }

    public static void endInline() {
        inlineCompletion.set(Boolean.FALSE);
    }

    public static boolean isInline() {
        Boolean flag = inlineCompletion.get();
        if (flag == null) {
            return false;
        }
        return flag.booleanValue();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/Nio2Endpoint$SocketProcessor.class */
    protected class SocketProcessor extends SocketProcessorBase<Nio2Channel> {
        public SocketProcessor(SocketWrapperBase<Nio2Channel> socketWrapper, SocketEvent event) {
            super(socketWrapper, event);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.apache.tomcat.util.net.SocketProcessorBase
        protected void doRun() {
            int handshake;
            if (SocketEvent.OPEN_WRITE != this.event) {
                ((Nio2SocketWrapper) this.socketWrapper).releaseReadPending();
            }
            boolean launch = false;
            try {
                try {
                    try {
                        if (((Nio2Channel) this.socketWrapper.getSocket()).isHandshakeComplete()) {
                            handshake = 0;
                        } else if (this.event == SocketEvent.STOP || this.event == SocketEvent.DISCONNECT || this.event == SocketEvent.ERROR) {
                            handshake = -1;
                        } else {
                            handshake = ((Nio2Channel) this.socketWrapper.getSocket()).handshake();
                            this.event = SocketEvent.OPEN_READ;
                        }
                    } catch (IOException x) {
                        handshake = -1;
                        if (Nio2Endpoint.log.isDebugEnabled()) {
                            Nio2Endpoint.log.debug(AbstractEndpoint.sm.getString("endpoint.err.handshake"), x);
                        }
                    }
                    if (handshake == 0) {
                        AbstractEndpoint.Handler.SocketState socketState = AbstractEndpoint.Handler.SocketState.OPEN;
                        AbstractEndpoint.Handler.SocketState state = this.event == null ? Nio2Endpoint.this.getHandler().process(this.socketWrapper, SocketEvent.OPEN_READ) : Nio2Endpoint.this.getHandler().process(this.socketWrapper, this.event);
                        if (state == AbstractEndpoint.Handler.SocketState.CLOSED) {
                            Nio2Endpoint.this.closeSocket((SocketWrapperBase<Nio2Channel>) this.socketWrapper);
                            if (Nio2Endpoint.this.running && !Nio2Endpoint.this.paused && !Nio2Endpoint.this.nioChannels.push(this.socketWrapper.getSocket())) {
                                ((Nio2Channel) this.socketWrapper.getSocket()).free();
                            }
                        } else if (state == AbstractEndpoint.Handler.SocketState.UPGRADING) {
                            launch = true;
                        }
                    } else if (handshake == -1) {
                        Nio2Endpoint.this.closeSocket((SocketWrapperBase<Nio2Channel>) this.socketWrapper);
                        if (Nio2Endpoint.this.running && !Nio2Endpoint.this.paused && !Nio2Endpoint.this.nioChannels.push(this.socketWrapper.getSocket())) {
                            ((Nio2Channel) this.socketWrapper.getSocket()).free();
                        }
                    }
                    launch = launch;
                } catch (VirtualMachineError vme) {
                    ExceptionUtils.handleThrowable(vme);
                    if (0 != 0) {
                        try {
                            Nio2Endpoint.this.getExecutor().execute(new SocketProcessor(this.socketWrapper, SocketEvent.OPEN_READ));
                        } catch (NullPointerException npe) {
                            if (Nio2Endpoint.this.running) {
                                Nio2Endpoint.log.error(AbstractEndpoint.sm.getString("endpoint.launch.fail"), npe);
                            }
                        }
                    }
                    this.socketWrapper = null;
                    this.event = null;
                    if (!Nio2Endpoint.this.running || Nio2Endpoint.this.paused) {
                        return;
                    }
                    Nio2Endpoint.this.processorCache.push(this);
                }
            } finally {
                if (0 != 0) {
                    try {
                        Nio2Endpoint.this.getExecutor().execute(new SocketProcessor(this.socketWrapper, SocketEvent.OPEN_READ));
                    } catch (NullPointerException npe2) {
                        if (Nio2Endpoint.this.running) {
                            Nio2Endpoint.log.error(AbstractEndpoint.sm.getString("endpoint.launch.fail"), npe2);
                        }
                    }
                }
                this.socketWrapper = null;
                this.event = null;
                if (Nio2Endpoint.this.running && !Nio2Endpoint.this.paused) {
                    Nio2Endpoint.this.processorCache.push(this);
                }
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/Nio2Endpoint$SendfileData.class */
    public static class SendfileData extends SendfileDataBase {
        private FileChannel fchannel;
        private boolean doneInline;
        private boolean error;

        public SendfileData(String filename, long pos, long length) {
            super(filename, pos, length);
            this.doneInline = false;
            this.error = false;
        }
    }
}