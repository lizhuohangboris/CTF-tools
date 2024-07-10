package org.apache.tomcat.websocket;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.WritePendingException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.PongMessage;
import javax.websocket.RemoteEndpoint;
import javax.websocket.SendResult;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.InstanceManagerBindings;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsSession.class */
public class WsSession implements Session {
    private static final byte[] ELLIPSIS_BYTES = "…".getBytes(StandardCharsets.UTF_8);
    private static final int ELLIPSIS_BYTES_LEN = ELLIPSIS_BYTES.length;
    private static final StringManager sm = StringManager.getManager(WsSession.class);
    private static AtomicLong ids = new AtomicLong(0);
    private final Endpoint localEndpoint;
    private final WsRemoteEndpointImplBase wsRemoteEndpoint;
    private final RemoteEndpoint.Async remoteEndpointAsync;
    private final RemoteEndpoint.Basic remoteEndpointBasic;
    private final ClassLoader applicationClassLoader;
    private final WsWebSocketContainer webSocketContainer;
    private final URI requestUri;
    private final Map<String, List<String>> requestParameterMap;
    private final String queryString;
    private final Principal userPrincipal;
    private final EndpointConfig endpointConfig;
    private final List<Extension> negotiatedExtensions;
    private final String subProtocol;
    private final Map<String, String> pathParameters;
    private final boolean secure;
    private final String httpSessionId;
    private final String id;
    private volatile int maxBinaryMessageBufferSize;
    private volatile int maxTextMessageBufferSize;
    private volatile long maxIdleTimeout;
    private WsFrameBase wsFrame;
    private final Log log = LogFactory.getLog(WsSession.class);
    private volatile MessageHandler textMessageHandler = null;
    private volatile MessageHandler binaryMessageHandler = null;
    private volatile MessageHandler.Whole<PongMessage> pongMessageHandler = null;
    private volatile State state = State.OPEN;
    private final Object stateLock = new Object();
    private final Map<String, Object> userProperties = new ConcurrentHashMap();
    private volatile long lastActive = System.currentTimeMillis();
    private Map<FutureToSendHandler, FutureToSendHandler> futures = new ConcurrentHashMap();

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsSession$State.class */
    public enum State {
        OPEN,
        OUTPUT_CLOSED,
        CLOSED
    }

    public WsSession(Endpoint localEndpoint, WsRemoteEndpointImplBase wsRemoteEndpoint, WsWebSocketContainer wsWebSocketContainer, URI requestUri, Map<String, List<String>> requestParameterMap, String queryString, Principal userPrincipal, String httpSessionId, List<Extension> negotiatedExtensions, String subProtocol, Map<String, String> pathParameters, boolean secure, EndpointConfig endpointConfig) throws DeploymentException {
        this.maxBinaryMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.maxTextMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.maxIdleTimeout = 0L;
        this.localEndpoint = localEndpoint;
        this.wsRemoteEndpoint = wsRemoteEndpoint;
        this.wsRemoteEndpoint.setSession(this);
        this.remoteEndpointAsync = new WsRemoteEndpointAsync(wsRemoteEndpoint);
        this.remoteEndpointBasic = new WsRemoteEndpointBasic(wsRemoteEndpoint);
        this.webSocketContainer = wsWebSocketContainer;
        this.applicationClassLoader = Thread.currentThread().getContextClassLoader();
        wsRemoteEndpoint.setSendTimeout(wsWebSocketContainer.getDefaultAsyncSendTimeout());
        this.maxBinaryMessageBufferSize = this.webSocketContainer.getDefaultMaxBinaryMessageBufferSize();
        this.maxTextMessageBufferSize = this.webSocketContainer.getDefaultMaxTextMessageBufferSize();
        this.maxIdleTimeout = this.webSocketContainer.getDefaultMaxSessionIdleTimeout();
        this.requestUri = requestUri;
        if (requestParameterMap == null) {
            this.requestParameterMap = Collections.emptyMap();
        } else {
            this.requestParameterMap = requestParameterMap;
        }
        this.queryString = queryString;
        this.userPrincipal = userPrincipal;
        this.httpSessionId = httpSessionId;
        this.negotiatedExtensions = negotiatedExtensions;
        if (subProtocol == null) {
            this.subProtocol = "";
        } else {
            this.subProtocol = subProtocol;
        }
        this.pathParameters = pathParameters;
        this.secure = secure;
        this.wsRemoteEndpoint.setEncoders(endpointConfig);
        this.endpointConfig = endpointConfig;
        this.userProperties.putAll(endpointConfig.getUserProperties());
        this.id = Long.toHexString(ids.getAndIncrement());
        InstanceManager instanceManager = this.webSocketContainer.getInstanceManager();
        instanceManager = instanceManager == null ? InstanceManagerBindings.get(this.applicationClassLoader) : instanceManager;
        if (instanceManager != null) {
            try {
                instanceManager.newInstance(localEndpoint);
            } catch (Exception e) {
                throw new DeploymentException(sm.getString("wsSession.instanceNew"), e);
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("wsSession.created", this.id));
        }
    }

    @Override // javax.websocket.Session
    public WebSocketContainer getContainer() {
        checkState();
        return this.webSocketContainer;
    }

    @Override // javax.websocket.Session
    public void addMessageHandler(MessageHandler listener) {
        Class<?> target = Util.getMessageType(listener);
        doAddMessageHandler(target, listener);
    }

    @Override // javax.websocket.Session
    public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Partial<T> handler) throws IllegalStateException {
        doAddMessageHandler(clazz, handler);
    }

    @Override // javax.websocket.Session
    public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Whole<T> handler) throws IllegalStateException {
        doAddMessageHandler(clazz, handler);
    }

    private void doAddMessageHandler(Class<?> target, MessageHandler listener) {
        checkState();
        Set<MessageHandlerResult> mhResults = Util.getMessageHandlers(target, listener, this.endpointConfig, this);
        for (MessageHandlerResult mhResult : mhResults) {
            switch (mhResult.getType()) {
                case TEXT:
                    if (this.textMessageHandler != null) {
                        throw new IllegalStateException(sm.getString("wsSession.duplicateHandlerText"));
                    }
                    this.textMessageHandler = mhResult.getHandler();
                    break;
                case BINARY:
                    if (this.binaryMessageHandler != null) {
                        throw new IllegalStateException(sm.getString("wsSession.duplicateHandlerBinary"));
                    }
                    this.binaryMessageHandler = mhResult.getHandler();
                    break;
                case PONG:
                    if (this.pongMessageHandler != null) {
                        throw new IllegalStateException(sm.getString("wsSession.duplicateHandlerPong"));
                    }
                    MessageHandler handler = mhResult.getHandler();
                    if (handler instanceof MessageHandler.Whole) {
                        this.pongMessageHandler = (MessageHandler.Whole) handler;
                        break;
                    } else {
                        throw new IllegalStateException(sm.getString("wsSession.invalidHandlerTypePong"));
                    }
                default:
                    throw new IllegalArgumentException(sm.getString("wsSession.unknownHandlerType", listener, mhResult.getType()));
            }
        }
    }

    @Override // javax.websocket.Session
    public Set<MessageHandler> getMessageHandlers() {
        checkState();
        Set<MessageHandler> result = new HashSet<>();
        if (this.binaryMessageHandler != null) {
            result.add(this.binaryMessageHandler);
        }
        if (this.textMessageHandler != null) {
            result.add(this.textMessageHandler);
        }
        if (this.pongMessageHandler != null) {
            result.add(this.pongMessageHandler);
        }
        return result;
    }

    @Override // javax.websocket.Session
    public void removeMessageHandler(MessageHandler listener) {
        checkState();
        if (listener == null) {
            return;
        }
        MessageHandler wrapped = null;
        if (listener instanceof WrappedMessageHandler) {
            wrapped = ((WrappedMessageHandler) listener).getWrappedHandler();
        }
        if (wrapped == null) {
            wrapped = listener;
        }
        boolean removed = false;
        if (wrapped.equals(this.textMessageHandler) || listener.equals(this.textMessageHandler)) {
            this.textMessageHandler = null;
            removed = true;
        }
        if (wrapped.equals(this.binaryMessageHandler) || listener.equals(this.binaryMessageHandler)) {
            this.binaryMessageHandler = null;
            removed = true;
        }
        if (wrapped.equals(this.pongMessageHandler) || listener.equals(this.pongMessageHandler)) {
            this.pongMessageHandler = null;
            removed = true;
        }
        if (!removed) {
            throw new IllegalStateException(sm.getString("wsSession.removeHandlerFailed", listener));
        }
    }

    @Override // javax.websocket.Session
    public String getProtocolVersion() {
        checkState();
        return Constants.WS_VERSION_HEADER_VALUE;
    }

    @Override // javax.websocket.Session
    public String getNegotiatedSubprotocol() {
        checkState();
        return this.subProtocol;
    }

    @Override // javax.websocket.Session
    public List<Extension> getNegotiatedExtensions() {
        checkState();
        return this.negotiatedExtensions;
    }

    @Override // javax.websocket.Session
    public boolean isSecure() {
        checkState();
        return this.secure;
    }

    @Override // javax.websocket.Session
    public boolean isOpen() {
        return this.state == State.OPEN;
    }

    @Override // javax.websocket.Session
    public long getMaxIdleTimeout() {
        checkState();
        return this.maxIdleTimeout;
    }

    @Override // javax.websocket.Session
    public void setMaxIdleTimeout(long timeout) {
        checkState();
        this.maxIdleTimeout = timeout;
    }

    @Override // javax.websocket.Session
    public void setMaxBinaryMessageBufferSize(int max) {
        checkState();
        this.maxBinaryMessageBufferSize = max;
    }

    @Override // javax.websocket.Session
    public int getMaxBinaryMessageBufferSize() {
        checkState();
        return this.maxBinaryMessageBufferSize;
    }

    @Override // javax.websocket.Session
    public void setMaxTextMessageBufferSize(int max) {
        checkState();
        this.maxTextMessageBufferSize = max;
    }

    @Override // javax.websocket.Session
    public int getMaxTextMessageBufferSize() {
        checkState();
        return this.maxTextMessageBufferSize;
    }

    @Override // javax.websocket.Session
    public Set<Session> getOpenSessions() {
        checkState();
        return this.webSocketContainer.getOpenSessions(this.localEndpoint);
    }

    @Override // javax.websocket.Session
    public RemoteEndpoint.Async getAsyncRemote() {
        checkState();
        return this.remoteEndpointAsync;
    }

    @Override // javax.websocket.Session
    public RemoteEndpoint.Basic getBasicRemote() {
        checkState();
        return this.remoteEndpointBasic;
    }

    @Override // javax.websocket.Session, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, ""));
    }

    @Override // javax.websocket.Session
    public void close(CloseReason closeReason) throws IOException {
        doClose(closeReason, closeReason);
    }

    public void doClose(CloseReason closeReasonMessage, CloseReason closeReasonLocal) {
        if (this.state != State.OPEN) {
            return;
        }
        synchronized (this.stateLock) {
            if (this.state != State.OPEN) {
                return;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("wsSession.doClose", this.id));
            }
            try {
                this.wsRemoteEndpoint.setBatchingAllowed(false);
            } catch (IOException e) {
                this.log.warn(sm.getString("wsSession.flushFailOnClose"), e);
                fireEndpointOnError(e);
            }
            this.state = State.OUTPUT_CLOSED;
            sendCloseMessage(closeReasonMessage);
            fireEndpointOnClose(closeReasonLocal);
            IOException ioe = new IOException(sm.getString("wsSession.messageFailed"));
            SendResult sr = new SendResult(ioe);
            for (FutureToSendHandler f2sh : this.futures.keySet()) {
                f2sh.onResult(sr);
            }
        }
    }

    public void onClose(CloseReason closeReason) {
        synchronized (this.stateLock) {
            if (this.state != State.CLOSED) {
                try {
                    this.wsRemoteEndpoint.setBatchingAllowed(false);
                } catch (IOException e) {
                    this.log.warn(sm.getString("wsSession.flushFailOnClose"), e);
                    fireEndpointOnError(e);
                }
                if (this.state == State.OPEN) {
                    this.state = State.OUTPUT_CLOSED;
                    sendCloseMessage(closeReason);
                    fireEndpointOnClose(closeReason);
                }
                this.state = State.CLOSED;
                this.wsRemoteEndpoint.close();
            }
        }
    }

    private void fireEndpointOnClose(CloseReason closeReason) {
        Throwable throwable = null;
        InstanceManager instanceManager = this.webSocketContainer.getInstanceManager();
        if (instanceManager == null) {
            instanceManager = InstanceManagerBindings.get(this.applicationClassLoader);
        }
        Thread t = Thread.currentThread();
        ClassLoader cl = t.getContextClassLoader();
        t.setContextClassLoader(this.applicationClassLoader);
        try {
            this.localEndpoint.onClose(this, closeReason);
            if (instanceManager != null) {
                try {
                    instanceManager.destroyInstance(this.localEndpoint);
                } catch (Throwable t2) {
                    ExceptionUtils.handleThrowable(t2);
                    if (0 == 0) {
                        throwable = t2;
                    }
                }
            }
            t.setContextClassLoader(cl);
        } catch (Throwable t1) {
            try {
                ExceptionUtils.handleThrowable(t1);
                throwable = t1;
                if (instanceManager != null) {
                    try {
                        instanceManager.destroyInstance(this.localEndpoint);
                    } catch (Throwable t22) {
                        ExceptionUtils.handleThrowable(t22);
                        if (throwable == null) {
                            throwable = t22;
                        }
                    }
                }
                t.setContextClassLoader(cl);
            } finally {
                if (instanceManager != null) {
                    try {
                        instanceManager.destroyInstance(this.localEndpoint);
                    } catch (Throwable t23) {
                        ExceptionUtils.handleThrowable(t23);
                        if (throwable == null) {
                        }
                    }
                }
                t.setContextClassLoader(cl);
            }
        }
        if (throwable != null) {
            fireEndpointOnError(throwable);
        }
    }

    private void fireEndpointOnError(Throwable throwable) {
        Thread t = Thread.currentThread();
        ClassLoader cl = t.getContextClassLoader();
        t.setContextClassLoader(this.applicationClassLoader);
        try {
            this.localEndpoint.onError(this, throwable);
            t.setContextClassLoader(cl);
        } catch (Throwable th) {
            t.setContextClassLoader(cl);
            throw th;
        }
    }

    private void sendCloseMessage(CloseReason closeReason) {
        ByteBuffer msg = ByteBuffer.allocate(125);
        CloseReason.CloseCode closeCode = closeReason.getCloseCode();
        if (closeCode == CloseReason.CloseCodes.CLOSED_ABNORMALLY) {
            msg.putShort((short) CloseReason.CloseCodes.PROTOCOL_ERROR.getCode());
        } else {
            msg.putShort((short) closeCode.getCode());
        }
        String reason = closeReason.getReasonPhrase();
        if (reason != null && reason.length() > 0) {
            appendCloseReasonWithTruncation(msg, reason);
        }
        msg.flip();
        try {
            try {
                this.wsRemoteEndpoint.sendMessageBlock((byte) 8, msg, true);
                this.webSocketContainer.unregisterSession(this.localEndpoint, this);
            } catch (IOException | WritePendingException e) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug(sm.getString("wsSession.sendCloseFail", this.id), e);
                }
                this.wsRemoteEndpoint.close();
                if (closeCode != CloseReason.CloseCodes.CLOSED_ABNORMALLY) {
                    this.localEndpoint.onError(this, e);
                }
                this.webSocketContainer.unregisterSession(this.localEndpoint, this);
            }
        } catch (Throwable th) {
            this.webSocketContainer.unregisterSession(this.localEndpoint, this);
            throw th;
        }
    }

    protected static void appendCloseReasonWithTruncation(ByteBuffer msg, String reason) {
        byte[] reasonBytes = reason.getBytes(StandardCharsets.UTF_8);
        if (reasonBytes.length <= 123) {
            msg.put(reasonBytes);
            return;
        }
        int remaining = 123 - ELLIPSIS_BYTES_LEN;
        int pos = 0;
        byte[] bytes = reason.substring(0, 0 + 1).getBytes(StandardCharsets.UTF_8);
        while (true) {
            byte[] bytesNext = bytes;
            if (remaining >= bytesNext.length) {
                msg.put(bytesNext);
                remaining -= bytesNext.length;
                pos++;
                bytes = reason.substring(pos, pos + 1).getBytes(StandardCharsets.UTF_8);
            } else {
                msg.put(ELLIPSIS_BYTES);
                return;
            }
        }
    }

    public void registerFuture(FutureToSendHandler f2sh) {
        this.futures.put(f2sh, f2sh);
        if (this.state == State.OPEN || f2sh.isDone()) {
            return;
        }
        IOException ioe = new IOException(sm.getString("wsSession.messageFailed"));
        SendResult sr = new SendResult(ioe);
        f2sh.onResult(sr);
    }

    public void unregisterFuture(FutureToSendHandler f2sh) {
        this.futures.remove(f2sh);
    }

    @Override // javax.websocket.Session
    public URI getRequestURI() {
        checkState();
        return this.requestUri;
    }

    @Override // javax.websocket.Session
    public Map<String, List<String>> getRequestParameterMap() {
        checkState();
        return this.requestParameterMap;
    }

    @Override // javax.websocket.Session
    public String getQueryString() {
        checkState();
        return this.queryString;
    }

    @Override // javax.websocket.Session
    public Principal getUserPrincipal() {
        checkState();
        return this.userPrincipal;
    }

    @Override // javax.websocket.Session
    public Map<String, String> getPathParameters() {
        checkState();
        return this.pathParameters;
    }

    @Override // javax.websocket.Session
    public String getId() {
        return this.id;
    }

    @Override // javax.websocket.Session
    public Map<String, Object> getUserProperties() {
        checkState();
        return this.userProperties;
    }

    public Endpoint getLocal() {
        return this.localEndpoint;
    }

    public String getHttpSessionId() {
        return this.httpSessionId;
    }

    public MessageHandler getTextMessageHandler() {
        return this.textMessageHandler;
    }

    public MessageHandler getBinaryMessageHandler() {
        return this.binaryMessageHandler;
    }

    public MessageHandler.Whole<PongMessage> getPongMessageHandler() {
        return this.pongMessageHandler;
    }

    public void updateLastActive() {
        this.lastActive = System.currentTimeMillis();
    }

    public void checkExpiration() {
        long timeout = this.maxIdleTimeout;
        if (timeout >= 1 && System.currentTimeMillis() - this.lastActive > timeout) {
            String msg = sm.getString("wsSession.timeout", getId());
            if (this.log.isDebugEnabled()) {
                this.log.debug(msg);
            }
            doClose(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, msg), new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, msg));
        }
    }

    private void checkState() {
        if (this.state == State.CLOSED) {
            throw new IllegalStateException(sm.getString("wsSession.closed", this.id));
        }
    }

    public void setWsFrame(WsFrameBase wsFrame) {
        this.wsFrame = wsFrame;
    }

    public void suspend() {
        this.wsFrame.suspend();
    }

    public void resume() {
        this.wsFrame.resume();
    }
}