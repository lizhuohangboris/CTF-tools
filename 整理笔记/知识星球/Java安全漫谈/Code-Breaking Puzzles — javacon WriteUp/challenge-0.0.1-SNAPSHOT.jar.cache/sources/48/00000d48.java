package org.apache.tomcat.util.net;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.collections.SynchronizedQueue;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.jsse.JSSESupport;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/NioEndpoint.class */
public class NioEndpoint extends AbstractJsseEndpoint<NioChannel, SocketChannel> {
    private static final Log log = LogFactory.getLog(NioEndpoint.class);
    public static final int OP_REGISTER = 256;
    private SynchronizedStack<PollerEvent> eventCache;
    private SynchronizedStack<NioChannel> nioChannels;
    private NioSelectorPool selectorPool = new NioSelectorPool();
    private volatile ServerSocketChannel serverSock = null;
    private volatile CountDownLatch stopLatch = null;
    private boolean useInheritedChannel = false;
    private int pollerThreadPriority = 5;
    private int pollerThreadCount = Math.min(2, Runtime.getRuntime().availableProcessors());
    private long selectorTimeout = 1000;
    private Poller[] pollers = null;
    private AtomicInteger pollerRotater = new AtomicInteger(0);

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public boolean setProperty(String name, String value) {
        try {
            if (name.startsWith("selectorPool.")) {
                return IntrospectionUtils.setProperty(this.selectorPool, name.substring("selectorPool.".length()), value);
            }
            return super.setProperty(name, value);
        } catch (Exception x) {
            log.error("Unable to set attribute \"" + name + "\" to \"" + value + "\"", x);
            return false;
        }
    }

    public void setUseInheritedChannel(boolean useInheritedChannel) {
        this.useInheritedChannel = useInheritedChannel;
    }

    public boolean getUseInheritedChannel() {
        return this.useInheritedChannel;
    }

    public void setPollerThreadPriority(int pollerThreadPriority) {
        this.pollerThreadPriority = pollerThreadPriority;
    }

    public int getPollerThreadPriority() {
        return this.pollerThreadPriority;
    }

    public void setPollerThreadCount(int pollerThreadCount) {
        this.pollerThreadCount = pollerThreadCount;
    }

    public int getPollerThreadCount() {
        return this.pollerThreadCount;
    }

    public void setSelectorTimeout(long timeout) {
        this.selectorTimeout = timeout;
    }

    public long getSelectorTimeout() {
        return this.selectorTimeout;
    }

    public Poller getPoller0() {
        int idx = Math.abs(this.pollerRotater.incrementAndGet()) % this.pollers.length;
        return this.pollers[idx];
    }

    public void setSelectorPool(NioSelectorPool selectorPool) {
        this.selectorPool = selectorPool;
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public boolean getDeferAccept() {
        return false;
    }

    public int getKeepAliveCount() {
        if (this.pollers == null) {
            return 0;
        }
        int sum = 0;
        for (int i = 0; i < this.pollers.length; i++) {
            sum += this.pollers[i].getKeyCount();
        }
        return sum;
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void bind() throws Exception {
        initServerSocket();
        if (this.acceptorThreadCount == 0) {
            this.acceptorThreadCount = 1;
        }
        if (this.pollerThreadCount <= 0) {
            this.pollerThreadCount = 1;
        }
        setStopLatch(new CountDownLatch(this.pollerThreadCount));
        initialiseSsl();
        this.selectorPool.open();
    }

    protected void initServerSocket() throws Exception {
        if (!getUseInheritedChannel()) {
            this.serverSock = ServerSocketChannel.open();
            this.socketProperties.setProperties(this.serverSock.socket());
            InetSocketAddress addr = getAddress() != null ? new InetSocketAddress(getAddress(), getPort()) : new InetSocketAddress(getPort());
            this.serverSock.socket().bind(addr, getAcceptCount());
        } else {
            Channel ic = System.inheritedChannel();
            if (ic instanceof ServerSocketChannel) {
                this.serverSock = (ServerSocketChannel) ic;
            }
            if (this.serverSock == null) {
                throw new IllegalArgumentException(sm.getString("endpoint.init.bind.inherited"));
            }
        }
        this.serverSock.configureBlocking(true);
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void startInternal() throws Exception {
        if (!this.running) {
            this.running = true;
            this.paused = false;
            this.processorCache = new SynchronizedStack<>(128, this.socketProperties.getProcessorCache());
            this.eventCache = new SynchronizedStack<>(128, this.socketProperties.getEventCache());
            this.nioChannels = new SynchronizedStack<>(128, this.socketProperties.getBufferPool());
            if (getExecutor() == null) {
                createExecutor();
            }
            initializeConnectionLatch();
            this.pollers = new Poller[getPollerThreadCount()];
            for (int i = 0; i < this.pollers.length; i++) {
                this.pollers[i] = new Poller();
                Thread pollerThread = new Thread(this.pollers[i], getName() + "-ClientPoller-" + i);
                pollerThread.setPriority(this.threadPriority);
                pollerThread.setDaemon(true);
                pollerThread.start();
            }
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
            for (int i = 0; this.pollers != null && i < this.pollers.length; i++) {
                if (this.pollers[i] != null) {
                    this.pollers[i].destroy();
                    this.pollers[i] = null;
                }
            }
            try {
                if (!getStopLatch().await(this.selectorTimeout + 100, TimeUnit.MILLISECONDS)) {
                    log.warn(sm.getString("endpoint.nio.stopLatchAwaitFail"));
                }
            } catch (InterruptedException e) {
                log.warn(sm.getString("endpoint.nio.stopLatchAwaitInterrupted"), e);
            }
            shutdownExecutor();
            this.eventCache.clear();
            this.nioChannels.clear();
            this.processorCache.clear();
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractJsseEndpoint, org.apache.tomcat.util.net.AbstractEndpoint
    public void unbind() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Destroy initiated for " + new InetSocketAddress(getAddress(), getPort()));
        }
        if (this.running) {
            stop();
        }
        doCloseServerSocket();
        destroySsl();
        super.unbind();
        if (getHandler() != null) {
            getHandler().recycle();
        }
        this.selectorPool.close();
        if (log.isDebugEnabled()) {
            log.debug("Destroy completed for " + new InetSocketAddress(getAddress(), getPort()));
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected void doCloseServerSocket() throws IOException {
        if (!getUseInheritedChannel() && this.serverSock != null) {
            this.serverSock.socket().close();
            this.serverSock.close();
        }
        this.serverSock = null;
    }

    public NioSelectorPool getSelectorPool() {
        return this.selectorPool;
    }

    protected CountDownLatch getStopLatch() {
        return this.stopLatch;
    }

    protected void setStopLatch(CountDownLatch stopLatch) {
        this.stopLatch = stopLatch;
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public boolean setSocketOptions(SocketChannel socket) {
        try {
            socket.configureBlocking(false);
            Socket sock = socket.socket();
            this.socketProperties.setProperties(sock);
            NioChannel channel = this.nioChannels.pop();
            if (channel == null) {
                SocketBufferHandler bufhandler = new SocketBufferHandler(this.socketProperties.getAppReadBufSize(), this.socketProperties.getAppWriteBufSize(), this.socketProperties.getDirectBuffer());
                if (isSSLEnabled()) {
                    channel = new SecureNioChannel(socket, bufhandler, this.selectorPool, this);
                } else {
                    channel = new NioChannel(socket, bufhandler);
                }
            } else {
                channel.setIOChannel(socket);
                channel.reset();
            }
            getPoller0().register(channel);
            return true;
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            try {
                log.error("", t);
                return false;
            } catch (Throwable tt) {
                ExceptionUtils.handleThrowable(tt);
                return false;
            }
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void closeSocket(SocketChannel socket) {
        countDownConnection();
        try {
            socket.socket().close();
        } catch (IOException ioe) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("endpoint.err.close"), ioe);
            }
        }
        try {
            socket.close();
        } catch (IOException ioe2) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("endpoint.err.close"), ioe2);
            }
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractJsseEndpoint
    protected NetworkChannel getServerSocket() {
        return this.serverSock;
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public SocketChannel serverSocketAccept() throws Exception {
        return this.serverSock.accept();
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected Log getLog() {
        return log;
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected SocketProcessorBase<NioChannel> createSocketProcessor(SocketWrapperBase<NioChannel> socketWrapper, SocketEvent event) {
        return new SocketProcessor(socketWrapper, event);
    }

    public void close(NioChannel socket, SelectionKey key) {
        try {
            if (socket.getPoller().cancelledKey(key) != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Socket: [" + socket + "] closed");
                }
                if (this.running && !this.paused && !this.nioChannels.push(socket)) {
                    socket.free();
                }
            }
        } catch (Exception x) {
            log.error("", x);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/NioEndpoint$PollerEvent.class */
    public static class PollerEvent implements Runnable {
        private NioChannel socket;
        private int interestOps;
        private NioSocketWrapper socketWrapper;

        public PollerEvent(NioChannel ch2, NioSocketWrapper w, int intOps) {
            reset(ch2, w, intOps);
        }

        public void reset(NioChannel ch2, NioSocketWrapper w, int intOps) {
            this.socket = ch2;
            this.interestOps = intOps;
            this.socketWrapper = w;
        }

        public void reset() {
            reset(null, null, 0);
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.interestOps == 256) {
                try {
                    this.socket.getIOChannel().register(this.socket.getPoller().getSelector(), 1, this.socketWrapper);
                    return;
                } catch (Exception x) {
                    NioEndpoint.log.error(AbstractEndpoint.sm.getString("endpoint.nio.registerFail"), x);
                    return;
                }
            }
            SelectionKey key = this.socket.getIOChannel().keyFor(this.socket.getPoller().getSelector());
            try {
                if (key == null) {
                    this.socket.socketWrapper.getEndpoint().countDownConnection();
                    ((NioSocketWrapper) this.socket.socketWrapper).closed = true;
                } else {
                    NioSocketWrapper socketWrapper = (NioSocketWrapper) key.attachment();
                    if (socketWrapper != null) {
                        int ops = key.interestOps() | this.interestOps;
                        socketWrapper.interestOps(ops);
                        key.interestOps(ops);
                    } else {
                        this.socket.getPoller().cancelledKey(key);
                    }
                }
            } catch (CancelledKeyException e) {
                try {
                    this.socket.getPoller().cancelledKey(key);
                } catch (Exception e2) {
                }
            }
        }

        public String toString() {
            return "Poller event: socket [" + this.socket + "], socketWrapper [" + this.socketWrapper + "], interestOps [" + this.interestOps + "]";
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/NioEndpoint$Poller.class */
    public class Poller implements Runnable {
        private final SynchronizedQueue<PollerEvent> events = new SynchronizedQueue<>();
        private volatile boolean close = false;
        private long nextExpiration = 0;
        private AtomicLong wakeupCounter = new AtomicLong(0);
        private volatile int keyCount = 0;
        private Selector selector = Selector.open();

        public Poller() throws IOException {
            NioEndpoint.this = this$0;
        }

        public int getKeyCount() {
            return this.keyCount;
        }

        public Selector getSelector() {
            return this.selector;
        }

        protected void destroy() {
            this.close = true;
            this.selector.wakeup();
        }

        private void addEvent(PollerEvent event) {
            this.events.offer(event);
            if (this.wakeupCounter.incrementAndGet() == 0) {
                this.selector.wakeup();
            }
        }

        public void add(NioChannel socket, int interestOps) {
            PollerEvent r = (PollerEvent) NioEndpoint.this.eventCache.pop();
            if (r == null) {
                r = new PollerEvent(socket, null, interestOps);
            } else {
                r.reset(socket, null, interestOps);
            }
            addEvent(r);
            if (this.close) {
                NioSocketWrapper ka = (NioSocketWrapper) socket.getAttachment();
                NioEndpoint.this.processSocket(ka, SocketEvent.STOP, false);
            }
        }

        public boolean events() {
            PollerEvent pe;
            boolean result = false;
            int size = this.events.size();
            for (int i = 0; i < size && (pe = this.events.poll()) != null; i++) {
                result = true;
                try {
                    pe.run();
                    pe.reset();
                    if (NioEndpoint.this.running && !NioEndpoint.this.paused) {
                        NioEndpoint.this.eventCache.push(pe);
                    }
                } catch (Throwable x) {
                    NioEndpoint.log.error("", x);
                }
            }
            return result;
        }

        public void register(NioChannel socket) {
            socket.setPoller(this);
            NioSocketWrapper ka = new NioSocketWrapper(socket, NioEndpoint.this);
            socket.setSocketWrapper(ka);
            ka.setPoller(this);
            ka.setReadTimeout(NioEndpoint.this.getConnectionTimeout());
            ka.setWriteTimeout(NioEndpoint.this.getConnectionTimeout());
            ka.setKeepAliveLeft(NioEndpoint.this.getMaxKeepAliveRequests());
            ka.setSecure(NioEndpoint.this.isSSLEnabled());
            PollerEvent r = (PollerEvent) NioEndpoint.this.eventCache.pop();
            ka.interestOps(1);
            if (r == null) {
                r = new PollerEvent(socket, ka, 256);
            } else {
                r.reset(socket, ka, 256);
            }
            addEvent(r);
        }

        public NioSocketWrapper cancelledKey(SelectionKey key) {
            NioSocketWrapper ka = null;
            if (key == null) {
                return null;
            }
            try {
                ka = (NioSocketWrapper) key.attach(null);
                if (ka != null) {
                    NioEndpoint.this.getHandler().release(ka);
                }
                if (key.isValid()) {
                    key.cancel();
                }
                if (ka != null) {
                    try {
                        ka.getSocket().close(true);
                    } catch (Exception e) {
                        if (NioEndpoint.log.isDebugEnabled()) {
                            NioEndpoint.log.debug(AbstractEndpoint.sm.getString("endpoint.debug.socketCloseFail"), e);
                        }
                    }
                }
                if (key.channel().isOpen()) {
                    try {
                        key.channel().close();
                    } catch (Exception e2) {
                        if (NioEndpoint.log.isDebugEnabled()) {
                            NioEndpoint.log.debug(AbstractEndpoint.sm.getString("endpoint.debug.channelCloseFail"), e2);
                        }
                    }
                }
                if (ka != null) {
                    try {
                        if (ka.getSendfileData() != null && ka.getSendfileData().fchannel != null && ka.getSendfileData().fchannel.isOpen()) {
                            ka.getSendfileData().fchannel.close();
                        }
                    } catch (Exception e3) {
                    }
                }
                if (ka != null) {
                    NioEndpoint.this.countDownConnection();
                    ka.closed = true;
                }
            } catch (Throwable e4) {
                ExceptionUtils.handleThrowable(e4);
                if (NioEndpoint.log.isDebugEnabled()) {
                    NioEndpoint.log.error("", e4);
                }
            }
            return ka;
        }

        @Override // java.lang.Runnable
        public void run() {
            while (true) {
                boolean hasEvents = false;
                try {
                    if (!this.close) {
                        hasEvents = events();
                        if (this.wakeupCounter.getAndSet(-1L) > 0) {
                            this.keyCount = this.selector.selectNow();
                        } else {
                            this.keyCount = this.selector.select(NioEndpoint.this.selectorTimeout);
                        }
                        this.wakeupCounter.set(0L);
                    }
                } catch (Throwable x) {
                    ExceptionUtils.handleThrowable(x);
                    NioEndpoint.log.error("", x);
                }
                if (this.close) {
                    events();
                    timeout(0, false);
                    try {
                        this.selector.close();
                        break;
                    } catch (IOException ioe) {
                        NioEndpoint.log.error(AbstractEndpoint.sm.getString("endpoint.nio.selectorCloseFail"), ioe);
                        break;
                    }
                }
                if (this.keyCount == 0) {
                    hasEvents |= events();
                }
                Iterator<SelectionKey> iterator = this.keyCount > 0 ? this.selector.selectedKeys().iterator() : null;
                while (iterator != null && iterator.hasNext()) {
                    SelectionKey sk = iterator.next();
                    NioSocketWrapper attachment = (NioSocketWrapper) sk.attachment();
                    if (attachment == null) {
                        iterator.remove();
                    } else {
                        iterator.remove();
                        processKey(sk, attachment);
                    }
                }
                timeout(this.keyCount, hasEvents);
            }
            NioEndpoint.this.getStopLatch().countDown();
        }

        protected void processKey(SelectionKey sk, NioSocketWrapper attachment) {
            try {
                if (this.close) {
                    cancelledKey(sk);
                } else if (sk.isValid() && attachment != null) {
                    if (sk.isReadable() || sk.isWritable()) {
                        if (attachment.getSendfileData() != null) {
                            processSendfile(sk, attachment, false);
                        } else {
                            unreg(sk, attachment, sk.readyOps());
                            boolean closeSocket = false;
                            if (sk.isReadable() && !NioEndpoint.this.processSocket(attachment, SocketEvent.OPEN_READ, true)) {
                                closeSocket = true;
                            }
                            if (!closeSocket && sk.isWritable() && !NioEndpoint.this.processSocket(attachment, SocketEvent.OPEN_WRITE, true)) {
                                closeSocket = true;
                            }
                            if (closeSocket) {
                                cancelledKey(sk);
                            }
                        }
                    }
                } else {
                    cancelledKey(sk);
                }
            } catch (CancelledKeyException e) {
                cancelledKey(sk);
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                NioEndpoint.log.error("", t);
            }
        }

        public SendfileState processSendfile(SelectionKey sk, NioSocketWrapper socketWrapper, boolean calledByProcessor) {
            try {
                unreg(sk, socketWrapper, sk.readyOps());
                SendfileData sd = socketWrapper.getSendfileData();
                if (NioEndpoint.log.isTraceEnabled()) {
                    NioEndpoint.log.trace("Processing send file for: " + sd.fileName);
                }
                if (sd.fchannel == null) {
                    File f = new File(sd.fileName);
                    FileInputStream fis = new FileInputStream(f);
                    sd.fchannel = fis.getChannel();
                }
                NioChannel sc = socketWrapper.getSocket();
                WritableByteChannel wc = sc instanceof SecureNioChannel ? sc : sc.getIOChannel();
                if (sc.getOutboundRemaining() > 0) {
                    if (sc.flushOutbound()) {
                        socketWrapper.updateLastWrite();
                    }
                } else {
                    long written = sd.fchannel.transferTo(sd.pos, sd.length, wc);
                    if (written > 0) {
                        sd.pos += written;
                        sd.length -= written;
                        socketWrapper.updateLastWrite();
                    } else if (sd.fchannel.size() <= sd.pos) {
                        throw new IOException("Sendfile configured to send more data than was available");
                    }
                }
                if (sd.length > 0 || sc.getOutboundRemaining() > 0) {
                    if (NioEndpoint.log.isDebugEnabled()) {
                        NioEndpoint.log.debug("OP_WRITE for sendfile: " + sd.fileName);
                    }
                    if (calledByProcessor) {
                        add(socketWrapper.getSocket(), 4);
                    } else {
                        reg(sk, socketWrapper, 4);
                    }
                    return SendfileState.PENDING;
                }
                if (NioEndpoint.log.isDebugEnabled()) {
                    NioEndpoint.log.debug("Send file complete for: " + sd.fileName);
                }
                socketWrapper.setSendfileData(null);
                try {
                    sd.fchannel.close();
                } catch (Exception e) {
                }
                if (!calledByProcessor) {
                    switch (sd.keepAliveState) {
                        case NONE:
                            if (NioEndpoint.log.isDebugEnabled()) {
                                NioEndpoint.log.debug("Send file connection is being closed");
                            }
                            NioEndpoint.this.close(sc, sk);
                            break;
                        case PIPELINED:
                            if (NioEndpoint.log.isDebugEnabled()) {
                                NioEndpoint.log.debug("Connection is keep alive, processing pipe-lined data");
                            }
                            if (!NioEndpoint.this.processSocket(socketWrapper, SocketEvent.OPEN_READ, true)) {
                                NioEndpoint.this.close(sc, sk);
                                break;
                            }
                            break;
                        case OPEN:
                            if (NioEndpoint.log.isDebugEnabled()) {
                                NioEndpoint.log.debug("Connection is keep alive, registering back for OP_READ");
                            }
                            reg(sk, socketWrapper, 1);
                            break;
                    }
                }
                return SendfileState.DONE;
            } catch (IOException x) {
                if (NioEndpoint.log.isDebugEnabled()) {
                    NioEndpoint.log.debug("Unable to complete sendfile request:", x);
                }
                if (!calledByProcessor && 0 != 0) {
                    NioEndpoint.this.close(null, sk);
                }
                return SendfileState.ERROR;
            } catch (Throwable t) {
                NioEndpoint.log.error("", t);
                if (!calledByProcessor && 0 != 0) {
                    NioEndpoint.this.close(null, sk);
                }
                return SendfileState.ERROR;
            }
        }

        protected void unreg(SelectionKey sk, NioSocketWrapper attachment, int readyOps) {
            reg(sk, attachment, sk.interestOps() & (readyOps ^ (-1)));
        }

        protected void reg(SelectionKey sk, NioSocketWrapper attachment, int intops) {
            sk.interestOps(intops);
            attachment.interestOps(intops);
        }

        protected void timeout(int keyCount, boolean hasEvents) {
            long now = System.currentTimeMillis();
            if (this.nextExpiration > 0 && ((keyCount > 0 || hasEvents) && now < this.nextExpiration && !this.close)) {
                return;
            }
            int keycount = 0;
            try {
                for (SelectionKey key : this.selector.keys()) {
                    keycount++;
                    try {
                        NioSocketWrapper ka = (NioSocketWrapper) key.attachment();
                        if (ka == null) {
                            cancelledKey(key);
                        } else if (this.close) {
                            key.interestOps(0);
                            ka.interestOps(0);
                            processKey(key, ka);
                        } else if ((ka.interestOps() & 1) == 1 || (ka.interestOps() & 4) == 4) {
                            boolean isTimedOut = false;
                            if ((ka.interestOps() & 1) == 1) {
                                long delta = now - ka.getLastRead();
                                long timeout = ka.getReadTimeout();
                                isTimedOut = timeout > 0 && delta > timeout;
                            }
                            if (!isTimedOut && (ka.interestOps() & 4) == 4) {
                                long delta2 = now - ka.getLastWrite();
                                long timeout2 = ka.getWriteTimeout();
                                isTimedOut = timeout2 > 0 && delta2 > timeout2;
                            }
                            if (isTimedOut) {
                                key.interestOps(0);
                                ka.interestOps(0);
                                ka.setError(new SocketTimeoutException());
                                if (!NioEndpoint.this.processSocket(ka, SocketEvent.ERROR, true)) {
                                    cancelledKey(key);
                                }
                            }
                        }
                    } catch (CancelledKeyException e) {
                        cancelledKey(key);
                    }
                }
            } catch (ConcurrentModificationException cme) {
                NioEndpoint.log.warn(AbstractEndpoint.sm.getString("endpoint.nio.timeoutCme"), cme);
            }
            long prevExp = this.nextExpiration;
            this.nextExpiration = System.currentTimeMillis() + NioEndpoint.this.socketProperties.getTimeoutInterval();
            if (NioEndpoint.log.isTraceEnabled()) {
                NioEndpoint.log.trace("timeout completed: keys processed=" + keycount + "; now=" + now + "; nextExpiration=" + prevExp + "; keyCount=" + keyCount + "; hasEvents=" + hasEvents + "; eval=" + (now < prevExp && (keyCount > 0 || hasEvents) && !this.close));
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/NioEndpoint$NioSocketWrapper.class */
    public static class NioSocketWrapper extends SocketWrapperBase<NioChannel> {
        private final NioSelectorPool pool;
        private Poller poller;
        private int interestOps;
        private CountDownLatch readLatch;
        private CountDownLatch writeLatch;
        private volatile SendfileData sendfileData;
        private volatile long lastRead;
        private volatile long lastWrite;
        private volatile boolean closed;

        public NioSocketWrapper(NioChannel channel, NioEndpoint endpoint) {
            super(channel, endpoint);
            this.poller = null;
            this.interestOps = 0;
            this.readLatch = null;
            this.writeLatch = null;
            this.sendfileData = null;
            this.lastRead = System.currentTimeMillis();
            this.lastWrite = this.lastRead;
            this.closed = false;
            this.pool = endpoint.getSelectorPool();
            this.socketBufferHandler = channel.getBufHandler();
        }

        public Poller getPoller() {
            return this.poller;
        }

        public void setPoller(Poller poller) {
            this.poller = poller;
        }

        public int interestOps() {
            return this.interestOps;
        }

        public int interestOps(int ops) {
            this.interestOps = ops;
            return ops;
        }

        public CountDownLatch getReadLatch() {
            return this.readLatch;
        }

        public CountDownLatch getWriteLatch() {
            return this.writeLatch;
        }

        protected CountDownLatch resetLatch(CountDownLatch latch) {
            if (latch == null || latch.getCount() == 0) {
                return null;
            }
            throw new IllegalStateException("Latch must be at count 0");
        }

        public void resetReadLatch() {
            this.readLatch = resetLatch(this.readLatch);
        }

        public void resetWriteLatch() {
            this.writeLatch = resetLatch(this.writeLatch);
        }

        protected CountDownLatch startLatch(CountDownLatch latch, int cnt) {
            if (latch == null || latch.getCount() == 0) {
                return new CountDownLatch(cnt);
            }
            throw new IllegalStateException("Latch must be at count 0 or null.");
        }

        public void startReadLatch(int cnt) {
            this.readLatch = startLatch(this.readLatch, cnt);
        }

        public void startWriteLatch(int cnt) {
            this.writeLatch = startLatch(this.writeLatch, cnt);
        }

        protected void awaitLatch(CountDownLatch latch, long timeout, TimeUnit unit) throws InterruptedException {
            if (latch == null) {
                throw new IllegalStateException("Latch cannot be null");
            }
            latch.await(timeout, unit);
        }

        public void awaitReadLatch(long timeout, TimeUnit unit) throws InterruptedException {
            awaitLatch(this.readLatch, timeout, unit);
        }

        public void awaitWriteLatch(long timeout, TimeUnit unit) throws InterruptedException {
            awaitLatch(this.writeLatch, timeout, unit);
        }

        public void setSendfileData(SendfileData sf) {
            this.sendfileData = sf;
        }

        public SendfileData getSendfileData() {
            return this.sendfileData;
        }

        public void updateLastWrite() {
            this.lastWrite = System.currentTimeMillis();
        }

        public long getLastWrite() {
            return this.lastWrite;
        }

        public void updateLastRead() {
            this.lastRead = System.currentTimeMillis();
        }

        public long getLastRead() {
            return this.lastRead;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean isReadyForRead() throws IOException {
            this.socketBufferHandler.configureReadBufferForRead();
            if (this.socketBufferHandler.getReadBuffer().remaining() > 0) {
                return true;
            }
            fillReadBuffer(false);
            boolean isReady = this.socketBufferHandler.getReadBuffer().position() > 0;
            return isReady;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public int read(boolean block, byte[] b, int off, int len) throws IOException {
            int nRead = populateReadBuffer(b, off, len);
            if (nRead > 0) {
                return nRead;
            }
            int nRead2 = fillReadBuffer(block);
            updateLastRead();
            if (nRead2 > 0) {
                this.socketBufferHandler.configureReadBufferForRead();
                nRead2 = Math.min(nRead2, len);
                this.socketBufferHandler.getReadBuffer().get(b, off, nRead2);
            }
            return nRead2;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public int read(boolean block, ByteBuffer to) throws IOException {
            int nRead;
            int nRead2 = populateReadBuffer(to);
            if (nRead2 > 0) {
                return nRead2;
            }
            int limit = this.socketBufferHandler.getReadBuffer().capacity();
            if (to.remaining() >= limit) {
                to.limit(to.position() + limit);
                nRead = fillReadBuffer(block, to);
                if (NioEndpoint.log.isDebugEnabled()) {
                    NioEndpoint.log.debug("Socket: [" + this + "], Read direct from socket: [" + nRead + "]");
                }
                updateLastRead();
            } else {
                nRead = fillReadBuffer(block);
                if (NioEndpoint.log.isDebugEnabled()) {
                    NioEndpoint.log.debug("Socket: [" + this + "], Read into buffer: [" + nRead + "]");
                }
                updateLastRead();
                if (nRead > 0) {
                    nRead = populateReadBuffer(to);
                }
            }
            return nRead;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void close() throws IOException {
            getSocket().close();
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean isClosed() {
            return this.closed || !getSocket().isOpen();
        }

        private int fillReadBuffer(boolean block) throws IOException {
            this.socketBufferHandler.configureReadBufferForWrite();
            return fillReadBuffer(block, this.socketBufferHandler.getReadBuffer());
        }

        /* JADX WARN: Finally extract failed */
        private int fillReadBuffer(boolean block, ByteBuffer to) throws IOException {
            int nRead;
            NioChannel channel = getSocket();
            if (block) {
                Selector selector = null;
                try {
                    selector = this.pool.get();
                } catch (IOException e) {
                }
                try {
                    NioSocketWrapper att = (NioSocketWrapper) channel.getAttachment();
                    if (att == null) {
                        throw new IOException("Key must be cancelled.");
                    }
                    nRead = this.pool.read(to, channel, selector, att.getReadTimeout());
                    if (selector != null) {
                        this.pool.put(selector);
                    }
                } catch (Throwable th) {
                    if (selector != null) {
                        this.pool.put(selector);
                    }
                    throw th;
                }
            } else {
                nRead = channel.read(to);
                if (nRead == -1) {
                    throw new EOFException();
                }
            }
            return nRead;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void doWrite(boolean block, ByteBuffer from) throws IOException {
            long writeTimeout = getWriteTimeout();
            Selector selector = null;
            try {
                selector = this.pool.get();
            } catch (IOException e) {
            }
            try {
                this.pool.write(from, getSocket(), selector, writeTimeout, block);
                if (block) {
                    do {
                    } while (!getSocket().flush(true, selector, writeTimeout));
                }
                updateLastWrite();
                if (selector != null) {
                    this.pool.put(selector);
                }
            } catch (Throwable th) {
                if (selector != null) {
                    this.pool.put(selector);
                }
                throw th;
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void registerReadInterest() {
            getPoller().add(getSocket(), 1);
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void registerWriteInterest() {
            getPoller().add(getSocket(), 4);
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public SendfileDataBase createSendfileData(String filename, long pos, long length) {
            return new SendfileData(filename, pos, length);
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public SendfileState processSendfile(SendfileDataBase sendfileData) {
            setSendfileData((SendfileData) sendfileData);
            SelectionKey key = getSocket().getIOChannel().keyFor(getSocket().getPoller().getSelector());
            return getSocket().getPoller().processSendfile(key, this, true);
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateRemoteAddr() {
            InetAddress inetAddr = getSocket().getIOChannel().socket().getInetAddress();
            if (inetAddr != null) {
                this.remoteAddr = inetAddr.getHostAddress();
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateRemoteHost() {
            InetAddress inetAddr = getSocket().getIOChannel().socket().getInetAddress();
            if (inetAddr != null) {
                this.remoteHost = inetAddr.getHostName();
                if (this.remoteAddr == null) {
                    this.remoteAddr = inetAddr.getHostAddress();
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateRemotePort() {
            this.remotePort = getSocket().getIOChannel().socket().getPort();
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateLocalName() {
            InetAddress inetAddr = getSocket().getIOChannel().socket().getLocalAddress();
            if (inetAddr != null) {
                this.localName = inetAddr.getHostName();
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateLocalAddr() {
            InetAddress inetAddr = getSocket().getIOChannel().socket().getLocalAddress();
            if (inetAddr != null) {
                this.localAddr = inetAddr.getHostAddress();
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateLocalPort() {
            this.localPort = getSocket().getIOChannel().socket().getLocalPort();
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public SSLSupport getSslSupport(String clientCertProvider) {
            if (getSocket() instanceof SecureNioChannel) {
                SecureNioChannel ch2 = (SecureNioChannel) getSocket();
                SSLSession session = ch2.getSslEngine().getSession();
                return ((NioEndpoint) getEndpoint()).getSslImplementation().getSSLSupport(session);
            }
            return null;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void doClientAuth(SSLSupport sslSupport) throws IOException {
            SecureNioChannel sslChannel = (SecureNioChannel) getSocket();
            SSLEngine engine = sslChannel.getSslEngine();
            if (!engine.getNeedClientAuth()) {
                engine.setNeedClientAuth(true);
                sslChannel.rehandshake(getEndpoint().getConnectionTimeout());
                ((JSSESupport) sslSupport).setSession(engine.getSession());
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void setAppReadBufHandler(ApplicationBufferHandler handler) {
            getSocket().setAppReadBufHandler(handler);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/NioEndpoint$SocketProcessor.class */
    protected class SocketProcessor extends SocketProcessorBase<NioChannel> {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SocketProcessor(SocketWrapperBase<NioChannel> socketWrapper, SocketEvent event) {
            super(socketWrapper, event);
            NioEndpoint.this = this$0;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.apache.tomcat.util.net.SocketProcessorBase
        protected void doRun() {
            NioChannel socket = (NioChannel) this.socketWrapper.getSocket();
            SelectionKey key = socket.getIOChannel().keyFor(socket.getPoller().getSelector());
            try {
                int handshake = -1;
                try {
                    try {
                        if (key != null) {
                            try {
                                if (socket.isHandshakeComplete()) {
                                    handshake = 0;
                                } else if (this.event == SocketEvent.STOP || this.event == SocketEvent.DISCONNECT || this.event == SocketEvent.ERROR) {
                                    handshake = -1;
                                } else {
                                    handshake = socket.handshake(key.isReadable(), key.isWritable());
                                    this.event = SocketEvent.OPEN_READ;
                                }
                            } catch (IOException x) {
                                handshake = -1;
                                if (NioEndpoint.log.isDebugEnabled()) {
                                    NioEndpoint.log.debug("Error during SSL handshake", x);
                                }
                            } catch (CancelledKeyException e) {
                                handshake = -1;
                            }
                        }
                        if (handshake == 0) {
                            AbstractEndpoint.Handler.SocketState socketState = AbstractEndpoint.Handler.SocketState.OPEN;
                            AbstractEndpoint.Handler.SocketState state = this.event == null ? NioEndpoint.this.getHandler().process(this.socketWrapper, SocketEvent.OPEN_READ) : NioEndpoint.this.getHandler().process(this.socketWrapper, this.event);
                            if (state == AbstractEndpoint.Handler.SocketState.CLOSED) {
                                NioEndpoint.this.close(socket, key);
                            }
                        } else if (handshake == -1) {
                            NioEndpoint.this.close(socket, key);
                        } else if (handshake == 1) {
                            this.socketWrapper.registerReadInterest();
                        } else if (handshake == 4) {
                            this.socketWrapper.registerWriteInterest();
                        }
                        this.socketWrapper = null;
                        this.event = null;
                        if (!NioEndpoint.this.running || NioEndpoint.this.paused) {
                            return;
                        }
                        NioEndpoint.this.processorCache.push(this);
                    } catch (CancelledKeyException e2) {
                        socket.getPoller().cancelledKey(key);
                        this.socketWrapper = null;
                        this.event = null;
                        if (!NioEndpoint.this.running || NioEndpoint.this.paused) {
                            return;
                        }
                        NioEndpoint.this.processorCache.push(this);
                    }
                } catch (VirtualMachineError vme) {
                    ExceptionUtils.handleThrowable(vme);
                    this.socketWrapper = null;
                    this.event = null;
                    if (!NioEndpoint.this.running || NioEndpoint.this.paused) {
                        return;
                    }
                    NioEndpoint.this.processorCache.push(this);
                } catch (Throwable t) {
                    NioEndpoint.log.error("", t);
                    socket.getPoller().cancelledKey(key);
                    this.socketWrapper = null;
                    this.event = null;
                    if (!NioEndpoint.this.running || NioEndpoint.this.paused) {
                        return;
                    }
                    NioEndpoint.this.processorCache.push(this);
                }
            } catch (Throwable th) {
                this.socketWrapper = null;
                this.event = null;
                if (NioEndpoint.this.running && !NioEndpoint.this.paused) {
                    NioEndpoint.this.processorCache.push(this);
                }
                throw th;
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/NioEndpoint$SendfileData.class */
    public static class SendfileData extends SendfileDataBase {
        protected volatile FileChannel fchannel;

        public SendfileData(String filename, long pos, long length) {
            super(filename, pos, length);
        }
    }
}