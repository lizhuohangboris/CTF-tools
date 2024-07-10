package org.apache.tomcat.util.net;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.net.SSLUtil;
import org.apache.tomcat.util.net.TLSClientHelloExtractor;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/SecureNioChannel.class */
public class SecureNioChannel extends NioChannel {
    private static final Log log = LogFactory.getLog(SecureNioChannel.class);
    private static final StringManager sm = StringManager.getManager(SecureNioChannel.class);
    private static final int DEFAULT_NET_BUFFER_SIZE = 16921;
    protected ByteBuffer netInBuffer;
    protected ByteBuffer netOutBuffer;
    protected SSLEngine sslEngine;
    protected boolean sniComplete;
    protected boolean handshakeComplete;
    protected SSLEngineResult.HandshakeStatus handshakeStatus;
    protected boolean closed;
    protected boolean closing;
    protected NioSelectorPool pool;
    private final NioEndpoint endpoint;

    public SecureNioChannel(SocketChannel channel, SocketBufferHandler bufHandler, NioSelectorPool pool, NioEndpoint endpoint) {
        super(channel, bufHandler);
        this.sniComplete = false;
        this.handshakeComplete = false;
        this.closed = false;
        this.closing = false;
        if (endpoint.getSocketProperties().getDirectSslBuffer()) {
            this.netInBuffer = ByteBuffer.allocateDirect(DEFAULT_NET_BUFFER_SIZE);
            this.netOutBuffer = ByteBuffer.allocateDirect(DEFAULT_NET_BUFFER_SIZE);
        } else {
            this.netInBuffer = ByteBuffer.allocate(DEFAULT_NET_BUFFER_SIZE);
            this.netOutBuffer = ByteBuffer.allocate(DEFAULT_NET_BUFFER_SIZE);
        }
        this.pool = pool;
        this.endpoint = endpoint;
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public void reset() throws IOException {
        super.reset();
        this.sslEngine = null;
        this.sniComplete = false;
        this.handshakeComplete = false;
        this.closed = false;
        this.closing = false;
        this.netInBuffer.clear();
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public void free() {
        super.free();
        if (this.endpoint.getSocketProperties().getDirectSslBuffer()) {
            ByteBufferUtils.cleanDirectBuffer(this.netInBuffer);
            ByteBufferUtils.cleanDirectBuffer(this.netOutBuffer);
        }
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public boolean flush(boolean block, Selector s, long timeout) throws IOException {
        if (!block) {
            flush(this.netOutBuffer);
        } else {
            this.pool.write(this.netOutBuffer, this, s, timeout, block);
        }
        return !this.netOutBuffer.hasRemaining();
    }

    protected boolean flush(ByteBuffer buf) throws IOException {
        int remaining = buf.remaining();
        if (remaining > 0) {
            int written = this.sc.write(buf);
            return written >= remaining;
        }
        return true;
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public int handshake(boolean read, boolean write) throws IOException {
        SSLEngineResult handshake;
        if (this.handshakeComplete) {
            return 0;
        }
        if (!this.sniComplete) {
            int sniResult = processSNI();
            if (sniResult == 0) {
                this.sniComplete = true;
            } else {
                return sniResult;
            }
        }
        if (flush(this.netOutBuffer)) {
            while (!this.handshakeComplete) {
                switch (AnonymousClass1.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[this.handshakeStatus.ordinal()]) {
                    case 1:
                        throw new IOException(sm.getString("channel.nio.ssl.notHandshaking"));
                    case 2:
                        if (this.endpoint.hasNegotiableProtocols()) {
                            if (this.sslEngine instanceof SSLUtil.ProtocolInfo) {
                                this.socketWrapper.setNegotiatedProtocol(((SSLUtil.ProtocolInfo) this.sslEngine).getNegotiatedProtocol());
                            } else if (JreCompat.isJre9Available()) {
                                this.socketWrapper.setNegotiatedProtocol(JreCompat.getInstance().getApplicationProtocol(this.sslEngine));
                            }
                        }
                        this.handshakeComplete = !this.netOutBuffer.hasRemaining();
                        return this.handshakeComplete ? 0 : 4;
                    case 3:
                        try {
                            handshake = handshakeWrap(write);
                        } catch (SSLException e) {
                            if (log.isDebugEnabled()) {
                                log.debug(sm.getString("channel.nio.ssl.wrapException"), e);
                            }
                            handshake = handshakeWrap(write);
                        }
                        if (handshake.getStatus() == SSLEngineResult.Status.OK) {
                            if (this.handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                                this.handshakeStatus = tasks();
                            }
                            if (this.handshakeStatus != SSLEngineResult.HandshakeStatus.NEED_UNWRAP || !flush(this.netOutBuffer)) {
                                return 4;
                            }
                        } else if (handshake.getStatus() == SSLEngineResult.Status.CLOSED) {
                            flush(this.netOutBuffer);
                            return -1;
                        } else {
                            throw new IOException(sm.getString("channel.nio.ssl.unexpectedStatusDuringWrap", handshake.getStatus()));
                        }
                        break;
                    case 4:
                        break;
                    case 5:
                        this.handshakeStatus = tasks();
                        continue;
                    default:
                        throw new IllegalStateException(sm.getString("channel.nio.ssl.invalidStatus", this.handshakeStatus));
                }
                SSLEngineResult handshake2 = handshakeUnwrap(read);
                if (handshake2.getStatus() == SSLEngineResult.Status.OK) {
                    if (this.handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                        this.handshakeStatus = tasks();
                    }
                } else if (handshake2.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                    return 1;
                } else {
                    throw new IOException(sm.getString("channel.nio.ssl.unexpectedStatusDuringWrap", handshake2.getStatus()));
                }
            }
            return 0;
        }
        return 4;
    }

    private int processSNI() throws IOException {
        TLSClientHelloExtractor extractor;
        int bytesRead = this.sc.read(this.netInBuffer);
        if (bytesRead == -1) {
            return -1;
        }
        TLSClientHelloExtractor tLSClientHelloExtractor = new TLSClientHelloExtractor(this.netInBuffer);
        while (true) {
            extractor = tLSClientHelloExtractor;
            if (extractor.getResult() != TLSClientHelloExtractor.ExtractorResult.UNDERFLOW || this.netInBuffer.capacity() >= this.endpoint.getSniParseLimit()) {
                break;
            }
            int newLimit = Math.min(this.netInBuffer.capacity() * 2, this.endpoint.getSniParseLimit());
            log.info(sm.getString("channel.nio.ssl.expandNetInBuffer", Integer.toString(newLimit)));
            this.netInBuffer = ByteBufferUtils.expand(this.netInBuffer, newLimit);
            this.sc.read(this.netInBuffer);
            tLSClientHelloExtractor = new TLSClientHelloExtractor(this.netInBuffer);
        }
        String hostName = null;
        List<Cipher> clientRequestedCiphers = null;
        List<String> clientRequestedApplicationProtocols = null;
        switch (extractor.getResult()) {
            case COMPLETE:
                hostName = extractor.getSNIValue();
                clientRequestedApplicationProtocols = extractor.getClientRequestedApplicationProtocols();
            case NOT_PRESENT:
                clientRequestedCiphers = extractor.getClientRequestedCiphers();
                break;
            case NEED_READ:
                return 1;
            case UNDERFLOW:
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("channel.nio.ssl.sniDefault"));
                }
                hostName = this.endpoint.getDefaultSSLHostConfigName();
                clientRequestedCiphers = Collections.emptyList();
                break;
            case NON_SECURE:
                this.netOutBuffer.clear();
                this.netOutBuffer.put(TLSClientHelloExtractor.USE_TLS_RESPONSE);
                this.netOutBuffer.flip();
                flushOutbound();
                throw new IOException(sm.getString("channel.nio.ssl.foundHttp"));
        }
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("channel.nio.ssl.sniHostName", this.sc, hostName));
        }
        this.sslEngine = this.endpoint.createSSLEngine(hostName, clientRequestedCiphers, clientRequestedApplicationProtocols);
        getBufHandler().expand(this.sslEngine.getSession().getApplicationBufferSize());
        if (this.netOutBuffer.capacity() < this.sslEngine.getSession().getApplicationBufferSize()) {
            log.info(sm.getString("channel.nio.ssl.expandNetOutBuffer", Integer.toString(this.sslEngine.getSession().getApplicationBufferSize())));
        }
        this.netInBuffer = ByteBufferUtils.expand(this.netInBuffer, this.sslEngine.getSession().getPacketBufferSize());
        this.netOutBuffer = ByteBufferUtils.expand(this.netOutBuffer, this.sslEngine.getSession().getPacketBufferSize());
        this.netOutBuffer.position(0);
        this.netOutBuffer.limit(0);
        this.sslEngine.beginHandshake();
        this.handshakeStatus = this.sslEngine.getHandshakeStatus();
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.apache.tomcat.util.net.SecureNioChannel$1  reason: invalid class name */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/SecureNioChannel$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus;

        static {
            try {
                $SwitchMap$org$apache$tomcat$util$net$TLSClientHelloExtractor$ExtractorResult[TLSClientHelloExtractor.ExtractorResult.COMPLETE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$tomcat$util$net$TLSClientHelloExtractor$ExtractorResult[TLSClientHelloExtractor.ExtractorResult.NOT_PRESENT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$tomcat$util$net$TLSClientHelloExtractor$ExtractorResult[TLSClientHelloExtractor.ExtractorResult.NEED_READ.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$tomcat$util$net$TLSClientHelloExtractor$ExtractorResult[TLSClientHelloExtractor.ExtractorResult.UNDERFLOW.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$tomcat$util$net$TLSClientHelloExtractor$ExtractorResult[TLSClientHelloExtractor.ExtractorResult.NON_SECURE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus = new int[SSLEngineResult.HandshakeStatus.values().length];
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.FINISHED.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NEED_WRAP.ordinal()] = 3;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NEED_UNWRAP.ordinal()] = 4;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NEED_TASK.ordinal()] = 5;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    public void rehandshake(long timeout) throws IOException {
        if (this.netInBuffer.position() > 0 && this.netInBuffer.position() < this.netInBuffer.limit()) {
            throw new IOException(sm.getString("channel.nio.ssl.netInputNotEmpty"));
        }
        if (this.netOutBuffer.position() > 0 && this.netOutBuffer.position() < this.netOutBuffer.limit()) {
            throw new IOException(sm.getString("channel.nio.ssl.netOutputNotEmpty"));
        }
        if (!getBufHandler().isReadBufferEmpty()) {
            throw new IOException(sm.getString("channel.nio.ssl.appInputNotEmpty"));
        }
        if (!getBufHandler().isWriteBufferEmpty()) {
            throw new IOException(sm.getString("channel.nio.ssl.appOutputNotEmpty"));
        }
        this.handshakeComplete = false;
        boolean isReadable = false;
        boolean isWriteable = false;
        boolean handshaking = true;
        Selector selector = null;
        SelectionKey key = null;
        try {
            try {
                this.sslEngine.beginHandshake();
                this.handshakeStatus = this.sslEngine.getHandshakeStatus();
                while (handshaking) {
                    int hsStatus = handshake(isReadable, isWriteable);
                    switch (hsStatus) {
                        case -1:
                            throw new EOFException(sm.getString("channel.nio.ssl.eofDuringHandshake"));
                        case 0:
                            handshaking = false;
                            break;
                        default:
                            long now = System.currentTimeMillis();
                            if (selector == null) {
                                selector = Selector.open();
                                key = getIOChannel().register(selector, hsStatus);
                            } else {
                                key.interestOps(hsStatus);
                            }
                            int keyCount = selector.select(timeout);
                            if (keyCount == 0 && System.currentTimeMillis() - now >= timeout) {
                                throw new SocketTimeoutException(sm.getString("channel.nio.ssl.timeoutDuringHandshake"));
                            }
                            isReadable = key.isReadable();
                            isWriteable = key.isWritable();
                            break;
                    }
                }
                if (key != null) {
                    try {
                        key.cancel();
                    } catch (Exception e) {
                    }
                }
                if (selector != null) {
                    try {
                        selector.close();
                    } catch (Exception e2) {
                    }
                }
            } catch (IOException x) {
                closeSilently();
                throw x;
            } catch (Exception cx) {
                closeSilently();
                IOException x2 = new IOException(cx);
                throw x2;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    key.cancel();
                } catch (Exception e3) {
                }
            }
            if (0 != 0) {
                try {
                    selector.close();
                } catch (Exception e4) {
                }
            }
            throw th;
        }
    }

    protected SSLEngineResult.HandshakeStatus tasks() {
        while (true) {
            Runnable r = this.sslEngine.getDelegatedTask();
            if (r != null) {
                r.run();
            } else {
                return this.sslEngine.getHandshakeStatus();
            }
        }
    }

    protected SSLEngineResult handshakeWrap(boolean doWrite) throws IOException {
        this.netOutBuffer.clear();
        getBufHandler().configureWriteBufferForRead();
        SSLEngineResult result = this.sslEngine.wrap(getBufHandler().getWriteBuffer(), this.netOutBuffer);
        this.netOutBuffer.flip();
        this.handshakeStatus = result.getHandshakeStatus();
        if (doWrite) {
            flush(this.netOutBuffer);
        }
        return result;
    }

    protected SSLEngineResult handshakeUnwrap(boolean doread) throws IOException {
        SSLEngineResult result;
        boolean cont;
        if (this.netInBuffer.position() == this.netInBuffer.limit()) {
            this.netInBuffer.clear();
        }
        if (doread) {
            int read = this.sc.read(this.netInBuffer);
            if (read == -1) {
                throw new IOException(sm.getString("channel.nio.ssl.eofDuringHandshake"));
            }
        }
        do {
            this.netInBuffer.flip();
            getBufHandler().configureReadBufferForWrite();
            result = this.sslEngine.unwrap(this.netInBuffer, getBufHandler().getReadBuffer());
            this.netInBuffer.compact();
            this.handshakeStatus = result.getHandshakeStatus();
            if (result.getStatus() == SSLEngineResult.Status.OK && result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                this.handshakeStatus = tasks();
            }
            cont = result.getStatus() == SSLEngineResult.Status.OK && this.handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
        } while (cont);
        return result;
    }

    @Override // org.apache.tomcat.util.net.NioChannel, java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closing) {
            return;
        }
        this.closing = true;
        this.sslEngine.closeOutbound();
        if (!flush(this.netOutBuffer)) {
            throw new IOException(sm.getString("channel.nio.ssl.remainingDataDuringClose"));
        }
        this.netOutBuffer.clear();
        SSLEngineResult handshake = this.sslEngine.wrap(getEmptyBuf(), this.netOutBuffer);
        if (handshake.getStatus() != SSLEngineResult.Status.CLOSED) {
            throw new IOException(sm.getString("channel.nio.ssl.invalidCloseState"));
        }
        this.netOutBuffer.flip();
        flush(this.netOutBuffer);
        this.closed = (this.netOutBuffer.hasRemaining() || handshake.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_WRAP) ? false : true;
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public void close(boolean force) throws IOException {
        try {
            close();
            if (force || this.closed) {
                this.closed = true;
                this.sc.socket().close();
                this.sc.close();
            }
        } catch (Throwable th) {
            if (force || this.closed) {
                this.closed = true;
                this.sc.socket().close();
                this.sc.close();
            }
            throw th;
        }
    }

    private void closeSilently() {
        try {
            close(true);
        } catch (IOException ioe) {
            log.debug(sm.getString("channel.nio.ssl.closeSilentError"), ioe);
        }
    }

    @Override // org.apache.tomcat.util.net.NioChannel, java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer dst) throws IOException {
        if (dst != getBufHandler().getReadBuffer() && (getAppReadBufHandler() == null || dst != getAppReadBufHandler().getByteBuffer())) {
            throw new IllegalArgumentException(sm.getString("channel.nio.ssl.invalidBuffer"));
        }
        if (this.closing || this.closed) {
            return -1;
        }
        if (this.handshakeComplete) {
            int netread = this.sc.read(this.netInBuffer);
            if (netread == -1) {
                return -1;
            }
            int read = 0;
            do {
                this.netInBuffer.flip();
                SSLEngineResult unwrap = this.sslEngine.unwrap(this.netInBuffer, dst);
                this.netInBuffer.compact();
                if (unwrap.getStatus() == SSLEngineResult.Status.OK || unwrap.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                    read += unwrap.bytesProduced();
                    if (unwrap.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                        tasks();
                    }
                    if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                        break;
                    }
                } else if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                    if (read > 0) {
                        break;
                    } else if (dst == getBufHandler().getReadBuffer()) {
                        getBufHandler().expand(this.sslEngine.getSession().getApplicationBufferSize());
                        dst = getBufHandler().getReadBuffer();
                    } else if (dst == getAppReadBufHandler().getByteBuffer()) {
                        getAppReadBufHandler().expand(this.sslEngine.getSession().getApplicationBufferSize());
                        dst = getAppReadBufHandler().getByteBuffer();
                    } else {
                        throw new IOException(sm.getString("channel.nio.ssl.unwrapFailResize", unwrap.getStatus()));
                    }
                } else {
                    throw new IOException(sm.getString("channel.nio.ssl.unwrapFail", unwrap.getStatus()));
                }
            } while (this.netInBuffer.position() != 0);
            return read;
        }
        throw new IllegalStateException(sm.getString("channel.nio.ssl.incompleteHandshake"));
    }

    @Override // org.apache.tomcat.util.net.NioChannel, java.nio.channels.WritableByteChannel
    public int write(ByteBuffer src) throws IOException {
        checkInterruptStatus();
        if (src == this.netOutBuffer) {
            int written = this.sc.write(src);
            return written;
        } else if (this.closing || this.closed) {
            throw new IOException(sm.getString("channel.nio.ssl.closing"));
        } else {
            if (!flush(this.netOutBuffer)) {
                return 0;
            }
            this.netOutBuffer.clear();
            SSLEngineResult result = this.sslEngine.wrap(src, this.netOutBuffer);
            int written2 = result.bytesConsumed();
            this.netOutBuffer.flip();
            if (result.getStatus() == SSLEngineResult.Status.OK) {
                if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    tasks();
                }
                flush(this.netOutBuffer);
                return written2;
            }
            throw new IOException(sm.getString("channel.nio.ssl.wrapFail", result.getStatus()));
        }
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public int getOutboundRemaining() {
        return this.netOutBuffer.remaining();
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public boolean flushOutbound() throws IOException {
        int remaining = this.netOutBuffer.remaining();
        flush(this.netOutBuffer);
        int remaining2 = this.netOutBuffer.remaining();
        return remaining2 < remaining;
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public boolean isHandshakeComplete() {
        return this.handshakeComplete;
    }

    @Override // org.apache.tomcat.util.net.NioChannel
    public boolean isClosing() {
        return this.closing;
    }

    public SSLEngine getSslEngine() {
        return this.sslEngine;
    }

    public ByteBuffer getEmptyBuf() {
        return emptyBuf;
    }
}