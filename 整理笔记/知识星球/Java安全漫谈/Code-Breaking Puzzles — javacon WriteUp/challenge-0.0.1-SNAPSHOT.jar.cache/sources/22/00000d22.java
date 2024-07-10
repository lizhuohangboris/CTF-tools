package org.apache.tomcat.util.net;

import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.Address;
import org.apache.tomcat.jni.Error;
import org.apache.tomcat.jni.Library;
import org.apache.tomcat.jni.OS;
import org.apache.tomcat.jni.Poll;
import org.apache.tomcat.jni.Pool;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.jni.SSLConf;
import org.apache.tomcat.jni.SSLContext;
import org.apache.tomcat.jni.SSLSocket;
import org.apache.tomcat.jni.Sockaddr;
import org.apache.tomcat.jni.Socket;
import org.apache.tomcat.jni.Status;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.Acceptor;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.openssl.OpenSSLConf;
import org.apache.tomcat.util.net.openssl.OpenSSLEngine;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/AprEndpoint.class */
public class AprEndpoint extends AbstractEndpoint<Long, Long> implements SSLContext.SNICallBack {
    private static final Log log = LogFactory.getLog(AprEndpoint.class);
    protected long rootPool = 0;
    protected volatile long serverSock = 0;
    protected long serverSockPool = 0;
    protected long sslContext = 0;
    private final Map<Long, AprSocketWrapper> connections = new ConcurrentHashMap();
    protected boolean deferAccept = true;
    private boolean ipv6v6only = false;
    protected int sendfileSize = 1024;
    protected int pollTime = 2000;
    private boolean useSendFileSet = false;
    protected Poller poller = null;
    protected Sendfile sendfile = null;

    public AprEndpoint() {
        setMaxConnections(8192);
    }

    public void setDeferAccept(boolean deferAccept) {
        this.deferAccept = deferAccept;
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public boolean getDeferAccept() {
        return this.deferAccept;
    }

    public void setIpv6v6only(boolean ipv6v6only) {
        this.ipv6v6only = ipv6v6only;
    }

    public boolean getIpv6v6only() {
        return this.ipv6v6only;
    }

    public void setSendfileSize(int sendfileSize) {
        this.sendfileSize = sendfileSize;
    }

    public int getSendfileSize() {
        return this.sendfileSize;
    }

    public int getPollTime() {
        return this.pollTime;
    }

    public void setPollTime(int pollTime) {
        if (pollTime > 0) {
            this.pollTime = pollTime;
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void setUseSendfile(boolean useSendfile) {
        this.useSendFileSet = true;
        super.setUseSendfile(useSendfile);
    }

    private void setUseSendfileInternal(boolean useSendfile) {
        super.setUseSendfile(useSendfile);
    }

    public Poller getPoller() {
        return this.poller;
    }

    public Sendfile getSendfile() {
        return this.sendfile;
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected SSLHostConfig.Type getSslConfigType() {
        return SSLHostConfig.Type.OPENSSL;
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public InetSocketAddress getLocalAddress() throws IOException {
        long s = this.serverSock;
        if (s == 0) {
            return null;
        }
        try {
            long sa = Address.get(0, s);
            Sockaddr addr = Address.getInfo(sa);
            if (addr.hostname == null) {
                if (addr.family == 2) {
                    return new InetSocketAddress("::", addr.port);
                }
                return new InetSocketAddress(Address.APR_ANYADDR, addr.port);
            }
            return new InetSocketAddress(addr.hostname, addr.port);
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void setMaxConnections(int maxConnections) {
        if (maxConnections == -1) {
            log.warn(sm.getString("endpoint.apr.maxConnections.unlimited", Integer.valueOf(getMaxConnections())));
        } else if (this.running) {
            log.warn(sm.getString("endpoint.apr.maxConnections.running", Integer.valueOf(getMaxConnections())));
        } else {
            super.setMaxConnections(maxConnections);
        }
    }

    public int getKeepAliveCount() {
        if (this.poller == null) {
            return 0;
        }
        return this.poller.getConnectionCount();
    }

    public int getSendfileCount() {
        if (this.sendfile == null) {
            return 0;
        }
        return this.sendfile.getSendfileCount();
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void bind() throws Exception {
        try {
            this.rootPool = Pool.create(0L);
            this.serverSockPool = Pool.create(this.rootPool);
            String addressStr = null;
            if (getAddress() != null) {
                addressStr = getAddress().getHostAddress();
            }
            int family = 1;
            if (Library.APR_HAVE_IPV6) {
                if (addressStr == null) {
                    if (!OS.IS_BSD) {
                        family = 0;
                    }
                } else if (addressStr.indexOf(58) >= 0) {
                    family = 0;
                }
            }
            long inetAddress = Address.info(addressStr, family, getPort(), 0, this.rootPool);
            this.serverSock = Socket.create(Address.getInfo(inetAddress).family, 0, 6, this.rootPool);
            if (OS.IS_UNIX) {
                Socket.optSet(this.serverSock, 16, 1);
            }
            if (Library.APR_HAVE_IPV6) {
                if (getIpv6v6only()) {
                    Socket.optSet(this.serverSock, 16384, 1);
                } else {
                    Socket.optSet(this.serverSock, 16384, 0);
                }
            }
            Socket.optSet(this.serverSock, 2, 1);
            int ret = Socket.bind(this.serverSock, inetAddress);
            if (ret != 0) {
                throw new Exception(sm.getString("endpoint.init.bind", "" + ret, Error.strerror(ret)));
            }
            int ret2 = Socket.listen(this.serverSock, getAcceptCount());
            if (ret2 != 0) {
                throw new Exception(sm.getString("endpoint.init.listen", "" + ret2, Error.strerror(ret2)));
            }
            if (OS.IS_WIN32 || OS.IS_WIN64) {
                Socket.optSet(this.serverSock, 16, 1);
            }
            if (!this.useSendFileSet) {
                setUseSendfileInternal(Library.APR_HAS_SENDFILE);
            } else if (getUseSendfile() && !Library.APR_HAS_SENDFILE) {
                setUseSendfileInternal(false);
            }
            if (this.acceptorThreadCount == 0) {
                this.acceptorThreadCount = 1;
            }
            if (this.deferAccept && Socket.optSet(this.serverSock, 32768, 1) == 70023) {
                this.deferAccept = false;
            }
            if (isSSLEnabled()) {
                for (SSLHostConfig sslHostConfig : this.sslHostConfigs.values()) {
                    sslHostConfig.setConfigType(getSslConfigType());
                    createSSLContext(sslHostConfig);
                }
                SSLHostConfig defaultSSLHostConfig = this.sslHostConfigs.get(getDefaultSSLHostConfigName());
                if (defaultSSLHostConfig == null) {
                    throw new IllegalArgumentException(sm.getString("endpoint.noSslHostConfig", getDefaultSSLHostConfigName(), getName()));
                }
                Long defaultSSLContext = defaultSSLHostConfig.getOpenSslContext();
                this.sslContext = defaultSSLContext.longValue();
                org.apache.tomcat.jni.SSLContext.registerDefault(defaultSSLContext, this);
            }
        } catch (UnsatisfiedLinkError e) {
            throw new Exception(sm.getString("endpoint.init.notavail"));
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected void createSSLContext(SSLHostConfig sslHostConfig) throws Exception {
        String[] enabledProtocols;
        long cctx;
        Set<SSLHostConfigCertificate> certificates = sslHostConfig.getCertificates(true);
        boolean firstCertificate = true;
        for (SSLHostConfigCertificate certificate : certificates) {
            if (SSLHostConfig.adjustRelativePath(certificate.getCertificateFile()) == null) {
                throw new Exception(sm.getString("endpoint.apr.noSslCertFile"));
            }
            if (firstCertificate) {
                firstCertificate = false;
                List<String> enabledProtocols2 = SSLUtilBase.getEnabled("protocols", log, true, sslHostConfig.getProtocols(), OpenSSLEngine.IMPLEMENTED_PROTOCOLS_SET);
                sslHostConfig.setEnabledProtocols((String[]) enabledProtocols2.toArray(new String[enabledProtocols2.size()]));
                List<String> enabledCiphers = SSLUtilBase.getEnabled("ciphers", log, false, sslHostConfig.getJsseCipherNames(), OpenSSLEngine.AVAILABLE_CIPHER_SUITES);
                sslHostConfig.setEnabledCiphers((String[]) enabledCiphers.toArray(new String[enabledCiphers.size()]));
            }
        }
        if (certificates.size() > 2) {
            throw new Exception(sm.getString("endpoint.apr.tooManyCertFiles"));
        }
        int value = 0;
        if (sslHostConfig.getProtocols().size() == 0) {
            value = 28;
        } else {
            for (String protocol : sslHostConfig.getEnabledProtocols()) {
                if (!Constants.SSL_PROTO_SSLv2Hello.equalsIgnoreCase(protocol)) {
                    if (Constants.SSL_PROTO_SSLv2.equalsIgnoreCase(protocol)) {
                        value |= 1;
                    } else if (Constants.SSL_PROTO_SSLv3.equalsIgnoreCase(protocol)) {
                        value |= 2;
                    } else if (Constants.SSL_PROTO_TLSv1.equalsIgnoreCase(protocol)) {
                        value |= 4;
                    } else if (Constants.SSL_PROTO_TLSv1_1.equalsIgnoreCase(protocol)) {
                        value |= 8;
                    } else if (Constants.SSL_PROTO_TLSv1_2.equalsIgnoreCase(protocol)) {
                        value |= 16;
                    } else {
                        throw new Exception(sm.getString("endpoint.apr.invalidSslProtocol", protocol));
                    }
                }
            }
        }
        try {
            long ctx = org.apache.tomcat.jni.SSLContext.make(this.rootPool, value, 1);
            if (sslHostConfig.getInsecureRenegotiation()) {
                org.apache.tomcat.jni.SSLContext.setOptions(ctx, 262144);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions(ctx, 262144);
            }
            if (sslHostConfig.getHonorCipherOrder()) {
                org.apache.tomcat.jni.SSLContext.setOptions(ctx, 4194304);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions(ctx, 4194304);
            }
            if (sslHostConfig.getDisableCompression()) {
                org.apache.tomcat.jni.SSLContext.setOptions(ctx, 131072);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions(ctx, 131072);
            }
            if (sslHostConfig.getDisableSessionTickets()) {
                org.apache.tomcat.jni.SSLContext.setOptions(ctx, 16384);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions(ctx, 16384);
            }
            org.apache.tomcat.jni.SSLContext.setCipherSuite(ctx, sslHostConfig.getCiphers());
            int idx = 0;
            for (SSLHostConfigCertificate certificate2 : sslHostConfig.getCertificates(true)) {
                int i = idx;
                idx++;
                org.apache.tomcat.jni.SSLContext.setCertificate(ctx, SSLHostConfig.adjustRelativePath(certificate2.getCertificateFile()), SSLHostConfig.adjustRelativePath(certificate2.getCertificateKeyFile()), certificate2.getCertificateKeyPassword(), i);
                org.apache.tomcat.jni.SSLContext.setCertificateChainFile(ctx, SSLHostConfig.adjustRelativePath(certificate2.getCertificateChainFile()), false);
            }
            org.apache.tomcat.jni.SSLContext.setCACertificate(ctx, SSLHostConfig.adjustRelativePath(sslHostConfig.getCaCertificateFile()), SSLHostConfig.adjustRelativePath(sslHostConfig.getCaCertificatePath()));
            org.apache.tomcat.jni.SSLContext.setCARevocation(ctx, SSLHostConfig.adjustRelativePath(sslHostConfig.getCertificateRevocationListFile()), SSLHostConfig.adjustRelativePath(sslHostConfig.getCertificateRevocationListPath()));
            switch (sslHostConfig.getCertificateVerification()) {
                case NONE:
                    value = 0;
                    break;
                case OPTIONAL:
                    value = 1;
                    break;
                case OPTIONAL_NO_CA:
                    value = 3;
                    break;
                case REQUIRED:
                    value = 2;
                    break;
            }
            org.apache.tomcat.jni.SSLContext.setVerify(ctx, value, sslHostConfig.getCertificateVerificationDepth());
            if (getUseSendfile()) {
                setUseSendfileInternal(false);
                if (this.useSendFileSet) {
                    log.warn(sm.getString("endpoint.apr.noSendfileWithSSL"));
                }
            }
            if (this.negotiableProtocols.size() > 0) {
                List<String> protocols = new ArrayList<>();
                protocols.addAll(this.negotiableProtocols);
                protocols.add("http/1.1");
                String[] protocolsArray = (String[]) protocols.toArray(new String[0]);
                org.apache.tomcat.jni.SSLContext.setAlpnProtos(ctx, protocolsArray, 0);
            }
            org.apache.tomcat.jni.SSLContext.setSessionIdContext(ctx, org.apache.tomcat.jni.SSLContext.DEFAULT_SESSION_ID_CONTEXT);
            OpenSSLConf openSslConf = sslHostConfig.getOpenSslConf();
            if (openSslConf != null) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("endpoint.apr.makeConf"));
                    }
                    cctx = SSLConf.make(this.rootPool, 58);
                    if (cctx != 0) {
                        if (log.isDebugEnabled()) {
                            log.debug(sm.getString("endpoint.apr.checkConf"));
                        }
                        try {
                            if (!openSslConf.check(cctx)) {
                                log.error(sm.getString("endpoint.apr.errCheckConf"));
                                throw new Exception(sm.getString("endpoint.apr.errCheckConf"));
                            }
                            if (log.isDebugEnabled()) {
                                log.debug(sm.getString("endpoint.apr.applyConf"));
                            }
                            try {
                                if (!openSslConf.apply(cctx, ctx)) {
                                    log.error(sm.getString("endpoint.apr.errApplyConf"));
                                    throw new Exception(sm.getString("endpoint.apr.errApplyConf"));
                                }
                                int opts = org.apache.tomcat.jni.SSLContext.getOptions(ctx);
                                List<String> enabled = new ArrayList<>();
                                enabled.add(Constants.SSL_PROTO_SSLv2Hello);
                                if ((opts & SSL.SSL_OP_NO_TLSv1) == 0) {
                                    enabled.add(Constants.SSL_PROTO_TLSv1);
                                }
                                if ((opts & 268435456) == 0) {
                                    enabled.add(Constants.SSL_PROTO_TLSv1_1);
                                }
                                if ((opts & 134217728) == 0) {
                                    enabled.add(Constants.SSL_PROTO_TLSv1_2);
                                }
                                if ((opts & 16777216) == 0) {
                                    enabled.add(Constants.SSL_PROTO_SSLv2);
                                }
                                if ((opts & 33554432) == 0) {
                                    enabled.add(Constants.SSL_PROTO_SSLv3);
                                }
                                sslHostConfig.setEnabledProtocols((String[]) enabled.toArray(new String[enabled.size()]));
                                sslHostConfig.setEnabledCiphers(org.apache.tomcat.jni.SSLContext.getCiphers(ctx));
                            } catch (Exception e) {
                                throw new Exception(sm.getString("endpoint.apr.errApplyConf"), e);
                            }
                        } catch (Exception e2) {
                            throw new Exception(sm.getString("endpoint.apr.errCheckConf"), e2);
                        }
                    }
                } catch (Exception e3) {
                    throw new Exception(sm.getString("endpoint.apr.errMakeConf"), e3);
                }
            } else {
                cctx = 0;
            }
            sslHostConfig.setOpenSslConfContext(Long.valueOf(cctx));
            sslHostConfig.setOpenSslContext(Long.valueOf(ctx));
        } catch (Exception e4) {
            throw new Exception(sm.getString("endpoint.apr.failSslContextMake"), e4);
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected void releaseSSLContext(SSLHostConfig sslHostConfig) {
        Long ctx = sslHostConfig.getOpenSslContext();
        if (ctx != null && ctx.longValue() != 0) {
            org.apache.tomcat.jni.SSLContext.free(ctx.longValue());
            sslHostConfig.setOpenSslContext(null);
        }
        Long cctx = sslHostConfig.getOpenSslConfContext();
        if (cctx != null && cctx.longValue() != 0) {
            SSLConf.free(cctx.longValue());
            sslHostConfig.setOpenSslConfContext(null);
        }
    }

    @Override // org.apache.tomcat.jni.SSLContext.SNICallBack
    public long getSslContext(String sniHostName) {
        SSLHostConfig sslHostConfig = getSSLHostConfig(sniHostName);
        Long ctx = sslHostConfig.getOpenSslContext();
        if (ctx != null) {
            return ctx.longValue();
        }
        return 0L;
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public boolean isAlpnSupported() {
        return isSSLEnabled();
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void startInternal() throws Exception {
        if (!this.running) {
            this.running = true;
            this.paused = false;
            this.processorCache = new SynchronizedStack<>(128, this.socketProperties.getProcessorCache());
            if (getExecutor() == null) {
                createExecutor();
            }
            initializeConnectionLatch();
            this.poller = new Poller();
            this.poller.init();
            this.poller.start();
            if (getUseSendfile()) {
                this.sendfile = new Sendfile();
                this.sendfile.init();
                this.sendfile.start();
            }
            startAcceptorThreads();
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void stopInternal() {
        long waitLeft;
        if (!this.paused) {
            pause();
        }
        if (this.running) {
            this.running = false;
            this.poller.stop();
            for (SocketWrapperBase<Long> socketWrapper : this.connections.values()) {
                try {
                    socketWrapper.close();
                } catch (IOException e) {
                }
            }
            Iterator it = this.acceptors.iterator();
            while (it.hasNext()) {
                Acceptor<Long> acceptor = (Acceptor) it.next();
                long j = AbstractComponentTracker.LINGERING_TIMEOUT;
                while (true) {
                    waitLeft = j;
                    if (waitLeft <= 0 || acceptor.getState() == Acceptor.AcceptorState.ENDED || this.serverSock == 0) {
                        break;
                    }
                    try {
                        Thread.sleep(50L);
                    } catch (InterruptedException e2) {
                    }
                    j = waitLeft - 50;
                }
                if (waitLeft == 0) {
                    log.warn(sm.getString("endpoint.warn.unlockAcceptorFailed", acceptor.getThreadName()));
                    if (this.serverSock != 0) {
                        Socket.shutdown(this.serverSock, 0);
                        this.serverSock = 0L;
                    }
                }
            }
            try {
                this.poller.destroy();
            } catch (Exception e3) {
            }
            this.poller = null;
            this.connections.clear();
            if (getUseSendfile()) {
                try {
                    this.sendfile.stop();
                    this.sendfile.destroy();
                } catch (Exception e4) {
                }
                this.sendfile = null;
            }
            this.processorCache.clear();
        }
        shutdownExecutor();
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void unbind() throws Exception {
        if (this.running) {
            stop();
        }
        if (this.serverSockPool != 0) {
            Pool.destroy(this.serverSockPool);
            this.serverSockPool = 0L;
        }
        doCloseServerSocket();
        if (this.sslContext != 0) {
            Long ctx = Long.valueOf(this.sslContext);
            org.apache.tomcat.jni.SSLContext.unregisterDefault(ctx);
            for (SSLHostConfig sslHostConfig : this.sslHostConfigs.values()) {
                sslHostConfig.setOpenSslContext(null);
            }
            this.sslContext = 0L;
        }
        if (this.rootPool != 0) {
            Pool.destroy(this.rootPool);
            this.rootPool = 0L;
        }
        getHandler().recycle();
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected void doCloseServerSocket() {
        if (this.serverSock != 0) {
            Socket.close(this.serverSock);
            this.serverSock = 0L;
        }
    }

    protected boolean setSocketOptions(SocketWrapperBase<Long> socketWrapper) {
        long socket = socketWrapper.getSocket().longValue();
        try {
            if (this.socketProperties.getSoLingerOn() && this.socketProperties.getSoLingerTime() >= 0) {
                Socket.optSet(socket, 1, this.socketProperties.getSoLingerTime());
            }
            if (this.socketProperties.getTcpNoDelay()) {
                Socket.optSet(socket, 512, this.socketProperties.getTcpNoDelay() ? 1 : 0);
            }
            Socket.timeoutSet(socket, this.socketProperties.getSoTimeout() * 1000);
            if (this.sslContext != 0) {
                SSLSocket.attach(this.sslContext, socket);
                if (SSLSocket.handshake(socket) != 0) {
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("endpoint.err.handshake") + ": " + SSL.getLastError());
                        return false;
                    }
                    return false;
                } else if (this.negotiableProtocols.size() > 0) {
                    byte[] negotiated = new byte[256];
                    int len = SSLSocket.getALPN(socket, negotiated);
                    String negotiatedProtocol = new String(negotiated, 0, len, StandardCharsets.UTF_8);
                    if (negotiatedProtocol.length() > 0) {
                        socketWrapper.setNegotiatedProtocol(negotiatedProtocol);
                        if (log.isDebugEnabled()) {
                            log.debug(sm.getString("endpoint.alpn.negotiated", negotiatedProtocol));
                        }
                    }
                }
            }
            return true;
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            if (log.isDebugEnabled()) {
                if (1 == 2) {
                    log.debug(sm.getString("endpoint.err.handshake"), t);
                    return false;
                }
                log.debug(sm.getString("endpoint.err.unexpected"), t);
                return false;
            }
            return false;
        }
    }

    protected long allocatePoller(int size, long pool, int timeout) {
        try {
            return Poll.create(size, pool, 0, timeout * 1000);
        } catch (Error e) {
            if (Status.APR_STATUS_IS_EINVAL(e.getError())) {
                log.info(sm.getString("endpoint.poll.limitedpollsize", "" + size));
                return 0L;
            }
            log.error(sm.getString("endpoint.poll.initfail"), e);
            return -1L;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public boolean setSocketOptions(Long socket) {
        try {
            if (this.running) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("endpoint.debug.socket", socket));
                }
                AprSocketWrapper wrapper = new AprSocketWrapper(socket, this);
                wrapper.setKeepAliveLeft(getMaxKeepAliveRequests());
                wrapper.setSecure(isSSLEnabled());
                wrapper.setReadTimeout(getConnectionTimeout());
                wrapper.setWriteTimeout(getConnectionTimeout());
                this.connections.put(socket, wrapper);
                getExecutor().execute(new SocketWithOptionsProcessor(wrapper));
            }
            return true;
        } catch (RejectedExecutionException x) {
            log.warn("Socket processing request was rejected for:" + socket, x);
            return false;
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log.error(sm.getString("endpoint.process.fail"), t);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public Long serverSocketAccept() throws Exception {
        long socket = Socket.accept(this.serverSock);
        if (log.isDebugEnabled()) {
            long sa = Address.get(1, socket);
            Sockaddr addr = Address.getInfo(sa);
            log.debug(sm.getString("endpoint.apr.remoteport", Long.valueOf(socket), Long.valueOf(addr.port)));
        }
        return Long.valueOf(socket);
    }

    protected boolean processSocket(long socket, SocketEvent event) {
        return processSocket(this.connections.get(Long.valueOf(socket)), event, true);
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected SocketProcessorBase<Long> createSocketProcessor(SocketWrapperBase<Long> socketWrapper, SocketEvent event) {
        return new SocketProcessor(socketWrapper, event);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void closeSocket(Long socket) {
        closeSocket(socket.longValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeSocket(long socket) {
        SocketWrapperBase<Long> wrapper = this.connections.remove(Long.valueOf(socket));
        if (wrapper != null) {
            ((AprSocketWrapper) wrapper).close();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void destroySocket(Long socket) {
        destroySocket(socket.longValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void destroySocket(long socket) {
        this.connections.remove(Long.valueOf(socket));
        if (log.isDebugEnabled()) {
            String msg = sm.getString("endpoint.debug.destroySocket", Long.valueOf(socket));
            if (log.isTraceEnabled()) {
                log.trace(msg, new Exception());
            } else {
                log.debug(msg);
            }
        }
        if (socket != 0) {
            Socket.destroy(socket);
            countDownConnection();
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected Log getLog() {
        return log;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/AprEndpoint$SocketInfo.class */
    public static class SocketInfo {
        public long socket;
        public long timeout;
        public int flags;

        public boolean read() {
            return (this.flags & 1) == 1;
        }

        public boolean write() {
            return (this.flags & 4) == 4;
        }

        public static int merge(int flag1, int flag2) {
            return (flag1 & 1) | (flag2 & 1) | (flag1 & 4) | (flag2 & 4);
        }

        public String toString() {
            return "Socket: [" + this.socket + "], timeout: [" + this.timeout + "], flags: [" + this.flags;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/AprEndpoint$SocketTimeouts.class */
    public static class SocketTimeouts {
        protected long[] sockets;
        protected long[] timeouts;
        protected int pos = 0;
        protected int size = 0;

        public SocketTimeouts(int size) {
            this.sockets = new long[size];
            this.timeouts = new long[size];
        }

        public void add(long socket, long timeout) {
            this.sockets[this.size] = socket;
            this.timeouts[this.size] = timeout;
            this.size++;
        }

        public long remove(long socket) {
            long result = 0;
            int i = 0;
            while (true) {
                if (i >= this.size) {
                    break;
                } else if (this.sockets[i] != socket) {
                    i++;
                } else {
                    result = this.timeouts[i];
                    this.sockets[i] = this.sockets[this.size - 1];
                    this.timeouts[i] = this.timeouts[this.size - 1];
                    this.size--;
                    break;
                }
            }
            return result;
        }

        public long check(long date) {
            while (this.pos < this.size) {
                if (date >= this.timeouts[this.pos]) {
                    long result = this.sockets[this.pos];
                    this.sockets[this.pos] = this.sockets[this.size - 1];
                    this.timeouts[this.pos] = this.timeouts[this.size - 1];
                    this.size--;
                    return result;
                }
                this.pos++;
            }
            this.pos = 0;
            return 0L;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/AprEndpoint$SocketList.class */
    public static class SocketList {
        protected long[] sockets;
        protected long[] timeouts;
        protected int[] flags;
        protected SocketInfo info = new SocketInfo();
        protected volatile int size = 0;
        protected int pos = 0;

        public SocketList(int size) {
            this.sockets = new long[size];
            this.timeouts = new long[size];
            this.flags = new int[size];
        }

        public int size() {
            return this.size;
        }

        public SocketInfo get() {
            if (this.pos == this.size) {
                return null;
            }
            this.info.socket = this.sockets[this.pos];
            this.info.timeout = this.timeouts[this.pos];
            this.info.flags = this.flags[this.pos];
            this.pos++;
            return this.info;
        }

        public void clear() {
            this.size = 0;
            this.pos = 0;
        }

        public boolean add(long socket, long timeout, int flag) {
            if (this.size == this.sockets.length) {
                return false;
            }
            for (int i = 0; i < this.size; i++) {
                if (this.sockets[i] == socket) {
                    this.flags[i] = SocketInfo.merge(this.flags[i], flag);
                    return true;
                }
            }
            this.sockets[this.size] = socket;
            this.timeouts[this.size] = timeout;
            this.flags[this.size] = flag;
            this.size++;
            return true;
        }

        public boolean remove(long socket) {
            for (int i = 0; i < this.size; i++) {
                if (this.sockets[i] == socket) {
                    this.sockets[i] = this.sockets[this.size - 1];
                    this.timeouts[i] = this.timeouts[this.size - 1];
                    this.flags[this.size] = this.flags[this.size - 1];
                    this.size--;
                    return true;
                }
            }
            return false;
        }

        public void duplicate(SocketList copy) {
            copy.size = this.size;
            copy.pos = this.pos;
            System.arraycopy(this.sockets, 0, copy.sockets, 0, this.size);
            System.arraycopy(this.timeouts, 0, copy.timeouts, 0, this.size);
            System.arraycopy(this.flags, 0, copy.flags, 0, this.size);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/AprEndpoint$Poller.class */
    public class Poller implements Runnable {
        private int pollerCount;
        private int pollerTime;
        private int nextPollerTime;
        private long[] desc;
        private volatile Thread pollerThread;
        private long[] pollers = null;
        private int actualPollerSize = 0;
        private int[] pollerSpace = null;
        private long pool = 0;
        private SocketList addList = null;
        private SocketList closeList = null;
        private SocketTimeouts timeouts = null;
        private long lastMaintain = System.currentTimeMillis();
        private AtomicInteger connectionCount = new AtomicInteger(0);
        private volatile boolean pollerRunning = true;

        public Poller() {
        }

        public int getConnectionCount() {
            return this.connectionCount.get();
        }

        protected synchronized void init() {
            this.pool = Pool.create(AprEndpoint.this.serverSockPool);
            int defaultPollerSize = AprEndpoint.this.getMaxConnections();
            if ((OS.IS_WIN32 || OS.IS_WIN64) && defaultPollerSize > 1024) {
                this.actualPollerSize = 1024;
            } else {
                this.actualPollerSize = defaultPollerSize;
            }
            this.timeouts = new SocketTimeouts(defaultPollerSize);
            long pollset = AprEndpoint.this.allocatePoller(this.actualPollerSize, this.pool, -1);
            if (pollset == 0 && this.actualPollerSize > 1024) {
                this.actualPollerSize = 1024;
                pollset = AprEndpoint.this.allocatePoller(this.actualPollerSize, this.pool, -1);
            }
            if (pollset == 0) {
                this.actualPollerSize = 62;
                pollset = AprEndpoint.this.allocatePoller(this.actualPollerSize, this.pool, -1);
            }
            this.pollerCount = defaultPollerSize / this.actualPollerSize;
            this.pollerTime = AprEndpoint.this.pollTime / this.pollerCount;
            this.nextPollerTime = this.pollerTime;
            this.pollers = new long[this.pollerCount];
            this.pollers[0] = pollset;
            for (int i = 1; i < this.pollerCount; i++) {
                this.pollers[i] = AprEndpoint.this.allocatePoller(this.actualPollerSize, this.pool, -1);
            }
            this.pollerSpace = new int[this.pollerCount];
            for (int i2 = 0; i2 < this.pollerCount; i2++) {
                this.pollerSpace[i2] = this.actualPollerSize;
            }
            this.desc = new long[this.actualPollerSize * 4];
            this.connectionCount.set(0);
            this.addList = new SocketList(defaultPollerSize);
            this.closeList = new SocketList(defaultPollerSize);
        }

        protected void start() {
            this.pollerThread = new Thread(AprEndpoint.this.poller, AprEndpoint.this.getName() + "-Poller");
            this.pollerThread.setPriority(AprEndpoint.this.threadPriority);
            this.pollerThread.setDaemon(true);
            this.pollerThread.start();
        }

        protected synchronized void stop() {
            this.pollerRunning = false;
            notify();
        }

        protected synchronized void destroy() {
            for (int loops = this.pollerCount * 50; loops > 0 && this.pollerThread.isAlive(); loops--) {
                try {
                    wait(AprEndpoint.this.pollTime / 1000);
                } catch (InterruptedException e) {
                }
            }
            if (this.pollerThread.isAlive()) {
                AprEndpoint.log.warn(AbstractEndpoint.sm.getString("endpoint.pollerThreadStop"));
            }
            SocketInfo socketInfo = this.closeList.get();
            while (true) {
                SocketInfo info = socketInfo;
                if (info == null) {
                    break;
                }
                this.addList.remove(info.socket);
                removeFromPoller(info.socket);
                AprEndpoint.this.destroySocket(info.socket);
                socketInfo = this.closeList.get();
            }
            this.closeList.clear();
            SocketInfo socketInfo2 = this.addList.get();
            while (true) {
                SocketInfo info2 = socketInfo2;
                if (info2 == null) {
                    break;
                }
                removeFromPoller(info2.socket);
                AprEndpoint.this.destroySocket(info2.socket);
                socketInfo2 = this.addList.get();
            }
            this.addList.clear();
            for (int i = 0; i < this.pollerCount; i++) {
                int rv = Poll.pollset(this.pollers[i], this.desc);
                if (rv > 0) {
                    for (int n = 0; n < rv; n++) {
                        AprEndpoint.this.destroySocket(this.desc[(n * 2) + 1]);
                    }
                }
            }
            Pool.destroy(this.pool);
            this.connectionCount.set(0);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void add(long socket, long timeout, int flags) {
            if (AprEndpoint.log.isDebugEnabled()) {
                String msg = AbstractEndpoint.sm.getString("endpoint.debug.pollerAdd", Long.valueOf(socket), Long.valueOf(timeout), Integer.valueOf(flags));
                if (AprEndpoint.log.isTraceEnabled()) {
                    AprEndpoint.log.trace(msg, new Exception());
                } else {
                    AprEndpoint.log.debug(msg);
                }
            }
            if (timeout <= 0) {
                timeout = 2147483647L;
            }
            synchronized (this) {
                if (this.addList.add(socket, timeout, flags)) {
                    notify();
                }
            }
        }

        private boolean addToPoller(long socket, int events) {
            for (int i = 0; i < this.pollers.length; i++) {
                if (this.pollerSpace[i] > 0) {
                    int rv = Poll.add(this.pollers[i], socket, events);
                    if (rv == 0) {
                        int[] iArr = this.pollerSpace;
                        int i2 = i;
                        iArr[i2] = iArr[i2] - 1;
                        this.connectionCount.incrementAndGet();
                        return true;
                    }
                }
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void close(long socket) {
            this.closeList.add(socket, 0L, 0);
            notify();
        }

        private void removeFromPoller(long socket) {
            if (AprEndpoint.log.isDebugEnabled()) {
                AprEndpoint.log.debug(AbstractEndpoint.sm.getString("endpoint.debug.pollerRemove", Long.valueOf(socket)));
            }
            int i = 0;
            while (true) {
                if (i >= this.pollers.length) {
                    break;
                }
                if (this.pollerSpace[i] < this.actualPollerSize) {
                    int rv = Poll.remove(this.pollers[i], socket);
                    if (rv != 70015) {
                        int[] iArr = this.pollerSpace;
                        int i2 = i;
                        iArr[i2] = iArr[i2] + 1;
                        this.connectionCount.decrementAndGet();
                        if (AprEndpoint.log.isDebugEnabled()) {
                            AprEndpoint.log.debug(AbstractEndpoint.sm.getString("endpoint.debug.pollerRemoved", Long.valueOf(socket)));
                        }
                    }
                }
                i++;
            }
            this.timeouts.remove(socket);
        }

        private synchronized void maintain() {
            long date = System.currentTimeMillis();
            if (date - this.lastMaintain < 1000) {
                return;
            }
            this.lastMaintain = date;
            long check = this.timeouts.check(date);
            while (true) {
                long socket = check;
                if (socket != 0) {
                    if (AprEndpoint.log.isDebugEnabled()) {
                        AprEndpoint.log.debug(AbstractEndpoint.sm.getString("endpoint.debug.socketTimeout", Long.valueOf(socket)));
                    }
                    SocketWrapperBase<Long> socketWrapper = (SocketWrapperBase) AprEndpoint.this.connections.get(Long.valueOf(socket));
                    socketWrapper.setError(new SocketTimeoutException());
                    AprEndpoint.this.processSocket(socketWrapper, SocketEvent.ERROR, true);
                    check = this.timeouts.check(date);
                } else {
                    return;
                }
            }
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("Poller");
            long[] res = new long[this.actualPollerSize * 2];
            for (int i = 0; i < this.pollers.length; i++) {
                int count = Poll.pollset(this.pollers[i], res);
                buf.append(" [ ");
                for (int j = 0; j < count; j++) {
                    buf.append(this.desc[(2 * j) + 1]).append(" ");
                }
                buf.append("]");
            }
            return buf.toString();
        }

        @Override // java.lang.Runnable
        public void run() {
            SocketList localAddList = new SocketList(AprEndpoint.this.getMaxConnections());
            SocketList localCloseList = new SocketList(AprEndpoint.this.getMaxConnections());
            loop0: while (this.pollerRunning) {
                while (this.pollerRunning && this.connectionCount.get() < 1 && this.addList.size() < 1 && this.closeList.size() < 1) {
                    try {
                        if (AprEndpoint.this.getConnectionTimeout() > 0 && this.pollerRunning) {
                            maintain();
                        }
                        synchronized (this) {
                            if (this.pollerRunning && this.addList.size() < 1 && this.closeList.size() < 1) {
                                wait(AbstractComponentTracker.LINGERING_TIMEOUT);
                            }
                        }
                    } catch (InterruptedException e) {
                    } catch (Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                        AprEndpoint.this.getLog().warn(AbstractEndpoint.sm.getString("endpoint.timeout.err"));
                    }
                }
                if (!this.pollerRunning) {
                    break;
                }
                try {
                    synchronized (this) {
                        if (this.closeList.size() > 0) {
                            this.closeList.duplicate(localCloseList);
                            this.closeList.clear();
                        } else {
                            localCloseList.clear();
                        }
                        synchronized (this) {
                            if (this.addList.size() > 0) {
                                this.addList.duplicate(localAddList);
                                this.addList.clear();
                            } else {
                                localAddList.clear();
                            }
                        }
                    }
                    if (localCloseList.size() > 0) {
                        for (SocketInfo info = localCloseList.get(); info != null; info = localCloseList.get()) {
                            localAddList.remove(info.socket);
                            removeFromPoller(info.socket);
                            AprEndpoint.this.destroySocket(info.socket);
                        }
                    }
                    if (localAddList.size() > 0) {
                        for (SocketInfo info2 = localAddList.get(); info2 != null; info2 = localAddList.get()) {
                            if (AprEndpoint.log.isDebugEnabled()) {
                                AprEndpoint.log.debug(AbstractEndpoint.sm.getString("endpoint.debug.pollerAddDo", Long.valueOf(info2.socket)));
                            }
                            this.timeouts.remove(info2.socket);
                            AprSocketWrapper wrapper = (AprSocketWrapper) AprEndpoint.this.connections.get(Long.valueOf(info2.socket));
                            if (wrapper != null) {
                                if (!info2.read() && !info2.write()) {
                                    AprEndpoint.this.closeSocket(info2.socket);
                                    AprEndpoint.this.getLog().warn(AbstractEndpoint.sm.getString("endpoint.apr.pollAddInvalid", info2));
                                } else {
                                    wrapper.pollerFlags = wrapper.pollerFlags | (info2.read() ? 1 : 0) | (info2.write() ? 4 : 0);
                                    removeFromPoller(info2.socket);
                                    if (!addToPoller(info2.socket, wrapper.pollerFlags)) {
                                        AprEndpoint.this.closeSocket(info2.socket);
                                    } else {
                                        this.timeouts.add(info2.socket, System.currentTimeMillis() + info2.timeout);
                                    }
                                }
                            }
                        }
                    }
                    for (int i = 0; i < this.pollers.length; i++) {
                        boolean reset = false;
                        int rv = 0;
                        if (this.pollerSpace[i] < this.actualPollerSize) {
                            rv = Poll.poll(this.pollers[i], this.nextPollerTime, this.desc, true);
                            this.nextPollerTime = this.pollerTime;
                        } else {
                            this.nextPollerTime += this.pollerTime;
                        }
                        if (rv > 0) {
                            int rv2 = mergeDescriptors(this.desc, rv);
                            int[] iArr = this.pollerSpace;
                            int i2 = i;
                            iArr[i2] = iArr[i2] + rv2;
                            this.connectionCount.addAndGet(-rv2);
                            for (int n = 0; n < rv2; n++) {
                                if (AprEndpoint.this.getLog().isDebugEnabled()) {
                                    AprEndpoint.log.debug(AbstractEndpoint.sm.getString("endpoint.debug.pollerProcess", Long.valueOf(this.desc[(n * 2) + 1]), Long.valueOf(this.desc[n * 2])));
                                }
                                long timeout = this.timeouts.remove(this.desc[(n * 2) + 1]);
                                AprSocketWrapper wrapper2 = (AprSocketWrapper) AprEndpoint.this.connections.get(Long.valueOf(this.desc[(n * 2) + 1]));
                                if (wrapper2 != null) {
                                    wrapper2.pollerFlags &= ((int) this.desc[n * 2]) ^ (-1);
                                    if ((this.desc[n * 2] & 32) == 32 || (this.desc[n * 2] & 16) == 16 || (this.desc[n * 2] & 64) == 64) {
                                        if ((this.desc[n * 2] & 1) == 1) {
                                            if (!AprEndpoint.this.processSocket(this.desc[(n * 2) + 1], SocketEvent.OPEN_READ)) {
                                                AprEndpoint.this.closeSocket(this.desc[(n * 2) + 1]);
                                            }
                                        } else if ((this.desc[n * 2] & 4) == 4) {
                                            if (!AprEndpoint.this.processSocket(this.desc[(n * 2) + 1], SocketEvent.OPEN_WRITE)) {
                                                AprEndpoint.this.closeSocket(this.desc[(n * 2) + 1]);
                                            }
                                        } else if ((wrapper2.pollerFlags & 1) == 1) {
                                            if (!AprEndpoint.this.processSocket(this.desc[(n * 2) + 1], SocketEvent.OPEN_READ)) {
                                                AprEndpoint.this.closeSocket(this.desc[(n * 2) + 1]);
                                            }
                                        } else if ((wrapper2.pollerFlags & 4) != 4) {
                                            AprEndpoint.this.closeSocket(this.desc[(n * 2) + 1]);
                                        } else if (!AprEndpoint.this.processSocket(this.desc[(n * 2) + 1], SocketEvent.OPEN_WRITE)) {
                                            AprEndpoint.this.closeSocket(this.desc[(n * 2) + 1]);
                                        }
                                    } else if ((this.desc[n * 2] & 1) == 1 || (this.desc[n * 2] & 4) == 4) {
                                        boolean error = false;
                                        if ((this.desc[n * 2] & 1) == 1 && !AprEndpoint.this.processSocket(this.desc[(n * 2) + 1], SocketEvent.OPEN_READ)) {
                                            error = true;
                                            AprEndpoint.this.closeSocket(this.desc[(n * 2) + 1]);
                                        }
                                        if (!error && (this.desc[n * 2] & 4) == 4 && !AprEndpoint.this.processSocket(this.desc[(n * 2) + 1], SocketEvent.OPEN_WRITE)) {
                                            error = true;
                                            AprEndpoint.this.closeSocket(this.desc[(n * 2) + 1]);
                                        }
                                        if (!error && wrapper2.pollerFlags != 0) {
                                            if (timeout > 0) {
                                                timeout -= System.currentTimeMillis();
                                            }
                                            if (timeout <= 0) {
                                                timeout = 1;
                                            }
                                            if (timeout > 2147483647L) {
                                                timeout = 2147483647L;
                                            }
                                            add(this.desc[(n * 2) + 1], (int) timeout, wrapper2.pollerFlags);
                                        }
                                    } else {
                                        AprEndpoint.this.getLog().warn(AbstractEndpoint.sm.getString("endpoint.apr.pollUnknownEvent", Long.valueOf(this.desc[n * 2])));
                                        AprEndpoint.this.closeSocket(this.desc[(n * 2) + 1]);
                                    }
                                }
                            }
                        } else if (rv < 0) {
                            int errn = -rv;
                            if (errn != 120001 && errn != 120003) {
                                if (errn > 120000) {
                                    errn -= 120000;
                                }
                                AprEndpoint.this.getLog().error(AbstractEndpoint.sm.getString("endpoint.apr.pollError", Integer.valueOf(errn), Error.strerror(errn)));
                                reset = true;
                            }
                        }
                        if (reset && this.pollerRunning) {
                            int count = Poll.pollset(this.pollers[i], this.desc);
                            long newPoller = AprEndpoint.this.allocatePoller(this.actualPollerSize, this.pool, -1);
                            this.pollerSpace[i] = this.actualPollerSize;
                            this.connectionCount.addAndGet(-count);
                            Poll.destroy(this.pollers[i]);
                            this.pollers[i] = newPoller;
                        }
                    }
                } catch (Throwable t2) {
                    ExceptionUtils.handleThrowable(t2);
                    AprEndpoint.this.getLog().warn(AbstractEndpoint.sm.getString("endpoint.poll.error"), t2);
                }
                try {
                    if (AprEndpoint.this.getConnectionTimeout() > 0 && this.pollerRunning) {
                        maintain();
                    }
                } catch (Throwable t3) {
                    ExceptionUtils.handleThrowable(t3);
                    AprEndpoint.this.getLog().warn(AbstractEndpoint.sm.getString("endpoint.timeout.err"), t3);
                }
            }
            synchronized (this) {
                notifyAll();
            }
        }

        private int mergeDescriptors(long[] desc, int startCount) {
            Map<Long, Long> merged = new HashMap<>(startCount);
            for (int n = 0; n < startCount; n++) {
                Long newValue = merged.merge(Long.valueOf(desc[(2 * n) + 1]), Long.valueOf(desc[2 * n]), v1, v2 -> {
                    return Long.valueOf(v1.longValue() | v2.longValue());
                });
                if (AprEndpoint.log.isDebugEnabled() && newValue.longValue() != desc[2 * n]) {
                    AprEndpoint.log.debug(AbstractEndpoint.sm.getString("endpoint.apr.pollMergeEvents", Long.valueOf(desc[(2 * n) + 1]), Long.valueOf(desc[2 * n]), newValue));
                }
            }
            int i = 0;
            for (Map.Entry<Long, Long> entry : merged.entrySet()) {
                int i2 = i;
                int i3 = i + 1;
                desc[i2] = entry.getValue().longValue();
                i = i3 + 1;
                desc[i3] = entry.getKey().longValue();
            }
            return merged.size();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/AprEndpoint$SendfileData.class */
    public static class SendfileData extends SendfileDataBase {
        protected long fd;
        protected long fdpool;
        protected long socket;

        public SendfileData(String filename, long pos, long length) {
            super(filename, pos, length);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/AprEndpoint$Sendfile.class */
    public class Sendfile implements Runnable {
        protected long[] desc;
        protected HashMap<Long, SendfileData> sendfileData;
        protected int sendfileCount;
        protected ArrayList<SendfileData> addS;
        private volatile Thread sendfileThread;
        protected long sendfilePollset = 0;
        protected long pool = 0;
        private volatile boolean sendfileRunning = true;

        public Sendfile() {
        }

        public int getSendfileCount() {
            return this.sendfileCount;
        }

        protected void init() {
            this.pool = Pool.create(AprEndpoint.this.serverSockPool);
            int size = AprEndpoint.this.sendfileSize;
            if (size <= 0) {
                size = (OS.IS_WIN32 || OS.IS_WIN64) ? 1024 : 16384;
            }
            this.sendfilePollset = AprEndpoint.this.allocatePoller(size, this.pool, AprEndpoint.this.getConnectionTimeout());
            if (this.sendfilePollset == 0 && size > 1024) {
                size = 1024;
                this.sendfilePollset = AprEndpoint.this.allocatePoller(1024, this.pool, AprEndpoint.this.getConnectionTimeout());
            }
            if (this.sendfilePollset == 0) {
                size = 62;
                this.sendfilePollset = AprEndpoint.this.allocatePoller(62, this.pool, AprEndpoint.this.getConnectionTimeout());
            }
            this.desc = new long[size * 2];
            this.sendfileData = new HashMap<>(size);
            this.addS = new ArrayList<>();
        }

        protected void start() {
            this.sendfileThread = new Thread(AprEndpoint.this.sendfile, AprEndpoint.this.getName() + "-Sendfile");
            this.sendfileThread.setPriority(AprEndpoint.this.threadPriority);
            this.sendfileThread.setDaemon(true);
            this.sendfileThread.start();
        }

        protected void stop() {
            this.sendfileRunning = false;
            for (int loops = 50; loops > 0 && this.sendfileThread.isAlive(); loops--) {
                try {
                    wait(AprEndpoint.this.pollTime / 1000);
                } catch (InterruptedException e) {
                }
            }
            if (this.sendfileThread.isAlive()) {
                AprEndpoint.log.warn(AbstractEndpoint.sm.getString("endpoint.sendfileThreadStop"));
            }
            notify();
        }

        protected void destroy() {
            for (int i = this.addS.size() - 1; i >= 0; i--) {
                SendfileData data = this.addS.get(i);
                AprEndpoint.this.closeSocket(data.socket);
            }
            int rv = Poll.pollset(this.sendfilePollset, this.desc);
            if (rv > 0) {
                for (int n = 0; n < rv; n++) {
                    AprEndpoint.this.closeSocket(this.desc[(n * 2) + 1]);
                }
            }
            Pool.destroy(this.pool);
            this.sendfileData.clear();
        }

        /* JADX WARN: Code restructure failed: missing block: B:21:0x00bd, code lost:
            monitor-enter(r10);
         */
        /* JADX WARN: Code restructure failed: missing block: B:22:0x00be, code lost:
            r10.addS.add(r11);
            notify();
         */
        /* JADX WARN: Code restructure failed: missing block: B:23:0x00cc, code lost:
            monitor-exit(r10);
         */
        /* JADX WARN: Code restructure failed: missing block: B:31:0x00da, code lost:
            return org.apache.tomcat.util.net.SendfileState.PENDING;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public org.apache.tomcat.util.net.SendfileState add(org.apache.tomcat.util.net.AprEndpoint.SendfileData r11) {
            /*
                Method dump skipped, instructions count: 219
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.net.AprEndpoint.Sendfile.add(org.apache.tomcat.util.net.AprEndpoint$SendfileData):org.apache.tomcat.util.net.SendfileState");
        }

        protected void remove(SendfileData data) {
            int rv = Poll.remove(this.sendfilePollset, data.socket);
            if (rv == 0) {
                this.sendfileCount--;
            }
            this.sendfileData.remove(Long.valueOf(data.socket));
        }

        /* JADX WARN: Removed duplicated region for block: B:129:0x0398 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void run() {
            /*
                Method dump skipped, instructions count: 937
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.net.AprEndpoint.Sendfile.run():void");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/AprEndpoint$SocketWithOptionsProcessor.class */
    public class SocketWithOptionsProcessor implements Runnable {
        protected SocketWrapperBase<Long> socket;

        public SocketWithOptionsProcessor(SocketWrapperBase<Long> socket) {
            this.socket = null;
            this.socket = socket;
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (this.socket) {
                if (!AprEndpoint.this.deferAccept) {
                    if (!AprEndpoint.this.setSocketOptions(this.socket)) {
                        AprEndpoint.this.closeSocket(this.socket.getSocket().longValue());
                        this.socket = null;
                    } else {
                        AprEndpoint.this.getPoller().add(this.socket.getSocket().longValue(), AprEndpoint.this.getConnectionTimeout(), 1);
                    }
                } else if (!AprEndpoint.this.setSocketOptions(this.socket)) {
                    AprEndpoint.this.closeSocket(this.socket.getSocket().longValue());
                    this.socket = null;
                } else {
                    AbstractEndpoint.Handler.SocketState state = AprEndpoint.this.getHandler().process(this.socket, SocketEvent.OPEN_READ);
                    if (state == AbstractEndpoint.Handler.SocketState.CLOSED) {
                        AprEndpoint.this.closeSocket(this.socket.getSocket().longValue());
                        this.socket = null;
                    }
                }
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/AprEndpoint$SocketProcessor.class */
    protected class SocketProcessor extends SocketProcessorBase<Long> {
        public SocketProcessor(SocketWrapperBase<Long> socketWrapper, SocketEvent event) {
            super(socketWrapper, event);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.apache.tomcat.util.net.SocketProcessorBase
        protected void doRun() {
            try {
                AbstractEndpoint.Handler.SocketState state = AprEndpoint.this.getHandler().process(this.socketWrapper, this.event);
                if (state == AbstractEndpoint.Handler.SocketState.CLOSED) {
                    AprEndpoint.this.closeSocket(((Long) this.socketWrapper.getSocket()).longValue());
                }
            } finally {
                this.socketWrapper = null;
                this.event = null;
                if (AprEndpoint.this.running && !AprEndpoint.this.paused) {
                    AprEndpoint.this.processorCache.push(this);
                }
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/AprEndpoint$AprSocketWrapper.class */
    public static class AprSocketWrapper extends SocketWrapperBase<Long> {
        private static final int SSL_OUTPUT_BUFFER_SIZE = 8192;
        private final ByteBuffer sslOutputBuffer;
        private final Object closedLock;
        private volatile boolean closed;
        private int pollerFlags;

        public AprSocketWrapper(Long socket, AprEndpoint endpoint) {
            super(socket, endpoint);
            this.closedLock = new Object();
            this.closed = false;
            this.pollerFlags = 0;
            if (endpoint.isSSLEnabled()) {
                this.sslOutputBuffer = ByteBuffer.allocateDirect(8192);
                this.sslOutputBuffer.position(8192);
            } else {
                this.sslOutputBuffer = null;
            }
            this.socketBufferHandler = new SocketBufferHandler(9000, 9000, true);
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public int read(boolean block, byte[] b, int off, int len) throws IOException {
            int nRead = populateReadBuffer(b, off, len);
            if (nRead > 0) {
                return nRead;
            }
            int nRead2 = fillReadBuffer(block);
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
            if (to.isDirect() && to.remaining() >= limit) {
                to.limit(to.position() + limit);
                nRead = fillReadBuffer(block, to);
                if (AprEndpoint.log.isDebugEnabled()) {
                    AprEndpoint.log.debug("Socket: [" + this + "], Read direct from socket: [" + nRead + "]");
                }
            } else {
                nRead = fillReadBuffer(block);
                if (AprEndpoint.log.isDebugEnabled()) {
                    AprEndpoint.log.debug("Socket: [" + this + "], Read into buffer: [" + nRead + "]");
                }
                if (nRead > 0) {
                    nRead = populateReadBuffer(to);
                }
            }
            return nRead;
        }

        private int fillReadBuffer(boolean block) throws IOException {
            this.socketBufferHandler.configureReadBufferForWrite();
            return fillReadBuffer(block, this.socketBufferHandler.getReadBuffer());
        }

        /* JADX WARN: Finally extract failed */
        private int fillReadBuffer(boolean block, ByteBuffer to) throws IOException {
            if (this.closed) {
                throw new IOException(sm.getString("socket.apr.closed", getSocket()));
            }
            Lock readLock = getBlockingStatusReadLock();
            ReentrantReadWriteLock.WriteLock writeLock = getBlockingStatusWriteLock();
            boolean readDone = false;
            int result = 0;
            readLock.lock();
            try {
                if (getBlockingStatus() == block) {
                    if (block) {
                        Socket.timeoutSet(getSocket().longValue(), getReadTimeout() * 1000);
                    }
                    result = Socket.recvb(getSocket().longValue(), to, to.position(), to.remaining());
                    readDone = true;
                }
                readLock.unlock();
                if (!readDone) {
                    writeLock.lock();
                    try {
                        setBlockingStatus(block);
                        if (block) {
                            Socket.timeoutSet(getSocket().longValue(), getReadTimeout() * 1000);
                        } else {
                            Socket.timeoutSet(getSocket().longValue(), 0L);
                        }
                        readLock.lock();
                        writeLock.unlock();
                        result = Socket.recvb(getSocket().longValue(), to, to.position(), to.remaining());
                        readLock.unlock();
                    } finally {
                        if (writeLock.isHeldByCurrentThread()) {
                            writeLock.unlock();
                        }
                    }
                }
                if (result > 0) {
                    to.position(to.position() + result);
                    return result;
                } else if (result == 0 || (-result) == 120002) {
                    return 0;
                } else {
                    if ((-result) == 120005 || (-result) == 120001) {
                        if (block) {
                            throw new SocketTimeoutException(sm.getString("iib.readtimeout"));
                        }
                        return 0;
                    } else if ((-result) == 70014) {
                        return -1;
                    } else {
                        if ((OS.IS_WIN32 || OS.IS_WIN64) && (-result) == 730053) {
                            throw new EOFException(sm.getString("socket.apr.clientAbort"));
                        }
                        throw new IOException(sm.getString("socket.apr.read.error", Integer.valueOf(-result), getSocket(), this));
                    }
                }
            } catch (Throwable th) {
                readLock.unlock();
                throw th;
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean isReadyForRead() throws IOException {
            this.socketBufferHandler.configureReadBufferForRead();
            if (this.socketBufferHandler.getReadBuffer().remaining() > 0) {
                return true;
            }
            int read = fillReadBuffer(false);
            boolean isReady = this.socketBufferHandler.getReadBuffer().position() > 0 || read == -1;
            return isReady;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void close() {
            getEndpoint().getHandler().release(this);
            synchronized (this.closedLock) {
                if (this.closed) {
                    return;
                }
                this.closed = true;
                if (this.sslOutputBuffer != null) {
                    ByteBufferUtils.cleanDirectBuffer(this.sslOutputBuffer);
                }
                ((AprEndpoint) getEndpoint()).getPoller().close(getSocket().longValue());
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean isClosed() {
            boolean z;
            synchronized (this.closedLock) {
                z = this.closed;
            }
            return z;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void writeBlockingDirect(ByteBuffer from) throws IOException {
            if (from.isDirect()) {
                super.writeBlockingDirect(from);
                return;
            }
            ByteBuffer writeBuffer = this.socketBufferHandler.getWriteBuffer();
            int limit = writeBuffer.capacity();
            while (from.remaining() >= limit) {
                this.socketBufferHandler.configureWriteBufferForWrite();
                transfer(from, writeBuffer);
                doWrite(true);
            }
            if (from.remaining() > 0) {
                this.socketBufferHandler.configureWriteBufferForWrite();
                transfer(from, writeBuffer);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void writeNonBlockingDirect(ByteBuffer from) throws IOException {
            if (from.isDirect()) {
                super.writeNonBlockingDirect(from);
                return;
            }
            ByteBuffer writeBuffer = this.socketBufferHandler.getWriteBuffer();
            int limit = writeBuffer.capacity();
            while (from.remaining() >= limit) {
                this.socketBufferHandler.configureWriteBufferForWrite();
                transfer(from, writeBuffer);
                int newPosition = writeBuffer.position() + limit;
                doWrite(false);
                if (writeBuffer.position() != newPosition) {
                    return;
                }
            }
            if (from.remaining() > 0) {
                this.socketBufferHandler.configureWriteBufferForWrite();
                transfer(from, writeBuffer);
            }
        }

        /* JADX WARN: Finally extract failed */
        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void doWrite(boolean block, ByteBuffer from) throws IOException {
            if (this.closed) {
                throw new IOException(sm.getString("socket.apr.closed", getSocket()));
            }
            Lock readLock = getBlockingStatusReadLock();
            ReentrantReadWriteLock.WriteLock writeLock = getBlockingStatusWriteLock();
            readLock.lock();
            try {
                if (getBlockingStatus() == block) {
                    if (block) {
                        Socket.timeoutSet(getSocket().longValue(), getWriteTimeout() * 1000);
                    }
                    doWriteInternal(from);
                    readLock.unlock();
                    return;
                }
                readLock.unlock();
                writeLock.lock();
                try {
                    setBlockingStatus(block);
                    if (block) {
                        Socket.timeoutSet(getSocket().longValue(), getWriteTimeout() * 1000);
                    } else {
                        Socket.timeoutSet(getSocket().longValue(), 0L);
                    }
                    readLock.lock();
                    writeLock.unlock();
                    doWriteInternal(from);
                    readLock.unlock();
                } finally {
                    if (writeLock.isHeldByCurrentThread()) {
                        writeLock.unlock();
                    }
                }
            } catch (Throwable th) {
                readLock.unlock();
                throw th;
            }
        }

        private void doWriteInternal(ByteBuffer from) throws IOException {
            int thisTime;
            do {
                if (getEndpoint().isSSLEnabled()) {
                    if (this.sslOutputBuffer.remaining() == 0) {
                        this.sslOutputBuffer.clear();
                        transfer(from, this.sslOutputBuffer);
                        this.sslOutputBuffer.flip();
                    }
                    thisTime = Socket.sendb(getSocket().longValue(), this.sslOutputBuffer, this.sslOutputBuffer.position(), this.sslOutputBuffer.limit());
                    if (thisTime > 0) {
                        this.sslOutputBuffer.position(this.sslOutputBuffer.position() + thisTime);
                    }
                } else {
                    thisTime = Socket.sendb(getSocket().longValue(), from, from.position(), from.remaining());
                    if (thisTime > 0) {
                        from.position(from.position() + thisTime);
                    }
                }
                if (Status.APR_STATUS_IS_EAGAIN(-thisTime)) {
                    thisTime = 0;
                } else if ((-thisTime) == 70014) {
                    throw new EOFException(sm.getString("socket.apr.clientAbort"));
                } else {
                    if ((OS.IS_WIN32 || OS.IS_WIN64) && (-thisTime) == 730053) {
                        throw new EOFException(sm.getString("socket.apr.clientAbort"));
                    }
                    if (thisTime < 0) {
                        throw new IOException(sm.getString("socket.apr.write.error", Integer.valueOf(-thisTime), getSocket(), this));
                    }
                }
                if (thisTime <= 0 && !getBlockingStatus()) {
                    return;
                }
            } while (from.hasRemaining());
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void registerReadInterest() {
            synchronized (this.closedLock) {
                if (this.closed) {
                    return;
                }
                Poller p = ((AprEndpoint) getEndpoint()).getPoller();
                if (p != null) {
                    p.add(getSocket().longValue(), getReadTimeout(), 1);
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void registerWriteInterest() {
            synchronized (this.closedLock) {
                if (this.closed) {
                    return;
                }
                ((AprEndpoint) getEndpoint()).getPoller().add(getSocket().longValue(), getWriteTimeout(), 4);
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public SendfileDataBase createSendfileData(String filename, long pos, long length) {
            return new SendfileData(filename, pos, length);
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public SendfileState processSendfile(SendfileDataBase sendfileData) {
            ((SendfileData) sendfileData).socket = getSocket().longValue();
            return ((AprEndpoint) getEndpoint()).getSendfile().add((SendfileData) sendfileData);
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateRemoteAddr() {
            if (this.closed) {
                return;
            }
            try {
                long socket = getSocket().longValue();
                long sa = Address.get(1, socket);
                this.remoteAddr = Address.getip(sa);
            } catch (Exception e) {
                AprEndpoint.log.warn(sm.getString("endpoint.warn.noRemoteAddr", getSocket()), e);
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateRemoteHost() {
            if (this.closed) {
                return;
            }
            try {
                long socket = getSocket().longValue();
                long sa = Address.get(1, socket);
                this.remoteHost = Address.getnameinfo(sa, 0);
                if (this.remoteAddr == null) {
                    this.remoteAddr = Address.getip(sa);
                }
            } catch (Exception e) {
                AprEndpoint.log.warn(sm.getString("endpoint.warn.noRemoteHost", getSocket()), e);
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateRemotePort() {
            if (this.closed) {
                return;
            }
            try {
                long socket = getSocket().longValue();
                long sa = Address.get(1, socket);
                Sockaddr addr = Address.getInfo(sa);
                this.remotePort = addr.port;
            } catch (Exception e) {
                AprEndpoint.log.warn(sm.getString("endpoint.warn.noRemotePort", getSocket()), e);
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateLocalName() {
            if (this.closed) {
                return;
            }
            try {
                long socket = getSocket().longValue();
                long sa = Address.get(0, socket);
                this.localName = Address.getnameinfo(sa, 0);
            } catch (Exception e) {
                AprEndpoint.log.warn(sm.getString("endpoint.warn.noLocalName"), e);
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateLocalAddr() {
            if (this.closed) {
                return;
            }
            try {
                long socket = getSocket().longValue();
                long sa = Address.get(0, socket);
                this.localAddr = Address.getip(sa);
            } catch (Exception e) {
                AprEndpoint.log.warn(sm.getString("endpoint.warn.noLocalAddr"), e);
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateLocalPort() {
            if (this.closed) {
                return;
            }
            try {
                long socket = getSocket().longValue();
                long sa = Address.get(0, socket);
                Sockaddr addr = Address.getInfo(sa);
                this.localPort = addr.port;
            } catch (Exception e) {
                AprEndpoint.log.warn(sm.getString("endpoint.warn.noLocalPort"), e);
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public SSLSupport getSslSupport(String clientCertProvider) {
            if (getEndpoint().isSSLEnabled()) {
                return new AprSSLSupport(this, clientCertProvider);
            }
            return null;
        }

        /* JADX WARN: Finally extract failed */
        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void doClientAuth(SSLSupport sslSupport) throws IOException {
            long socket = getSocket().longValue();
            Lock readLock = getBlockingStatusReadLock();
            ReentrantReadWriteLock.WriteLock writeLock = getBlockingStatusWriteLock();
            boolean renegotiateDone = false;
            try {
                readLock.lock();
                if (getBlockingStatus()) {
                    Socket.timeoutSet(getSocket().longValue(), getReadTimeout() * 1000);
                    SSLSocket.setVerify(socket, 2, -1);
                    SSLSocket.renegotiate(socket);
                    renegotiateDone = true;
                }
                readLock.unlock();
                if (!renegotiateDone) {
                    writeLock.lock();
                    setBlockingStatus(true);
                    Socket.timeoutSet(getSocket().longValue(), getReadTimeout() * 1000);
                    readLock.lock();
                    try {
                        writeLock.unlock();
                        SSLSocket.setVerify(socket, 2, -1);
                        SSLSocket.renegotiate(socket);
                        readLock.unlock();
                        if (writeLock.isHeldByCurrentThread()) {
                            writeLock.unlock();
                        }
                    } catch (Throwable th) {
                        readLock.unlock();
                        throw th;
                    }
                }
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                throw new IOException(sm.getString("socket.sslreneg"), t);
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void setAppReadBufHandler(ApplicationBufferHandler handler) {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getSSLInfoS(int id) {
            synchronized (this.closedLock) {
                if (this.closed) {
                    return null;
                }
                try {
                    return SSLSocket.getInfoS(getSocket().longValue(), id);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public int getSSLInfoI(int id) {
            synchronized (this.closedLock) {
                if (this.closed) {
                    return 0;
                }
                try {
                    return SSLSocket.getInfoI(getSocket().longValue(), id);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public byte[] getSSLInfoB(int id) {
            synchronized (this.closedLock) {
                if (this.closed) {
                    return null;
                }
                try {
                    return SSLSocket.getInfoB(getSocket().longValue(), id);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }
}